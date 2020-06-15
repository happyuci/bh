package com.sfwl.bh.entity.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author huhy
 * @version 1.0
 * @date 2020/5/12 10:43
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UserInfoChangeModel {

    private String oldPasswd;
    
    private String newPasswd;

    private String account;
    private String messageCode;

    private String oldAccount;
    private String oldMessageCode;
    private String newAccount;
    private String newMessageCode;
}
