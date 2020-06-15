package com.sfwl.bh.service;

import com.sfwl.bh.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.security.core.Authentication;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author huhy
 * @since 2020-05-09
 */
public interface IUserService extends IService<User> {

    Long getUserIdByAuthentication(Authentication authentication);
}
