package com.sfwl.bh.entity.ws;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author huhy
 * @version 1.0
 * @date 2020/5/15 15:07
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class WsData<T, U> {

    private String target;
    @SerializedName("target-data")
    private T targetData;
    private String action;
    private String code;
    @SerializedName("action-data")
    private U actionData;
}
