#!/bin/bash

# mvn install:install-file -Dfile=your-artifact-1.0.jar \
#                         [-DpomFile=your-pom.xml] \
#                         [-Dsources=src.jar] \
#                         [-Djavadoc=apidocs.jar] \
#                         [-DgroupId=org.some.group] \
#                         [-DartifactId=your-artifact] \
#                         [-Dversion=1.0] \
#                         [-Dpackaging=jar] \
#                         [-Dclassifier=sources] \
#                         [-DgeneratePom=true] \
#                         [-DcreateChecksum=true]

mvn install:install-file -Dfile=jquantlib-helpers-0.2.4.jar -Dpackaging=jar -DgroupId=org.jquantlib -DartifactId=jquantlib-helpers -Dversion=0.2.4

mvn install:install-file -Dfile=jquantlib-0.2.4.jar -DpomFile=pom.xml
