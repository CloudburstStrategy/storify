package com.cloudburst.storify;

import com.cloudburst.storify.examples.BuilderEntity;
import com.cloudburst.storify.examples.ExampleEnum;
import com.cloudburst.storify.examples.ImmutableBuilderEntity;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajchesney on 23/08/2016.
 */
public class ImmutableTests {

    @Test
    public void testImmutablePutAndThenLoad() {
        BuilderEntity entity = ImmutableBuilderEntity
                .builder()
                .id("testkey")
                .value("testvalue")
                .booleanValue(true)
                .exampleEnum(ExampleEnum.TWO)
                .build();

        Storify.sfy().put(entity);

        BuilderEntity test = Storify.sfy().load(BuilderEntity.class,"testkey").get();
        Assert.assertEquals("testkey", test.getId());
        Assert.assertEquals("testvalue", test.getValue());
        Assert.assertEquals(true, test.getBooleanValue());
        Assert.assertEquals(ExampleEnum.TWO, test.getExampleEnum());

    }


}
