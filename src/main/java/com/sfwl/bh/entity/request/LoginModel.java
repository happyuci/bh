package com.sfwl.bh.entity.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class LoginModel {

    private String account;
    private String passwd;
    private String messageCode;
}
