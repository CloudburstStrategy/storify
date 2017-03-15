package com.cloudburst.storify.examples;

import com.cloudburst.storify.annotation.Entity;
import com.cloudburst.storify.annotation.Id;
import com.cloudburst.storify.annotation.Cache;

@Entity
@Cache(expirationSeconds = 120)
public class CachedEntity {

    private String name;

    private String value;

    private long longValue;

    @Id
    public String getName() {
        return name;
    }

    public CachedEntity setName(String name) {
        this.name = name;
        return this;
    }

    public String getValue() {
        return value;
    }

    public CachedEntity setValue(String value) {
        this.value = value;
        return this;
    }

    public long getLongValue() {
        return longValue;
    }

    public CachedEntity setLongValue(long longValue) {
        this.longValue = longValue;
        return this;
    }
}
