package com.paxport.storify.examples;

import com.paxport.storify.annotation.Entity;
import com.paxport.storify.annotation.Id;

import org.immutables.value.Value;

@Value.Immutable
@Value.Style(
        get = {"is*", "get*"},
        depluralize = true
)
@Entity
public abstract class BuilderEntity {

    @Id
    public abstract String getId();

    public abstract String getValue();

    public abstract Boolean getBooleanValue();
}
