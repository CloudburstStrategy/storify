package com.paxport.storify.examples;

import com.paxport.storify.annotation.Entity;
import com.paxport.storify.annotation.Id;

/**
 * Created by ajchesney on 24/08/2016.
 */
@Entity
public class CompositeKeyEntity {

    private String one;

    private String two;

    private String value;

    @Id
    public String getKey() {
        return one + ":" + two;
    }

    public String getOne() {
        return one;
    }

    public CompositeKeyEntity setOne(String one) {
        this.one = one;
        return this;
    }

    public String getTwo() {
        return two;
    }

    public CompositeKeyEntity setTwo(String two) {
        this.two = two;
        return this;
    }

    public String getValue() {
        return value;
    }

    public CompositeKeyEntity setValue(String value) {
        this.value = value;
        return this;
    }
}
