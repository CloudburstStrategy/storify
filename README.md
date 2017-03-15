Storify
=======

[![Build Status](https://travis-ci.org/CloudburstStrategy/storify.svg?branch=master)](https://travis-ci.org/CloudburstStrategy/storify)

POJO Mapping for Google Datastore

Map @Entity to datastore Kinds

Inspired by Objectify but can be used on Flexible App Engine

## Define Entity Pojo

    @Entity
    public class BasicEntity {
    
        private String name;
    
        private String value;
    
        private long longValue;
    
        @Id
        public String getName() {
            return name;
        }
    
        public BasicEntity setName(String name) {
            this.name = name;
            return this;
        }
    
        public String getValue() {
            return value;
        }
    
        public BasicEntity setValue(String value) {
            this.value = value;
            return this;
        }
    
        public long getLongValue() {
            return longValue;
        }
    
        public BasicEntity setLongValue(long longValue) {
            this.longValue = longValue;
            return this;
        }
    }

## Basic Usage

    BasicEntity entity = new BasicEntity().setName("testkey").setValue("testvalue").setLongValue(123);
    Storify.sfy().put(entity);
    BasicEntity test = Storify.sfy().load(BasicEntity.class,"testkey");



## To Release new version to Bintray

    mvn clean release:prepare -Darguments="-Dmaven.javadoc.skip=true"
    mvn release:perform -Darguments="-Dmaven.javadoc.skip=true"