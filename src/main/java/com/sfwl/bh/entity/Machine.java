package com.sfwl.bh.entity;

import com.sfwl.bh.enums.GradType;
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
public class Machine {

    private Integer blockNum; // 模块数量

    private Integer slotRow; // 槽行数
    private Integer slotColumn; // 槽列数
    private Integer slotNum; // 槽数量

    private GradType gradType; // 梯度类型，0普通，1动态，2二维
    private Float[] LeftRightGradTable; // 梯度系数
    private Float[] upDownGradTable; // 上下梯度系数
    private String[] gradTableTopStr; // 梯度系数的项部替代字符
    private String[] gradTableLeftStr; // 梯度系数的左边替代字符

    private String[] fileExtName;// 文件扩展名

    public static final Machine DEFAULT = new Machine();
    static {
        DEFAULT.blockNum = 1;// 模块个数

        DEFAULT.slotRow = 8; // 槽行数
        DEFAULT.slotColumn = 12; // 槽列数
        DEFAULT.slotNum = 1; // 槽数量
        DEFAULT.gradType = GradType.General;// 梯度类型,0普通,1动态,2二维

        DEFAULT.LeftRightGradTable = new Float[]{0.0f, 0.04f, 0.13f, 0.25f, 0.36f, 0.45f, 0.55f, 0.64f, 0.75f, 0.87f, 0.96f,
                1f};// 梯度系数
        DEFAULT.upDownGradTable = null;
        DEFAULT.gradTableTopStr = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"}; // 顶部字符与 槽列数乘以槽数量 对应
        DEFAULT.gradTableLeftStr = new String[]{"A", "B", "C", "D", "E", "F", "G", "H"}; // 左边字符与 槽行数 对应

        DEFAULT.fileExtName = new String[]{".BIO", ".bio"};
    }
}
