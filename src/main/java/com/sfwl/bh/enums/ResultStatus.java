package com.sfwl.bh.enums;

public enum ResultStatus {

    SUCCESS(0, "成功!"),
    FAIL(-1, "失败!"),
    ERROR(-2, "系统异常!"),
    UNAUTHORIZED(-3, "用户验证失败!"),
    FORBIDDEN(-4, "没有权限!");

    private int code;
    private String msg;

    ResultStatus(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
