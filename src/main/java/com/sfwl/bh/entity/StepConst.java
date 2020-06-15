package com.sfwl.bh.entity;

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
public class StepConst {

    // 循环目标
    private Integer minTargetStep;
    private Integer maxTargetStep;
    private Integer defaultTargetStep;

    // 循环次数
    private Integer minCycNum;
    private Integer maxCycNum;
    private Integer defaultCycNum;

    // 温度
    private Float minTemp;
    private Float maxTemp;
    private Float defaultTemp;

    // 梯度范围
    private Float minLeftRightGradRange;
    private Float maxLeftRightGradRange;

    private Float minUpDownGradRange;
    private Float maxUpDownGradRange;

    // 最低温度大于该值生效
    private Float startGradRange; // 梯度的起点
    private Float maxDynamicTemp; // 最大相邻温度

    // 时间
    private Long minTime;
    private Long maxTime;
    private Long defaultTime; // 默认温度

    // 增加温度
    private Float minIncTemp;
    private Float maxIncTemp;

    // 增加时间
    private Long extTimeRange; // 正负共用一个

    // 变温速率
    private Float minRampRate;
    private Float maxRampRate;

    // 增量起点
    private Integer minOpenPos;
    private Integer maxOpenPos;

    public static final StepConst DEFAULT = new StepConst();
    static {
        DEFAULT.minTargetStep = 1;
        DEFAULT.maxTargetStep = 30 - 1;
        DEFAULT.defaultTargetStep = 3;

        DEFAULT.minCycNum = 1;
        DEFAULT.maxCycNum = 199;
        DEFAULT.defaultCycNum = 29;

        DEFAULT.minTemp = 0f;
        DEFAULT.maxTemp = 105f;
        DEFAULT.defaultTemp = 72f;

        DEFAULT.minLeftRightGradRange = 0f;
        DEFAULT.maxLeftRightGradRange = 42f;

        DEFAULT.minUpDownGradRange = 0f;
        DEFAULT.maxUpDownGradRange = 24f;

        DEFAULT.startGradRange = 30f;
        DEFAULT.maxDynamicTemp = 5f;

        DEFAULT.minTime = 0L;
        DEFAULT.maxTime = 18L * 60L * 60L * 1000L;
        DEFAULT.defaultTime = 30L * 1000L;

        DEFAULT.minIncTemp = -10f;
        DEFAULT.maxIncTemp = 10f;

        DEFAULT.extTimeRange = 10L * 60L * 1000L;

        DEFAULT.minRampRate = 0f;
        DEFAULT.maxRampRate = 5f;

        DEFAULT.minOpenPos = 1;
        DEFAULT.maxOpenPos = 199;
    }
}
