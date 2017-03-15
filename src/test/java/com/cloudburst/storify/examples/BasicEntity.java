package com.cloudburst.storify.examples;

import com.cloudburst.storify.annotation.Entity;
import com.cloudburst.storify.annotation.Id;

@Entity
public class BasicEntity {

    private String name;

    private String value;

    private long longValue;

    @Id
    public String getName() {
        return name;
    }

    public BasicEntity setName(String name) {
        this.name = name;
        return this;
    }

    public String getValue() {
        return value;
    }

    public BasicEntity setValue(String value) {
        this.value = value;
        return this;
    }

    public long getLongValue() {
        return longValue;
    }

    public BasicEntity setLongValue(long longValue) {
        this.longValue = longValue;
        return this;
    }
}
