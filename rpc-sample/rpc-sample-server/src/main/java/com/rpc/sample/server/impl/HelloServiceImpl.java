package com.rpc.sample.server.impl;

import com.rpc.sample.api.HelloService;
import com.rpc.sample.api.Person;
import com.rpc.server.RpcService;

/**
 * @author xushaopeng
 * @date 2019/04/05
 */
@RpcService(HelloService.class)
public class HelloServiceImpl implements HelloService {

    @Override
    public String hello(String name) {
        return "Hello! " + name;
    }

    @Override
    public String hello(Person person) {
        return "Hello! " + person.getFirstName() + " " + person.getLastName();
    }

}

