package com.sfwl.bh.utils;

import com.sfwl.bh.entity.*;
import com.sfwl.bh.enums.DirectionType;
import com.sfwl.bh.enums.GradType;
import com.sfwl.bh.enums.PlusMinusType;
import com.sfwl.bh.enums.StepType;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author huhy
 * @version 1.0
 * @date 2020/5/13 17:25
 */
@Slf4j
public class BioFileUtil {

    // BioFile 转换
    public static BioFile convertBioFile(BioFile originFile, Machine machine, FileConst fileConst, StepConst stepConst) {
        BioFile bioFile = new BioFile();
        bioFile.setFileName(originFile.getFileName()); // 文件名称
        bioFile.setFlidBool(originFile.getFlidBool()); // 热盖开关

        bioFile.setFlid(getAppropriateValue(originFile.getFlid(), fileConst.getMinLid(), fileConst.getMaxLid())); // 热盖温度
        bioFile.setFVolume(getAppropriateValue(originFile.getFVolume(), fileConst.getMinVolume(), fileConst.getMaxVolume())); // 试剂量

        int stepSize = Math.min(originFile.getSteplist().size(), stepConst.getMaxCycNum());
        List<Step> stepList = new ArrayList<>(stepSize);
        bioFile.setSteplist(stepList);
        for (int i = 0; i < stepSize; i++) {
            Step originStep = originFile.getSteplist().get(i);
            Step step = new Step();
            step.setFTempCycSel(originStep.getFTempCycSel()); // 温度步，循环步
            if (StepType.STEPitem.equals(step.getFTempCycSel())) {
                step.setFTargetStep(originStep.getFTargetStep()); // 循环目标
                step.setFCycNum(getAppropriateValue(originStep.getFCycNum(), stepConst.getMinCycNum(), stepConst.getMaxCycNum())); // 循环次数
            } else if (StepType.LOOPitem.equals(step.getFTempCycSel())) {
                if (GradType.General.equals(machine.getGradType())) {
                    step.setFDirection(DirectionType.LeftRight);
                    step.setFGradRange(getAppropriateValue(originStep.getFGradRange(), stepConst.getMinLeftRightGradRange(), stepConst.getMinLeftRightGradRange()));
                    Float[] fSetTemp = originStep.getFSetTemp();
                    float minTmp = Math.max(fSetTemp[0], stepConst.getMinTemp());
                    float maxTmp = Math.min(fSetTemp[0] + step.getFGradRange(), stepConst.getMaxTemp());
                    fSetTemp[4] = fSetTemp[2] = fSetTemp[0] = minTmp;
                    fSetTemp[5] = fSetTemp[3] = fSetTemp[1] = maxTmp;
                    step.setFSetTemp(fSetTemp);
                } else if (GradType.Dynamic.equals(machine.getGradType())) {
                    Float[] fSetTemp = originStep.getFSetTemp();
                    fSetTemp[0] = getAppropriateValue(fSetTemp[0], stepConst.getMinTemp(), stepConst.getMaxTemp());
                    for (int j = 1; j < fSetTemp.length; j++) {
                        fSetTemp[j] = getAppropriateValue(fSetTemp[j], fSetTemp[j - 1], stepConst.getMinTemp(), stepConst.getMaxTemp());
                    }
                    step.setFSetTemp(fSetTemp);
                } else if (GradType.twoDimens.equals(machine.getGradType())) {
                    step.setFDirection(Objects.isNull(originStep.getFDirection()) ? DirectionType.LeftRight : originStep.getFDirection());
                    if (DirectionType.LeftRight.equals(step.getFDirection())) {
                        step.setFGradRange(getAppropriateValue(originStep.getFGradRange(), stepConst.getMinLeftRightGradRange(), stepConst.getMinLeftRightGradRange()));
                        Float[] fSetTemp = originStep.getFSetTemp();
                        float minTmp = Math.max(fSetTemp[0], stepConst.getMinTemp());
                        float maxTmp = Math.min(fSetTemp[0] + step.getFGradRange(), stepConst.getMaxTemp());
                        fSetTemp[4] = fSetTemp[2] = fSetTemp[0] = minTmp;
                        fSetTemp[5] = fSetTemp[3] = fSetTemp[1] = maxTmp;
                        step.setFSetTemp(fSetTemp);
                    } else if (DirectionType.UpDown.equals(step.getFDirection())) {
                        step.setFGradRange(getAppropriateValue(originStep.getFGradRange(), stepConst.getMinUpDownGradRange(), stepConst.getMinUpDownGradRange()));
                        Float[] fSetTemp = originStep.getFSetTemp();
                        float minTmp = Math.max(fSetTemp[0], stepConst.getMinTemp());
                        float maxTmp = Math.min(fSetTemp[0] + step.getFGradRange(), stepConst.getMaxTemp());

                        fSetTemp[1] = fSetTemp[0] = minTmp;
                        fSetTemp[3] = fSetTemp[2] = (minTmp + maxTmp) / 2;
                        fSetTemp[5] = fSetTemp[4] = maxTmp;
                        step.setFSetTemp(fSetTemp);
                    }
                }
                step.setFSetTime(getAppropriateValue(originStep.getFSetTime(), stepConst.getMinTime(), stepConst.getMaxTime())); // 步骤时间

                step.setPlusMinus(originStep.getPlusMinus());
                step.setFExtTime(Math.max(Math.abs(originStep.getFSetTime()), stepConst.getExtTimeRange())); // 时间增量
                if (PlusMinusType.minus.equals(step.getPlusMinus())) {
                    step.setFExtTime(-step.getFExtTime());
                }
                step.setFExtTimeOpen(originStep.getFExtTimeOpen()); // 时间增量开启点

                step.setFIncTempPlusMinus(originStep.getFIncTempPlusMinus());
                step.setFIncTemp(getAppropriateValue(Math.abs(originStep.getFIncTemp()), stepConst.getMinIncTemp(), stepConst.getMaxIncTemp())); // 温度增量
                if (PlusMinusType.minus.equals(step.getFIncTempPlusMinus())) {
                    step.setFIncTemp(-step.getFIncTemp());
                }
                step.setFIncTempOpen(originStep.getFIncTempOpen()); // 温度增量开启点
                step.setFRampRate(getAppropriateValue(originStep.getFRampRate(), stepConst.getMinRampRate(), stepConst.getMaxRampRate())); // 变温速率
            }
            stepList.add(step);
        }
        return bioFile;
    }

    /**
     * 当 value 小于 最小值 返回最小值
     * 当 value 大于 最大值 返回最大值
     * 当 value 大于 最小值 小于最大值 返回 value
     */
    public static <T extends Comparable<T>> T getAppropriateValue(T value, T min, T max) {
        return value.compareTo(min) < 0 ? min : value.compareTo(max) > 0 ? max : value;
    }

    /**
     * 当 value 小于 最小值 返回默认值
     * 当 value 大于 最大值 返回默认值
     * 当 value 大于 最小值 小于最大值 返回 value
     */
    public static <T extends Comparable<T>> T getAppropriateValue(T value, T defaultValue, T min, T max) {
        if (Objects.isNull(value)) {
            return defaultValue;
        }
        return value.compareTo(min) < 0 ? defaultValue : value.compareTo(max) > 0 ? defaultValue : value;
    }

    /* 读文件 */
    public static BioFile readFile(byte[] bytes) {
        if (bytes.length == 686) {
            return readFile686(bytes);
        } else if (bytes.length == 1214) {
            return readFile1214(bytes);
        }
        throw new IllegalArgumentException("参数错误！");
    }

    private static BioFile readFile1214(byte[] bytes) {
        BioFile bioFile = new BioFile();
        bioFile.setFlidBool((bytes[2] & 0xff) > 0); // 热盖开关
        bioFile.setFlid(byteArrayToInt(bytes, 3) / 100f); // 热盖温度
        bioFile.setFVolume(bytes[8] & 0xff); // 试剂量

        int step = bytes[9] & 0xff; // 文件步骤总数
        List<Step> stepList = new ArrayList<>(step);// 温度循环步骤集合

        for (int i = 0; i < step; i++) {
            int index = 10 + i * 30;
            Step tempStep = new Step();
            tempStep.setFDirection(bytes[index] > 0 ? DirectionType.UpDown : DirectionType.LeftRight); // 梯度方向

            // 温度步、循环步
            if ((bytes[1 + index] & 0xff) == StepType.LOOPitem.getValue()) {
                tempStep.setFTempCycSel(StepType.LOOPitem);
            } else {
                tempStep.setFTempCycSel(StepType.STEPitem);
            }

            tempStep.setFTargetStep(byteArrayToInt(bytes, 2 + index));// 获取目标段
            tempStep.setFCycNum(byteArrayToInt(bytes, 4 + index));// 获取循环数

            Float[] fSetTemp = new Float[6]; // 获取设置温度
            fSetTemp[0] = byteArrayToInt(bytes, 6 + index) / 100f;
            fSetTemp[1] = byteArrayToInt(bytes, 8 + index) / 100f;
            fSetTemp[2] = byteArrayToInt(bytes, 10 + index) / 100f;
            fSetTemp[3] = byteArrayToInt(bytes, 12 + index) / 100f;
            fSetTemp[4] = byteArrayToInt(bytes, 14 + index) / 100f;
            fSetTemp[5] = byteArrayToInt(bytes, 16 + index) / 100f;
            tempStep.setFSetTemp(fSetTemp);

            tempStep.setFGradRange(byteArrayToInt(bytes, 18 + index) / 100f); // 获取梯度范围
            tempStep.setFSetTime(byteArrayToInt(bytes, 20 + index) * 1000L); // 获取设置时间,单位秒

            tempStep.setFIncTemp(byteArrayToInt(bytes, 22 + index) / 100f); // 获取温度增量
            tempStep.setFIncTempPlusMinus(tempStep.getFIncTemp() > 0 ? PlusMinusType.plus : PlusMinusType.minus);
            tempStep.setFIncTempOpen(bytes[28 + index]);
            tempStep.setFIncTempOpen(tempStep.getFIncTemp() == 0 ? 0 : tempStep.getFIncTempOpen() == 0 ? 1 : tempStep.getFIncTempOpen());
            tempStep.setFIncTemp(tempStep.getFIncTempOpen() == 0 ? 0 : tempStep.getFIncTemp());

            tempStep.setFExtTime((long) byteArrayToInt(bytes, 24 + index)); // 获取时间延伸
            tempStep.setPlusMinus(tempStep.getFExtTime() > 0 ? PlusMinusType.plus : PlusMinusType.minus);
            tempStep.setFExtTimeOpen(bytes[29 + index]);
            tempStep.setFExtTimeOpen(tempStep.getFExtTime() == 0 ? 0 : tempStep.getFExtTimeOpen() == 0 ? 1 : tempStep.getFExtTimeOpen());
            tempStep.setFExtTime(tempStep.getFExtTimeOpen() == 0 ? 0 : tempStep.getFExtTime());

            tempStep.setFRampRate(byteArrayToInt(bytes, 26 + index) / 100f); // 获取爬坡速率
            stepList.add(tempStep);
        }
        bioFile.setSteplist(stepList);
        return bioFile;
    }

    private static BioFile readFile686(byte[] bytes) {
        BioFile bioFile = new BioFile();
        bioFile.setFlidBool((bytes[680] & 0xff) != 0); // 热盖开关
        bioFile.setFlid(byteArrayToInt(bytes, 681) / 100f); // 热盖温度
        bioFile.setFVolume(bytes[12] & 0xff); // 试剂量

        int step = bytes[13] & 0xff; // 文件步骤总数
        List<Step> stepList = new ArrayList<>(step); // 温度循环步骤集合

        for (int i = 0; i < step; i++) {
            int index = 14 + i * 22;
            Step tempStep = new Step();
            // 获取步骤标志
            if (byteArrayToInt(bytes, index) == StepType.LOOPitem.getValue()) {
                tempStep.setFTempCycSel(StepType.LOOPitem);
            } else {
                tempStep.setFTempCycSel(StepType.STEPitem);
            }

            tempStep.setFTargetStep(byteArrayToInt(bytes, 2 + index)); // 获取目标段
            tempStep.setFCycNum(byteArrayToInt(bytes, 4 + index)); // 获取循环数

            float fSetTempItem = byteArrayToInt(bytes, 6 + index) / 100f; // 获取设置温度
            Float[] fSetTemp = new Float[6];
            fSetTemp[0] = fSetTempItem;
            fSetTemp[1] = fSetTempItem;
            fSetTemp[2] = fSetTempItem;
            fSetTemp[3] = fSetTempItem;
            fSetTemp[4] = fSetTempItem;
            fSetTemp[5] = fSetTempItem;
            tempStep.setFSetTemp(fSetTemp);

            tempStep.setFGradRange(byteArrayToInt(bytes, 8 + index) / 100f); // 获取梯度范围
            tempStep.setFSetTime(byteArrayToInt(bytes, 10 + index) * 1000L); // 获取设置时间,单位秒

            tempStep.setFIncTemp(byteArrayToInt(bytes, 12 + index) / 100f); // 获取温度增量
            tempStep.setFIncTempPlusMinus(tempStep.getFIncTemp() > 0 ? PlusMinusType.plus : PlusMinusType.minus);
            tempStep.setFIncTempOpen(bytes[18 + index]);
            tempStep.setFIncTempOpen(tempStep.getFIncTemp() == 0 ? 0 : tempStep.getFIncTempOpen() == 0 ? 1 : tempStep.getFIncTempOpen());
            tempStep.setFIncTemp(tempStep.getFIncTempOpen() == 0 ? 0 : tempStep.getFIncTemp());


            tempStep.setFExtTime((long) byteArrayToInt(bytes, 14 + index)); // 获取时间延伸
            tempStep.setPlusMinus(tempStep.getFExtTime() > 0 ? PlusMinusType.plus : PlusMinusType.minus);
            tempStep.setFExtTimeOpen(bytes[19 + index]);
            tempStep.setFExtTimeOpen(tempStep.getFExtTime() == 0 ? 0 : tempStep.getFExtTimeOpen() == 0 ? 1 : tempStep.getFExtTimeOpen());
            tempStep.setFExtTime(tempStep.getFExtTimeOpen() == 0 ? 0 : tempStep.getFExtTime());

            tempStep.setFRampRate(byteArrayToInt(bytes, 16 + index) / 100f); // 获取爬坡速率
            stepList.add(tempStep);
        }
        bioFile.setSteplist(stepList);
        return bioFile;
    }

    public static byte[] getByteArray686(BioFile bioFile) {
        byte[] array = new byte[686];
        array[12] = (byte) bioFile.getFVolume().intValue(); // 试剂量
        array[13] = (byte) bioFile.getSteplist().size(); // 文件步骤

        // 步骤集合
        for (int i = 0; i < bioFile.getSteplist().size(); i++) {
            int index = 14 + i * 22;
            Step tempStep = bioFile.getSteplist().get(i);
            // 获取温度循环步骤标志
            intToByteArray(tempStep.getFTempCycSel().getValue(), array, index);
            // 获取目标段
            intToByteArray(tempStep.getFTargetStep(), array, 2 + index);
            // 获取循环数
            intToByteArray(tempStep.getFCycNum(), array, 4 + index);
            // 获取设置温度
            int SetTemp = Math.round(tempStep.getFSetTemp()[0] * 100f);
            intToByteArray(SetTemp, array, 6 + index);
            // 获取梯度范围
            int GradRange = Math.round(tempStep.getFGradRange() * 100f);
            intToByteArray(GradRange, array, 8 + index);
            // 获取设置时间
            int SetTime = Math.round(tempStep.getFSetTime());
            intToByteArray(SetTime, array, 10 + index);
            // 获取温度增量
            int IncTemp = Math.round(tempStep.getFIncTemp() * 100f);
            intToByteArray(IncTemp, array, 12 + index);
            // 获取时间延伸
            int extTime = Math.round(tempStep.getFExtTime());
            intToByteArray(tempStep.getPlusMinus() == PlusMinusType.minus ? -extTime : extTime, array, 14 + index);
            // 获取爬坡速率
            int RampRate = Math.round(tempStep.getFRampRate() * 100f);
            intToByteArray(RampRate, array, 16 + index);

            array[18 + index] = (byte) tempStep.getFIncTempOpen();
            array[19 + index] = (byte) tempStep.getFExtTimeOpen();
        }

        /* 热盖开关 */
        array[680] = (byte) (bioFile.getFlidBool() ? 1 : 0);
        /* 热盖温度 */
        intToByteArray(Math.round(bioFile.getFlid() * 100F), array, 681);
        return array;
    }

    public static byte[] getByteArray1214(BioFile bioFile) {
        byte[] array = new byte[1214];
        /* 温控模式1/0 */
        //array[0] = MainApp.getInstance().appClass.getSettingTempCtrl().getValue();
        /* 热盖模式1/1 */
        //array[1] = MainApp.getInstance().appClass.getSettingLidState().getValue();

        /* 热盖开关1/2 */
        array[2] = (byte) (bioFile.getFlidBool() ? 1 : 0);
        /* 热盖温度2/3-4 */
        intToByteArray(Math.round(bioFile.getFlid() * 100F), array, 3);

        /* 5-7 未使用 */
        array[8] = (byte) bioFile.getFVolume().intValue(); // 试剂量 1/8
        array[9] = (byte) bioFile.getSteplist().size(); // 文件步骤1/9

        // 步骤集合
        for (int i = 0; i < bioFile.getSteplist().size(); i++) {
            int index = 10 + i * 30;
            Step tempStep = bioFile.getSteplist().get(i);

            array[index] = (byte) (tempStep.getFDirection() == DirectionType.LeftRight ? 0 : 1);
            array[1 + index] = (byte) tempStep.getFTempCycSel().getValue();// 获取步骤标志

            intToByteArray(tempStep.getFTargetStep(), array, 2 + index); // 获取目标段
            intToByteArray(tempStep.getFCycNum(), array, 4 + index); // 获取循环数

            int SetTemp = Math.round(tempStep.getFSetTemp()[0] * 100f); // 获取设置温度
            intToByteArray(SetTemp, array, 6 + index);
            SetTemp = Math.round(tempStep.getFSetTemp()[1] * 100f);
            intToByteArray(SetTemp, array, 8 + index);
            SetTemp = Math.round(tempStep.getFSetTemp()[2] * 100f);
            intToByteArray(SetTemp, array, 10 + index);
            SetTemp = Math.round(tempStep.getFSetTemp()[3] * 100f);
            intToByteArray(SetTemp, array, 12 + index);
            SetTemp = Math.round(tempStep.getFSetTemp()[4] * 100f);
            intToByteArray(SetTemp, array, 14 + index);
            SetTemp = Math.round(tempStep.getFSetTemp()[5] * 100f);
            intToByteArray(SetTemp, array, 16 + index);

            int GradRange = Math.round(tempStep.getFGradRange() * 100f); // 获取梯度范围
            intToByteArray(GradRange, array, 18 + index);

            int SetTime = (int) (tempStep.getFSetTime() / 1000L); // 获取设置时间
            intToByteArray(SetTime, array, 20 + index);

            int IncTemp = Math.round(tempStep.getFIncTemp() * 100f); // 获取温度增量
            intToByteArray(IncTemp, array, 22 + index);

            int extTime = (int) (tempStep.getFExtTime() / 1000L); // 获取时间延伸
            intToByteArray(tempStep.getPlusMinus() == PlusMinusType.minus ? -extTime : extTime, array, 24 + index);

            int RampRate = Math.round(tempStep.getFRampRate() * 100f); // 获取爬坡速率
            intToByteArray(RampRate, array, 26 + index);

            array[28 + index] = (byte) tempStep.getFIncTempOpen();
            array[29 + index] = (byte) tempStep.getFExtTimeOpen();
        }
        return array;
    }

    /* byte转int */
    public static int byteArrayToInt(final byte[] buffer, int offset) {
        int value = (buffer[offset] & 0xff) << 8;
        value += buffer[offset + 1] & 0xff;
        /* 判断最高位是否为负 */
        if (value >> 15 == 1)
            value = value | 0xffff0000;
        return value;
    }

    /* int转byte */
    public static void intToByteArray(int value, byte[] buffer, int offset) {
        buffer[offset] = (byte) (value >> 8);
        buffer[offset + 1] = (byte) value;
    }
}
