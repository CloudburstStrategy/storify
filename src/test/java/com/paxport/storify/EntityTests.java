package com.paxport.storify;

import com.paxport.storify.Storify;
import com.paxport.storify.examples.BasicEntity;
import com.paxport.storify.examples.CompositeKeyEntity;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajchesney on 23/08/2016.
 */
public class EntityTests {

    @Test
    public void testBasicPutAndThenLoad() {

        BasicEntity entity = new BasicEntity().setName("testkey").setValue("testvalue").setLongValue(123);

        Storify.sfy().put(entity);

        BasicEntity test = Storify.sfy().load(BasicEntity.class,"testkey");
        Assert.assertEquals("testkey", test.getName());
        Assert.assertEquals("testvalue", test.getValue());
        Assert.assertEquals(123, test.getLongValue());

    }

    @Test
    public void testCompositeKeyPutAndThenLoad() {

        CompositeKeyEntity entity = new CompositeKeyEntity().setOne("one").setTwo("two").setValue("testvalue");

        Storify.sfy().put(entity);

        CompositeKeyEntity test = Storify.sfy().load(CompositeKeyEntity.class,"one:two");
        Assert.assertEquals("one", test.getOne());
        Assert.assertEquals("two", test.getTwo());
        Assert.assertEquals("testvalue", test.getValue());

    }
}
