package com.sfwl.bh.entity;

import com.sfwl.bh.enums.FileType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author huhy
 * @version 1.0
 * @date 2020/5/8 16:42
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class FileConst {

    private FileType tdFileSize; // 文件类型
    private Integer stepSize; // 不展开最大步骤数
    private Integer stepAnalySize; // 展开最大步骤数
    private Long stepAnalyTime; // 解析后最大时间

    private Float minLid;// 热盖
    private Float maxLid;
    private Float defaultLid; // 默认热盖

    private Integer minVolume;// 试剂量
    private Integer maxVolume;
    private Integer defaultVolume; // 默认剂量

    public static final FileConst DEFAULT = new FileConst();

    static {
        DEFAULT.tdFileSize = FileType.Size686;
        DEFAULT.stepSize = 30; // 不展开最大步骤数
        DEFAULT.stepAnalySize = 1000; // 展开最大步骤数
        DEFAULT.stepAnalyTime = 50L * 60L * 60L * 1000L; // 解析后最大时间

        DEFAULT.minLid = 30f;// 热盖
        DEFAULT.maxLid = 115f;
        DEFAULT.defaultLid = 105f; // 默认热盖

        DEFAULT.minVolume = 1;// 试剂量
        DEFAULT.maxVolume = 100;
        DEFAULT.defaultVolume = 10; // 默认剂量
    }
}
