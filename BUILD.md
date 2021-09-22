# MISC notes to build the project
# Custom JRE

java --list-modules

jar --file=jnetcat-1.0.0-SNAPSHOT.jar --describe-module
jdeps jnetcat-1.0.0-SNAPSHOT.jar

java.base
java.desktop
java.sql
java.xml
+ java.scripting
+ java.naming
+ java.management

jlink --add-modules java.base,java.desktop,java.sql,java.xml,java.scripting,java.naming,java.management --output custom-runt

# Binary creation

* mvn clean package
	-> will output result executable in target/dist
