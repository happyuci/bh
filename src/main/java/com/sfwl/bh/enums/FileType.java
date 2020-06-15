package com.sfwl.bh.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author huhy
 * @version 1.0
 * @date 2020/5/8 16:40
 */
@Getter
@AllArgsConstructor
public enum FileType {
    /* 文件的大小 */
    Size686(686),
    Size1214(1214);

    private int value;
}
