#!/bin/bash

ROOT_PATH="/home/ubuntu/modak-server"
JAR="$ROOT_PATH/application.jar"

APP_LOG="$ROOT_PATH/application.log"
ERROR_LOG="$ROOT_PATH/error.log"
START_LOG="$ROOT_PATH/start.log"

NOW=$(date +%c)

echo "[$NOW] $JAR 복사" >> $START_LOG
cp $ROOT_PATH/build/libs/modak-server-0.0.1-SNAPSHOT.jar $JAR

echo "[$NOW] > $JAR 실행" >> $START_LOG
nohup java -jar $JAR > $APP_LOG 2> $ERROR_LOG &

SERVICE_PID=$(pgrep -f $JAR)
echo "[$NOW] > 서비스 PID: $SERVICE_PID" >> $START_LOG

[Sat Oct 12 12:29:58 2024] /home/ubuntu/modak-server/application.jar 복사
[Sat Oct 12 12:29:58 2024] > /home/ubuntu/modak-server/application.jar 실행
[Sat Oct 12 12:29:58 2024] > 서비스 PID: