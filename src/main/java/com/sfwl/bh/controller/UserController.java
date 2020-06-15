package com.sfwl.bh.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sfwl.bh.config.PBKDF2Encoder;
import com.sfwl.bh.entity.User;
import com.sfwl.bh.entity.request.UserInfoChangeModel;
import com.sfwl.bh.entity.response.BaseModel;
import com.sfwl.bh.enums.MessageTypeEnum;
import com.sfwl.bh.enums.ResultStatus;
import com.sfwl.bh.service.IUserService;
import com.sfwl.bh.service.impl.MessageCodeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author huhy
 * @since 2020-05-09
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private PBKDF2Encoder passwordEncoder;
    @Autowired
    private MessageCodeService messageCodeService;

    @Autowired
    private IUserService userService;

    @PostMapping("/update")
    public Mono<BaseModel<String>> update(@RequestBody User user, Authentication authentication) {
        User update = new User();
        update.setId(userService.getUserIdByAuthentication(authentication));
        update.setName(user.getName());
        update.setCompany(user.getCompany());
        return Mono.just(userService.updateById(update) ? new BaseModel<>(ResultStatus.SUCCESS.getCode(), "更新成功！") : new BaseModel<>(ResultStatus.FAIL.getCode(), "更新失败！"));
    }

    @PostMapping("/changePasswd")
    public Mono<BaseModel<String>> changePasswd(@RequestBody UserInfoChangeModel userInfoChangeModel, Authentication authentication) {
        if (StringUtils.isAnyBlank(userInfoChangeModel.getNewPasswd(), userInfoChangeModel.getOldPasswd())) {
            return Mono.just(new BaseModel<>(ResultStatus.FAIL.getCode(), "缺少参数！"));
        }
        User user = userService.getById(userService.getUserIdByAuthentication(authentication));
        if (!passwordEncoder.matches(userInfoChangeModel.getOldPasswd(), user.getPasswd())) {
            return Mono.just(new BaseModel<>(ResultStatus.FAIL.getCode(), "密码不正确！"));
        }
        user.setPasswd(passwordEncoder.encode(userInfoChangeModel.getNewPasswd()));
        return Mono.just(userService.updateById(user) ? new BaseModel<>(ResultStatus.SUCCESS.getCode(), "修改成功！") : new BaseModel<>(ResultStatus.FAIL.getCode(), "修改失败！"));
    }

    @PostMapping("/resetPasswd")
    public Mono<BaseModel<String>> resetPasswd(@RequestBody UserInfoChangeModel userInfoChangeModel) {
        if (StringUtils.isAnyBlank(userInfoChangeModel.getAccount(), userInfoChangeModel.getNewPasswd(), userInfoChangeModel.getMessageCode())) {
            return Mono.just(new BaseModel<>(ResultStatus.FAIL.getCode(), "缺少参数！"));
        }
        if (!messageCodeService.check(userInfoChangeModel.getAccount(), MessageTypeEnum.PWD_CHANGE.getType(), userInfoChangeModel.getMessageCode())) {
            return Mono.just(new BaseModel<>(ResultStatus.FAIL.getCode(), "验证码错误！"));
        }
        messageCodeService.del(userInfoChangeModel.getAccount(), MessageTypeEnum.PWD_CHANGE.getType());

        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<User>()
                .eq(User::getAccount, userInfoChangeModel.getAccount());
        User user = userService.getOne(lambdaQueryWrapper, false);
        if (Objects.isNull(user)) {
            return Mono.just(new BaseModel<>(ResultStatus.FAIL.getCode(), "用户不存在！"));
        }

        user.setPasswd(passwordEncoder.encode(userInfoChangeModel.getNewPasswd()));
        return Mono.just(userService.updateById(user) ? new BaseModel<>(ResultStatus.SUCCESS.getCode(), "修改成功！") : new BaseModel<>(ResultStatus.FAIL.getCode(), "修改失败！"));
    }

    @PostMapping("/mobileChange")
    public Mono<BaseModel<String>> mobileChange(@RequestBody UserInfoChangeModel userInfoChangeModel, Authentication authentication) {
        if (StringUtils.isAnyBlank(userInfoChangeModel.getNewAccount(), userInfoChangeModel.getNewMessageCode(), userInfoChangeModel.getOldAccount(), userInfoChangeModel.getOldMessageCode())) {
            return Mono.just(new BaseModel<>(ResultStatus.FAIL.getCode(), "缺少参数！"));
        }

        if (!messageCodeService.check(userInfoChangeModel.getOldAccount(), MessageTypeEnum.ORIGIN_MOBILE.getType(), userInfoChangeModel.getOldMessageCode())) {
            return Mono.just(new BaseModel<>(ResultStatus.FAIL.getCode(), "验证码错误！"));
        }
        messageCodeService.del(userInfoChangeModel.getOldAccount(), MessageTypeEnum.ORIGIN_MOBILE.getType());

        if (!messageCodeService.check(userInfoChangeModel.getNewAccount(), MessageTypeEnum.NEW_MOBILE.getType(), userInfoChangeModel.getNewMessageCode())) {
            return Mono.just(new BaseModel<>(ResultStatus.FAIL.getCode(), "验证码错误！"));
        }
        messageCodeService.del(userInfoChangeModel.getNewAccount(), MessageTypeEnum.NEW_MOBILE.getType());

        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<User>()
                .eq(User::getAccount, userInfoChangeModel.getNewAccount());
        User user = userService.getOne(lambdaQueryWrapper, false);
        if (!Objects.isNull(user)) {
            return Mono.just(new BaseModel<>(ResultStatus.FAIL.getCode(), "用户已存在！"));
        }

        User update = userService.getById(userService.getUserIdByAuthentication(authentication));
        if (!update.getAccount().equals(userInfoChangeModel.getOldAccount())) {
            return Mono.just(new BaseModel<>(ResultStatus.FAIL.getCode(), "非法账号！"));
        }
        update.setAccount(userInfoChangeModel.getNewAccount());
        return Mono.just(userService.updateById(update) ? new BaseModel<>(ResultStatus.SUCCESS.getCode(), "修改成功！") : new BaseModel<>(ResultStatus.FAIL.getCode(), "修改失败！"));
    }
}
