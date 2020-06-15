package com.sfwl.bh.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.gson.Gson;
import com.sfwl.bh.entity.*;
import com.sfwl.bh.entity.response.BaseModel;
import com.sfwl.bh.enums.RedisKeyEnum;
import com.sfwl.bh.enums.ResultStatus;
import com.sfwl.bh.service.IScriptService;
import com.sfwl.bh.service.IUserService;
import com.sfwl.bh.utils.BioFileUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author huhy
 * @since 2020-05-13
 */
@RestController
@RequestMapping("/script")
public class ScriptController {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private IUserService userService;
    @Autowired
    private IScriptService scriptService;

    private Gson gson = new Gson();

    @PostMapping("/info")
    public Mono<BaseModel<Map<String, Object>>> info(@RequestBody(required = false) BioFile file) {
        Machine machine = Machine.DEFAULT;
        FileConst fileConst = FileConst.DEFAULT;
        StepConst stepConst = StepConst.DEFAULT;
        BioFile script = null;

        // 若有设备ID，获取设备相关文件
        if (StringUtils.isNotBlank(file.getDeviceId())) {
            Boolean hasKey = redisTemplate.hasKey(String.format(RedisKeyEnum.DEVICE_MACHINE.getKey(), file.getDeviceId()));
            if (!Objects.isNull(hasKey) && hasKey) {
                machine = gson.fromJson(redisTemplate.opsForValue().get(String.format(RedisKeyEnum.DEVICE_MACHINE.getKey(), file.getDeviceId())), Machine.class);
                fileConst = gson.fromJson(redisTemplate.opsForValue().get(String.format(RedisKeyEnum.DEVICE_FILE_CONST.getKey(), file.getDeviceId())), FileConst.class);
                stepConst = gson.fromJson(redisTemplate.opsForValue().get(String.format(RedisKeyEnum.DEVICE_STEP_CONST.getKey(), file.getDeviceId())), StepConst.class);
            } else {
                return Mono.just(new BaseModel<>(ResultStatus.FAIL.getCode(), "设备离线！"));
            }
        }

        // 若上传文件，则转换文件，若无上传文件，返回空
        if (StringUtils.isNotBlank(file.getFileName())) {
            script = BioFileUtil.convertBioFile(file, machine, fileConst, stepConst);
        }

        // DeviceId 是否为 null
        Map<String, Object> result = new HashMap<>();
        result.put("machineClass", machine);
        result.put("fileConst", fileConst);
        result.put("stepConst", stepConst);
        result.put("script", script);
        return Mono.just(new BaseModel<>(ResultStatus.SUCCESS, result));
    }

    @PostMapping("/save")
    public Mono<BaseModel<String>> save(@RequestBody(required = false) BioFile file, Authentication authentication) {
        Long userId = userService.getUserIdByAuthentication(authentication);
        String fileName = file.getFileName();
        String data = gson.toJson(file);

        if (StringUtils.isBlank(file.getFileName())) {
            // 没有文件名，直接添加
            return Mono.just(saveScript(userId, fileName, data) ? new BaseModel<>(ResultStatus.SUCCESS) : new BaseModel<>(ResultStatus.FAIL));
        }

        LambdaQueryWrapper<Script> lambdaQueryWrapper = new LambdaQueryWrapper<Script>()
                .eq(Script::getUserId, userService.getUserIdByAuthentication(authentication))
                .eq(Script::getName, file.getFileName());
        Script script = scriptService.getOne(lambdaQueryWrapper, false);
        if (Objects.isNull(script)) {
            // 不存在同名文件，添加
            return Mono.just(saveScript(userId, fileName, data) ? new BaseModel<>(ResultStatus.SUCCESS) : new BaseModel<>(ResultStatus.FAIL));
        } else {
            // 存在同名文件，更新
            script.setData(data);
            return Mono.just(scriptService.updateById(script) ? new BaseModel<>(ResultStatus.SUCCESS) : new BaseModel<>(ResultStatus.FAIL));
        }
    }

    private boolean saveScript(Long userId, String fileName, String data) {
        Script script = new Script();
        script.setUserId(userId);
        script.setName(fileName);
        script.setData(data);
        return scriptService.save(script);
    }

    @GetMapping("/get")
    public Mono<BaseModel<BioFile>> get(@RequestParam Long id) {
        Script script = scriptService.getById(id);
        if (Objects.isNull(script)) {
            return Mono.just(new BaseModel<>(ResultStatus.FAIL.getCode(), "文件不存在!"));
        }
        return Mono.just(new BaseModel<>(ResultStatus.SUCCESS, gson.fromJson(script.getData(), BioFile.class)));
    }

    @GetMapping("/list")
    public Mono<BaseModel<List<Script>>> list() {
        LambdaQueryWrapper<Script> lambdaQueryWrapper = new LambdaQueryWrapper<Script>()
                .select(Script::getId, Script::getName, Script::getUserId);
        return Mono.just(new BaseModel<>(ResultStatus.SUCCESS, scriptService.list(lambdaQueryWrapper)));
    }

    @PostMapping("/delete")
    public Mono<BaseModel<String>> add(@RequestBody(required = false) Script script) {
        return Mono.just(scriptService.removeById(script.getId()) ? new BaseModel<>(ResultStatus.SUCCESS) : new BaseModel<>(ResultStatus.FAIL));
    }
}
