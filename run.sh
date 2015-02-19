#!/bin/sh

# Put your JDBC information in roller-custom.properties at following dir 
ROLLER_CUSTOM_PROPERTIES_DIR=./conf
# Set appropriate one for your Roller instance
JDBC_JAR=/Users/kyle/apps/db-derby-10.11.1.1-bin/lib/derby.jar
# Set appropriate one for your environment
TOOL_JAR=./target/wxr2roller-jar-with-dependencies.jar

java -cp $ROLLER_CUSTOM_PROPERTIES_DIR:$JDBC_JAR:$TOOL_JAR org.nailedtothex.wxr2roller.Main $@
