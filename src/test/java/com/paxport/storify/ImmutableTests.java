package com.paxport.storify;

import com.paxport.storify.examples.BuilderEntity;
import com.paxport.storify.examples.ExampleEnum;
import com.paxport.storify.examples.ImmutableBuilderEntity;

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
