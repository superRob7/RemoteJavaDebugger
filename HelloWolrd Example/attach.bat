@echo off
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=8000 HelloWorld