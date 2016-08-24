package com.paxport.storify;

import com.paxport.storify.examples.BasicEntity;
import com.paxport.storify.examples.CompositeKeyEntity;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajchesney on 23/08/2016.
 */
public class ImmutableTests {

    @Test
    public void testBasicPutAndThenLoad() {



        BasicEntity entity = new BasicEntity().setName("testkey").setValue("testvalue").setLongValue(123);

        Storify.sfy().put(entity);

        BasicEntity test = Storify.sfy().load(BasicEntity.class,"testkey");
        Assert.assertEquals("testkey", test.getName());
        Assert.assertEquals("testvalue", test.getValue());
        Assert.assertEquals(123, test.getLongValue());

    }


}
