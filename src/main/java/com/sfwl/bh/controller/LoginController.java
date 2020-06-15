package com.sfwl.bh.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sfwl.bh.config.JWTUtil;
import com.sfwl.bh.config.PBKDF2Encoder;
import com.sfwl.bh.entity.User;
import com.sfwl.bh.entity.request.LoginModel;
import com.sfwl.bh.entity.response.BaseModel;
import com.sfwl.bh.enums.MessageTypeEnum;
import com.sfwl.bh.enums.ResultStatus;
import com.sfwl.bh.service.IUserService;
import com.sfwl.bh.service.impl.MessageCodeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * @author huhy
 * @version 1.0
 * @date 2020/5/11 13:14
 */

@RestController
public class LoginController {

    @Autowired
    private PBKDF2Encoder passwordEncoder;
    @Autowired
    private MessageCodeService messageCodeService;

    @Autowired
    private IUserService userService;

    @PostMapping(value = "/doLogin")
    public Mono<BaseModel<?>> login(@RequestBody LoginModel loginModel) {
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<User>()
                .eq(User::getAccount, loginModel.getAccount());
        User user = userService.getOne(lambdaQueryWrapper, false);
        if (Objects.isNull(user)) {
            return Mono.just(new BaseModel<>(ResultStatus.FAIL.getCode(), "用户不存在！"));
        }
        if (StringUtils.isNoneBlank(loginModel.getAccount(), loginModel.getPasswd())) {
            if (passwordEncoder.matches(loginModel.getPasswd(), user.getPasswd())) {
                return Mono.just(new BaseModel<>(ResultStatus.SUCCESS, JWTUtil.sign(String.valueOf(user.getId())), user));
            } else {
                return Mono.just(new BaseModel<>(ResultStatus.FAIL.getCode(), "密码不正确！"));
            }
        } else if (StringUtils.isNoneBlank(loginModel.getAccount(), loginModel.getMessageCode())) {
            if (messageCodeService.check(loginModel.getAccount(), MessageTypeEnum.LOGIN.getType(), loginModel.getMessageCode())) {
                messageCodeService.del(loginModel.getAccount(), MessageTypeEnum.LOGIN.getType());
                return Mono.just(new BaseModel<>(ResultStatus.SUCCESS, JWTUtil.sign(String.valueOf(user.getId())), user));
            } else {
                return Mono.just(new BaseModel<>(ResultStatus.FAIL.getCode(), "验证码不正确！"));
            }
        }
        return Mono.just(new BaseModel<>(ResultStatus.FAIL.getCode(), "参数错误！"));
    }

    @PostMapping(value = "/doRegister")
    public Mono<BaseModel<?>> register(@RequestBody LoginModel loginModel) {
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<User>()
                .eq(User::getAccount, loginModel.getAccount());
        User user = userService.getOne(lambdaQueryWrapper, false);
        if (!Objects.isNull(user)) {
            return Mono.just(new BaseModel<>(ResultStatus.FAIL.getCode(), "用户已存在！"));
        }
        if (messageCodeService.check(loginModel.getAccount(), MessageTypeEnum.REGISTER.getType(), loginModel.getMessageCode())) {
            messageCodeService.del(loginModel.getAccount(), MessageTypeEnum.REGISTER.getType());
        } else {
            return Mono.just(new BaseModel<>(ResultStatus.FAIL.getCode(), "验证码错误！"));
        }
        User newUser = new User();
        newUser.setAccount(loginModel.getAccount());
        newUser.setPasswd(passwordEncoder.encode(loginModel.getPasswd()));
        return Mono.just(userService.save(newUser) ? new BaseModel<>(ResultStatus.SUCCESS) : new BaseModel<>(ResultStatus.FAIL));
    }
}
