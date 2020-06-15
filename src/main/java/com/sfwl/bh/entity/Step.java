package com.sfwl.bh.entity;

import com.sfwl.bh.enums.DirectionType;
import com.sfwl.bh.enums.PlusMinusType;
import com.sfwl.bh.enums.StepType;
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
public class Step {

    private StepType fTempCycSel; // 温度步、循环步

    private Integer fTargetStep; // 循环目标
    private Integer fCycNum; // 循环数

    // 0    2    4
    // 1    3    5
    private DirectionType fDirection; // 动态方向
    private Float[] fSetTemp; // 温度
    private Float fGradRange; // 梯度范围0-N或-1
    private Long fSetTime; // 时间
    private Float fIncTemp; // 增加温度
    private int fIncTempOpen; // 开启位置
    private PlusMinusType fIncTempPlusMinus; // 增加时间是正还是负
    private Long fExtTime; // 增加时间
    private int fExtTimeOpen; // 开启位置
    private PlusMinusType plusMinus; // 增加时间是正还是负
    private Float fRampRate; // 变温速率

    private Boolean checked;

    public Step() {
        fDirection = DirectionType.LeftRight;
        fTempCycSel = StepType.STEPitem;

        fTargetStep = 1;
        fCycNum = 0;

        fSetTemp = new Float[6];
        fSetTime = 0L;
        plusMinus = PlusMinusType.plus;
        fGradRange = 0f;
        fIncTemp = 0f;
        fIncTempOpen = 0;
        fIncTempPlusMinus = PlusMinusType.plus;
        fExtTime = 0L;
        fExtTimeOpen = 0;
        fRampRate = 0f;
    }
}
