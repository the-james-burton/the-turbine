@echo off

c:\dev\elk\elasticsearch-1.5.2\bin\elasticsearch.bat
c:\dev\elk\kibana-4.0.2-windows\bin\kibana.bat 

c:\dev\elk\logstash-1.4.2\bin\logstash.bat agent -f c:/dev/projects/the-turbine/turbine/src/main/resources/elk/logstash-json-windows.conf
