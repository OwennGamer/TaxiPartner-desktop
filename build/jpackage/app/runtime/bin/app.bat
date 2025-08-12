@echo off
set DIR="%~dp0"
set JAVA_EXEC="%DIR:"=%\java"



pushd %DIR% & %JAVA_EXEC% %CDS_JVM_OPTS%  -p "%~dp0/../app" -m com.partnertaxi.taxipartneradmin/com.partnertaxi.taxipartneradmin.HelloApplication  %* & popd
