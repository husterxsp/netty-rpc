package com.rpc.sample.api;

import lombok.Data;

/**
 * @author xushaopeng
 * @date 2019/04/05
 */
@Data
public class Person {

    private String firstName;

    private String lastName;

    public Person() {
    }

    public Person(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

}
