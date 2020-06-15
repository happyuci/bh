package com.sfwl.bh.controller;

import com.google.gson.Gson;
import com.sfwl.bh.entity.BioFile;
import com.sfwl.bh.entity.DeviceInfo;
import com.sfwl.bh.entity.DeviceStatus;
import com.sfwl.bh.entity.request.OpModel;
import com.sfwl.bh.entity.response.BaseModel;
import com.sfwl.bh.enums.RedisKeyEnum;
import com.sfwl.bh.enums.ResultStatus;
import com.sfwl.bh.service.IUserService;
import com.sfwl.bh.service.impl.BhWsService;
import com.sfwl.bh.utils.WsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

/**
 * @author huhy
 * @version 1.0
 * @date 2020/5/13 10:26
 */
@RestController
@RequestMapping("/device")
public class DeviceController {

    @Autowired
    private ReactiveRedisTemplate<String, String> reactiveRedisTemplate;
    @Autowired
    private BhWsService bhWsService;

    @Autowired
    private IUserService userService;

    private Gson gson = new Gson();

    @GetMapping(value = "/list")
    public Mono<BaseModel<List<DeviceInfo>>> list(Authentication authentication) {
        return Flux.just(userService.getUserIdByAuthentication(authentication))
                .flatMap(userId -> reactiveRedisTemplate.keys(String.format(RedisKeyEnum.DEVICE_BIND.getKey(), "*", userId)))
                .flatMap(bindKey -> reactiveRedisTemplate.opsForValue().get(bindKey))
                .flatMap(deviceId -> reactiveRedisTemplate.keys(String.format(RedisKeyEnum.DEVICE_INFO.getKey(), deviceId, "*")))
                .flatMap(deviceInfoKey -> reactiveRedisTemplate.opsForValue().get(deviceInfoKey))
                .map(json -> gson.fromJson(json, DeviceInfo.class)).collectList()
                .map(list -> new BaseModel<>(ResultStatus.SUCCESS, list));
    }

    @GetMapping("/info")
    public Mono<BaseModel<DeviceStatus>> info(@RequestParam String deviceId, @RequestParam String blockName) {
        return reactiveRedisTemplate.opsForValue().get(String.format(RedisKeyEnum.DEVICE_STATUS.getKey(), deviceId, blockName))
                .map(json -> gson.fromJson(json, DeviceStatus.class))
                .map(deviceStatus -> new BaseModel<>(ResultStatus.SUCCESS, deviceStatus));
    }

    @GetMapping("/file")
    public Mono<BaseModel<BioFile>> file(@RequestParam String deviceId, @RequestParam String blockName) {
        return reactiveRedisTemplate.opsForValue().get(String.format(RedisKeyEnum.DEVICE_FILE.getKey(), deviceId, blockName))
                .map(json -> gson.fromJson(json, BioFile.class))
                .map(bioFle -> new BaseModel<>(ResultStatus.SUCCESS, bioFle));
    }

    @PostMapping("/runFile")
    public Mono<BaseModel<String>> runFile(@RequestBody(required = false) OpModel<BioFile> opModel) {
        return Mono.just(bhWsService.sendMessage(opModel.getDeviceId(), WsUtil.getRunFile(opModel.getBlockName(), opModel.getData()))
                ? new BaseModel<>(ResultStatus.SUCCESS) : new BaseModel<>(ResultStatus.FAIL.getCode(), "设备离线！"));
    }

    @PostMapping("/pause")
    public Mono<BaseModel<String>> pause(@RequestBody(required = false) OpModel<?> opModel) {
        return Mono.just(bhWsService.sendMessage(opModel.getDeviceId(), WsUtil.getPauseData(opModel.getBlockName()))
                ? new BaseModel<>(ResultStatus.SUCCESS) : new BaseModel<>(ResultStatus.FAIL.getCode(), "设备离线！"));
    }

    @PostMapping("/rewind")
    public Mono<BaseModel<String>> rewind(@RequestBody(required = false) OpModel<?> opModel) {
        return Mono.just(bhWsService.sendMessage(opModel.getDeviceId(), WsUtil.getRewindData(opModel.getBlockName()))
                ? new BaseModel<>(ResultStatus.SUCCESS) : new BaseModel<>(ResultStatus.FAIL.getCode(), "设备离线！"));
    }

    @PostMapping("/stop")
    public Mono<BaseModel<String>> stop(@RequestBody(required = false) OpModel<?> opModel) {
        return Mono.just(bhWsService.sendMessage(opModel.getDeviceId(), WsUtil.getStopData(opModel.getBlockName()))
                ? new BaseModel<>(ResultStatus.SUCCESS) : new BaseModel<>(ResultStatus.FAIL.getCode(), "设备离线！"));
    }

    @PostMapping("/skip")
    public Mono<BaseModel<String>> skip(@RequestBody(required = false) OpModel<?> opModel) {
        return Mono.just(bhWsService.sendMessage(opModel.getDeviceId(), WsUtil.getSkipData(opModel.getBlockName()))
                ? new BaseModel<>(ResultStatus.SUCCESS) : new BaseModel<>(ResultStatus.FAIL.getCode(), "设备离线！"));
    }


    @GetMapping(value = "/infoF", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<BaseModel<DeviceStatus>> infoF(@RequestParam String deviceId, @RequestParam String blockName) {
        return Flux.interval(Duration.ofMillis(500))
                .map(l -> String.format(RedisKeyEnum.DEVICE_STATUS.getKey(), deviceId, blockName))
                .flatMap(reactiveRedisTemplate.opsForValue()::get)
                .map(json -> gson.fromJson(json, DeviceStatus.class))
                .map(deviceStatus -> new BaseModel<>(ResultStatus.SUCCESS, deviceStatus));
    }
}
