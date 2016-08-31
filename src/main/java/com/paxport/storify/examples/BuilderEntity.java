package com.paxport.storify.examples;

import com.paxport.storify.annotation.Entity;
import com.paxport.storify.annotation.Id;

import org.immutables.value.Value;

@Value.Immutable
@Value.Style(
        get = {"is*", "get*"},
        depluralize = true
)
@Entity(name = "BuilderEntity", builderClass = ImmutableBuilderEntity.Builder.class)
public abstract class BuilderEntity implements BuilderBoolComponent {

    @Id
    public abstract String getId();

    public abstract String getValue();

    // this should not be required but is due to jdk bug
    // http://stackoverflow.com/questions/31703563/java-8-interface-default-method-doesnt-seem-to-declare-property
    public abstract Boolean getBooleanValue();

    public abstract ExampleEnum getExampleEnum();
}
