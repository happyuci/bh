package com.sfwl.bh.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * @author huhy
 * @version 1.0
 * @date 2020/5/8 16:42
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BioFile {
    // 用户文件转换 TODO
    private String deviceId;
    private String blockName;

    private String fileName; // 文件名

    private Boolean flidBool; // 热盖开关
    private Float flid; // 热盖温度
    private Integer fVolume; // 试剂量

    private List<Step> steplist; // 步骤的集合
}
