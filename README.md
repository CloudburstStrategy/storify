Storify
===================

POJO Mapping for Google Datastore

Map @Entity to datastore Kinds

Inspired by Objectify but can be used on Flexible App Engine

## Basic Usage

    BasicEntity entity = new BasicEntity().setName("testkey").setValue("testvalue").setLongValue(123);
    Storify.sfy().put(entity);
    BasicEntity test = Storify.sfy().load(BasicEntity.class,"testkey");



## To Release new version to Bintray

    mvn clean release:prepare -Darguments="-Dmaven.javadoc.skip=true"
    mvn release:perform -Darguments="-Dmaven.javadoc.skip=true"