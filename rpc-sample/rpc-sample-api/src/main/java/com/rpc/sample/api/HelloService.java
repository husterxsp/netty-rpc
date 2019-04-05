package com.rpc.sample.api;

/**
 * @author xushaopeng
 * @date 2019/04/05
 */
public interface HelloService {

    String hello(String name);

    String hello(Person person);

}
