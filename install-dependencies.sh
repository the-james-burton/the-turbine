#!/bin/bash

mvn install:install-file -Dfile=lib/jquantlib/jquantlib-helpers-0.2.4.jar -DgroupId=org.jquantlib -DartifactId=jquantlib-helpers -Dversion=0.2.4 -D packaging=jar -DgeneratePom=true
mvn install:install-file -Dfile=lib/jquantlib/jquantlib-0.2.4.jar -DpomFile=lib/jquantlib/pom.xml

