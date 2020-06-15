package com.sfwl.bh.entity.response;


import com.sfwl.bh.enums.ResultStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BaseModel<T> {

    private Integer result;
    private String msg;
    private String token;
    private T object;

    public BaseModel(ResultStatus resultStatus) {
        this.result = resultStatus.getCode();
        this.msg = resultStatus.getMsg();
    }

    public BaseModel(Integer result, String msg) {
        this.result = result;
        this.msg = msg;
    }

    public BaseModel(ResultStatus resultStatus, T object) {
        this.result = resultStatus.getCode();
        this.msg = resultStatus.getMsg();
        this.object = object;
    }

    public BaseModel(Integer result, String msg, T object) {
        this.result = result;
        this.msg = msg;
        this.object = object;
    }

    public BaseModel(ResultStatus resultStatus, String token, T object) {
        this.result = resultStatus.getCode();
        this.msg = resultStatus.getMsg();
        this.token = token;
        this.object = object;
    }

    public BaseModel(Integer result, String msg, String token, T object) {
        this.result = result;
        this.msg = msg;
        this.token = token;
        this.object = object;
    }
}
