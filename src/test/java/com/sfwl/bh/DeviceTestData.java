package com.sfwl.bh;

import com.google.gson.Gson;
import com.sfwl.bh.entity.*;
import com.sfwl.bh.enums.RedisKeyEnum;
import com.sfwl.bh.enums.StepType;
import com.sfwl.bh.utils.RandomStrUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

/**
 * @author huhy
 * @version 1.0
 * @date 2019/8/13 11:47
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DeviceTestData {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private Gson gson = new Gson();

    @Test
    public void test() {
        for (int i = 0; i < 5; i++) {
            String deviceId = RandomStrUtil.getRandomStringByLength(6);
            Machine machine = Machine.DEFAULT;
            machine.setBlockNum(new Random().nextInt(2) + 1);
            redisTemplate.opsForValue().set(String.format(RedisKeyEnum.DEVICE_MACHINE.getKey(), deviceId), gson.toJson(machine));

            String machineName = RandomStrUtil.getRandomStringByLength(6);
            for (int j = 0; j < machine.getBlockNum(); j++) {
                DeviceInfo deviceInfo = new DeviceInfo();
                deviceInfo.setMachineName(machineName);
                deviceInfo.setDeviceId(deviceId);
                deviceInfo.setMachineModel("RePure-A");
                deviceInfo.setBlockName((char) (65 + j) + "");
                redisTemplate.opsForValue().set(String.format(RedisKeyEnum.DEVICE_INFO.getKey(), deviceId, deviceInfo.getBlockName()), gson.toJson(deviceInfo));

                DeviceStatus deviceStatus = new DeviceStatus();
                deviceStatus.setMachineName(deviceInfo.getMachineName());
                deviceStatus.setDeviceId(deviceInfo.getDeviceId());
                deviceStatus.setMachineModel(deviceInfo.getMachineModel());
                deviceStatus.setBlockName(deviceInfo.getBlockName());
                deviceStatus.setCurrentStep(1);
                deviceStatus.setFileModify(false);
                deviceStatus.setFileName("ST");
                deviceStatus.setLidTemp("30℃");
                deviceStatus.setLoopNum(Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
                deviceStatus.setProgress("0 %");
                deviceStatus.setRemainTime(60000L);
                deviceStatus.setStartTime(new Date());
                deviceStatus.setStatus("停止");
                deviceStatus.setStepTime(60000L);
                deviceStatus.setSystemErr("");
                redisTemplate.opsForValue().set(String.format(RedisKeyEnum.DEVICE_STATUS.getKey(), deviceId, deviceInfo.getBlockName()), gson.toJson(deviceStatus));

                BioFile bioFile = new BioFile();
                bioFile.setFileName("ST");
                List<Step> stepList = new ArrayList<>(6);
                stepList.add(new Step());
                stepList.add(new Step());
                stepList.add(new Step());
                stepList.add(new Step());
                stepList.add(new Step());
                Step step = new Step();
                step.setFTempCycSel(StepType.STEPitem);
                step.setFTargetStep(3);
                step.setFCycNum(5);
                stepList.add(step);
                bioFile.setSteplist(stepList);
                redisTemplate.opsForValue().set(String.format(RedisKeyEnum.DEVICE_FILE.getKey(), deviceId, deviceInfo.getBlockName()), gson.toJson(bioFile));
            }

            redisTemplate.opsForValue().set(String.format(RedisKeyEnum.DEVICE_FILE_CONST.getKey(), deviceId), gson.toJson(FileConst.DEFAULT));

            redisTemplate.opsForValue().set(String.format(RedisKeyEnum.DEVICE_STEP_CONST.getKey(), deviceId), gson.toJson(StepConst.DEFAULT));

            redisTemplate.opsForValue().set(String.format(RedisKeyEnum.DEVICE_BIND.getKey(), deviceId, 2), deviceId);
        }
    }

    @Test
    public void testB() {
        Gson gson = new Gson();
        String key = String.format(RedisKeyEnum.DEVICE_STATUS.getKey(), "vGijcR", "A");
        String json = redisTemplate.opsForValue().get(key);
        DeviceStatus deviceStatus = new Gson().fromJson(json, DeviceStatus.class);
        deviceStatus.setRemainTime(59000L);
//        deviceStatus.setSystemErr("错误信息!");
        redisTemplate.opsForValue().set(key, gson.toJson(deviceStatus));
        System.out.println(deviceStatus.getMachineName());
    }
}
