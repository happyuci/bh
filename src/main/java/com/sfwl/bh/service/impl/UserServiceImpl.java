package com.sfwl.bh.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sfwl.bh.config.JWTUtil;
import com.sfwl.bh.entity.User;
import com.sfwl.bh.mapper.UserMapper;
import com.sfwl.bh.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author huhy
 * @since 2020-05-09
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Override
    public Long getUserIdByAuthentication(Authentication authentication) {
        String userIdString = JWTUtil.getUserIdString(authentication.getCredentials().toString());
        return StringUtils.isBlank(userIdString) ? null : Long.parseLong(userIdString);
    }
}
