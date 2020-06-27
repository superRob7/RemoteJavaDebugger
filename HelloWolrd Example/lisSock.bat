@echo off
java -agentlib:jdwp=transport=dt_socket,server=n,suspend=y,address=8000 HelloWorld