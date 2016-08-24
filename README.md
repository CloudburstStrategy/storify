Big Query Tools
===================

Tooling for working with Google Datastore

Map @Entity to datastore Kinds

Inspired by Objectify but can be used on Flexible App Engine


## To Release new version to Bintray

    mvn clean release:prepare -Darguments="-Dmaven.javadoc.skip=true"
    mvn release:perform -Darguments="-Dmaven.javadoc.skip=true"