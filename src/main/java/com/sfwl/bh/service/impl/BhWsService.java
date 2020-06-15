package com.sfwl.bh.service.impl;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sfwl.bh.entity.*;
import com.sfwl.bh.enums.RedisKeyEnum;
import com.sfwl.bh.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.UnicastProcessor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author huhy
 * @version 1.0
 * @date 2020/5/18 9:50
 */
@Slf4j
@Component
public class BhWsService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    // 分布式服务需要分布式锁
    private static final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    //    private final ConcurrentMap<String, WebSocketSession> wsSessionMap = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, UnicastProcessor<String>> processorMap = new ConcurrentHashMap<>();

    private Gson gson = new Gson();

    public void handleSubscribe(String sessionId, UnicastProcessor<String> directProcessor) {
        log.info("Subscribe {}", sessionId);
//        wsSessionMap.putIfAbsent(webSocketSession.getId(), webSocketSession);
//        UnicastProcessor<String> directProcessor = UnicastProcessor.create();
        processorMap.putIfAbsent(sessionId, directProcessor);
    }

    @Deprecated
    public UnicastProcessor<String> getUnicastProcessor(String sessionId) {
        return processorMap.get(sessionId);
    }

    public Map<String, UnicastProcessor<String>> getUnicastProcessorMap() {
        return processorMap;
    }

    public boolean sendMessage(String sessionId, String message) {
        if (processorMap.containsKey(sessionId)) {
            log.info("Send {} {}", sessionId, message);
            processorMap.get(sessionId).onNext(message);
            return true;
        }
        return false;
    }

    public void handleMessage(String sessionId, String message) {
        log.info("Receive {} {}", sessionId, message);
        JsonObject root = JsonParser.parseString(message).getAsJsonObject();
        String action = root.get("action").getAsString();

        readWriteLock.writeLock().lock();
        try {
            switch (action) {
                case "init":
                    handleInit(sessionId, root);
                    break;
                case "read":
                    handleRead(sessionId, root);
                    break;
                default:
                    log.info("收到消息:" + message);
                    break;
            }
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    private void handleInit(String sessionId, JsonObject root) {
        JsonObject targetData = root.get("target-data").getAsJsonObject();
        Machine machine = gson.fromJson(targetData.get("MachineClass").getAsJsonObject(), Machine.class);
        redisTemplate.opsForValue().set(String.format(RedisKeyEnum.DEVICE_MACHINE.getKey(), sessionId), gson.toJson(machine));
        FileConst fileConst = gson.fromJson(targetData.get("tdFileConst").getAsJsonObject(), FileConst.class);
        redisTemplate.opsForValue().set(String.format(RedisKeyEnum.DEVICE_FILE_CONST.getKey(), sessionId), gson.toJson(fileConst));
        StepConst stepConst = gson.fromJson(targetData.get("StepConst").getAsJsonObject(), StepConst.class);
        redisTemplate.opsForValue().set(String.format(RedisKeyEnum.DEVICE_STEP_CONST.getKey(), sessionId), gson.toJson(stepConst));
        String machineName = targetData.get("MachineName").getAsString();
        String machineModel = targetData.get("MachineModel").getAsString();
        for (int i = 0; i < machine.getBlockNum(); i++) {
            DeviceInfo deviceInfo = new DeviceInfo();
            deviceInfo.setMachineName(machineName);
            deviceInfo.setDeviceId(sessionId);
            deviceInfo.setMachineModel(machineModel);
            deviceInfo.setBlockName((char) (65 + i) + "");
            redisTemplate.opsForValue().set(String.format(RedisKeyEnum.DEVICE_INFO.getKey(), sessionId, deviceInfo.getBlockName()), gson.toJson(deviceInfo));
        }
        redisTemplate.opsForValue().set(String.format(RedisKeyEnum.DEVICE_BIND.getKey(), sessionId, 1), sessionId);
    }

    public void handleRead(String sessionId, JsonObject root) {
        String code = root.get("code").getAsString();
        JsonObject targetData = root.get("target-data").getAsJsonObject();
        String blockName = code.charAt(code.length() - 1) + "";
        if (code.startsWith("status")) {
            DeviceStatus deviceStatus = new DeviceStatus();
            deviceStatus.setBlockTemp(targetData.get("block-temp").getAsString());
            deviceStatus.setCurrentStep(Integer.valueOf(targetData.get("current-step").getAsString()));
            deviceStatus.setFileModify(targetData.get("file-modify").getAsBoolean());
            deviceStatus.setFileName(targetData.get("file-name").getAsString());
            deviceStatus.setLidTemp(targetData.get("lid-temp").getAsString());
            List<Integer> loopNum = new ArrayList<>();
            for (JsonElement jsonElement : targetData.get("loop-num").getAsJsonArray()) {
                loopNum.add(jsonElement.getAsInt());
            }
            deviceStatus.setLoopNum(loopNum);
            deviceStatus.setProgress(targetData.get("progress").getAsString());
            String remainTimeStr = targetData.get("remain-time").getAsString().replace("小时", "").replace("分钟", "");
            try {
                if (!"∞".equals(remainTimeStr)) {
                    String[] array = remainTimeStr.split(" ");
                    deviceStatus.setRemainTime((Long.parseLong(array[0]) * 3600 + Long.parseLong(array[1]) * 60) * 1000);
                }
            } catch (Exception e) {
                log.error("剩余时间解析异常！", e);
            }
            try {
                deviceStatus.setStartTime(new Date(DateUtil.parseDate(targetData.get("start-time").getAsString(), "HH:mm:ss").getTime() + DateUtil.dayBegin(new Date()).getTime() + 8 * 3600 * 1000));
            } catch (Exception e) {
                log.error("时间转换异常！", e);
            }
            deviceStatus.setStatus(targetData.get("status").getAsString());
            try {
                deviceStatus.setStepTime(Objects.requireNonNull(DateUtil.parseDate(targetData.get("step-time").getAsString(), "HH:mm:ss")).getTime() + 8 * 3600 * 1000);
            } catch (Exception e) {
                log.error("时间转换异常！", e);
            }
            deviceStatus.setSystemErr(targetData.get("system-err").getAsString());
            deviceStatus.setBlockName(blockName);
            DeviceInfo deviceInfo = gson.fromJson(redisTemplate.opsForValue().get(String.format(RedisKeyEnum.DEVICE_INFO.getKey(), sessionId, blockName)), DeviceInfo.class);
            deviceStatus.setDeviceId(sessionId);
            deviceStatus.setMachineName(deviceInfo.getMachineName());
            deviceStatus.setMachineModel(deviceInfo.getMachineModel());
            deviceStatus.setBlockName(blockName);
            redisTemplate.opsForValue().set(String.format(RedisKeyEnum.DEVICE_STATUS.getKey(), sessionId, blockName), gson.toJson(deviceStatus));
        } else if (code.startsWith("file")) {
            BioFile bioFile = gson.fromJson(targetData, BioFile.class);
            redisTemplate.opsForValue().set(String.format(RedisKeyEnum.DEVICE_FILE.getKey(), sessionId, blockName), gson.toJson(bioFile));
        }

    }

    public void handleTerminate(String sessionId) {
        log.info("Terminate {}", sessionId);
        readWriteLock.writeLock().lock();
        try {
            Collection<String> keys = redisTemplate.keys("DEVICE_" + sessionId + "*");
            log.info("keys" + keys);
            if (!Objects.isNull(keys)) {
                keys.forEach(key -> redisTemplate.delete(key));
            }
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }
}
