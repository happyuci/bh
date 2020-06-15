package com.sfwl.bh.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * @author huhy
 * @version 1.0
 * @date 2020/5/13 9:34
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class DeviceStatus {

    private String machineName;
    private String machineModel;
    private String blockName;
    private String deviceId;

    private String blockTemp;
    private Integer currentStep;
    private Boolean fileModify;
    private String fileName;
    private String lidTemp;
    private List<Integer> loopNum;
    private String progress;
    private Long remainTime;
    private Date startTime;
    private String status;
    private Long stepTime;
    private String systemErr;
}
