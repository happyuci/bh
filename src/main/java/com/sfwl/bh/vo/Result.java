package com.sfwl.bh.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Result {
    private Integer id;

    private Integer linkid;

    private String name;
}
