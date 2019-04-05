package com.rpc.common.bean;

import lombok.Data;

/**
 * @author xushaopeng
 * @date 2019/04/03
 */
@Data
public class RpcResponse {

    private String requestId;

    private Exception exception;

    private Object result;

    public boolean hasException() {
        return exception != null;
    }

}
