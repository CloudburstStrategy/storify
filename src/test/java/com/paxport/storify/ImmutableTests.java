package com.paxport.storify;

import com.paxport.storify.examples.BasicEntity;
import com.paxport.storify.examples.BuilderEntity;
import com.paxport.storify.examples.CompositeKeyEntity;
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
                .build();

        Storify.sfy().put(entity);

        BuilderEntity test = Storify.sfy().load(BuilderEntity.class,"testkey");
        Assert.assertEquals("testkey", test.getId());
        Assert.assertEquals("testvalue", test.getValue());
        Assert.assertEquals(true, test.getBooleanValue());

    }


}
