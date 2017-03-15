package com.cloudburst.storify;

import com.cloudburst.storify.examples.CompositeKeyEntity;
import com.cloudburst.storify.examples.BasicEntity;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajchesney on 23/08/2016.
 */
public class EntityTests {

    @Test
    public void testBasicPutAndThenLoadThenDelete() {

        BasicEntity entity = new BasicEntity().setName("testkey").setValue("testvalue").setLongValue(123);

        Storify.sfy().put(entity);

        BasicEntity test = Storify.sfy().load(BasicEntity.class,"testkey").get();
        Assert.assertEquals("testkey", test.getName());
        Assert.assertEquals("testvalue", test.getValue());
        Assert.assertEquals(123, test.getLongValue());

        Storify.sfy().delete(BasicEntity.class,"testkey");

    }

    @Test
    public void testCompositeKeyPutAndThenLoad() {

        CompositeKeyEntity entity = new CompositeKeyEntity().setOne("one").setTwo("two").setValue("testvalue");

        Storify.sfy().put(entity);

        CompositeKeyEntity test = Storify.sfy().load(CompositeKeyEntity.class,"one:two").get();
        Assert.assertEquals("one", test.getOne());
        Assert.assertEquals("two", test.getTwo());
        Assert.assertEquals("testvalue", test.getValue());

        Storify.sfy().delete(CompositeKeyEntity.class,"one:two");

    }
}
