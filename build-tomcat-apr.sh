#!/bin/bash

# http://tomcat.apache.org/native-doc/
# script for guidance only - don't expect it to work as executable!

sudo apt-get install libapr1-dev libssl-dev

cd ~/Development/software/tomcat-native-1.1.33-src/jni/native

./configure --with-apr=/usr/bin/apr-1-config \
            --with-java-home=$JAVA_HOME \
            --with-ssl=yes \
            --prefix=$CATALINA_HOME

sudo make
sudo make install

libtool --finish ~/Development/installs/tomcat-8/lib

sudo cp lib* /usr/lib
sudo cp -r pkgconfig /usr/lib

