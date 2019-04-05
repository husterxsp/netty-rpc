package com.rpc.common.bean;

import lombok.Data;

/**
 * 封装 RPC 请求
 *
 * @author xushaopeng
 * @date 2019/04/03
 */
@Data
public class RpcRequest {

    private String requestId;

    private String interfaceName;

    private String serviceVersion;

    private String methodName;

    private Class<?>[] parameterTypes;

    private Object[] parameters;

}
