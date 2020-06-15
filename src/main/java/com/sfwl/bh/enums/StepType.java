package com.sfwl.bh.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author huhy
 * @version 1.0
 * @date 2020/5/8 16:42
 */
@Getter
@AllArgsConstructor
public enum StepType {
    STEPitem(0x55), LOOPitem(0xAA);

    private int value;
}
