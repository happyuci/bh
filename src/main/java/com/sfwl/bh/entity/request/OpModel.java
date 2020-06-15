package com.sfwl.bh.entity.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author huhy
 * @version 1.0
 * @date 2020/5/18 15:06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class OpModel<T> {

    private String deviceId;
    private String blockName;
    private T data;
}
