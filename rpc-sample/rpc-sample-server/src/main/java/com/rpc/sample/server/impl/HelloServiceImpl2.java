package com.rpc.sample.server.impl;

import com.rpc.sample.api.HelloService;
import com.rpc.sample.api.Person;
import com.rpc.server.RpcService;

/**
 * @author xushaopeng
 * @date 2019/04/05
 */
@RpcService(value = HelloService.class, version = "sample.hello2")
public class HelloServiceImpl2 implements HelloService {

    @Override
    public String hello(String name) {
        return "你好! " + name;
    }

    @Override
    public String hello(Person person) {
        return "你好! " + person.getFirstName() + " " + person.getLastName();
    }

}