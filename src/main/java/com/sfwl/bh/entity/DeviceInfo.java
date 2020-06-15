package com.sfwl.bh.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author huhy
 * @version 1.0
 * @date 2020/5/13 9:34
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class DeviceInfo {

    private String machineName;
    private String machineModel;
    private String blockName;
    private String deviceId;
}
