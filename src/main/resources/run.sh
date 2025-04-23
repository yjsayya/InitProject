#!/bin/bash

# 1. Java Home
JAVA_HOME=/app/java/jdk1.8.0 # java 경로 넣기

# 2. Application
APP_HOME=`pwd` # 현재 dir
APP_NAME="jar파일이름" # BootJar 후에 생긴 .jar 파일
APPLICATION=$APP_HOME/$APP_NAME
SERVICE_NAME=서비스이름

#CONF=$APP_HOME/application.yml

# 4.PID
PID_FILE=$SERVICE_NAME.pid

# 3. Java Options
MIN_HEAP_MEMORY=1024m
MAX_HEAP_MEMORY=1024m

JAVA_OPTS="-server"
JAVA_OPTS="$JAVA_OPTS -Dservice.name=$SERVICE_NAME"
JAVA_OPTS="$JAVA_OPTS -Xms$MIN_HEAP_MEMORY -Xmx$MAX_HEAP_MEMORY"
JAVA_OPTS="$JAVA_OPTS -Dspring.profiles.active=dev"
#JAVA_OPTS="$JAVA_OPTS -Dspring.profiles.active=prod"

JAVA_CMD="$JAVA_HOME/bin/java $JAVA_OPTS -jar $APPLICATION"

start() {
  if [ -f "$PID_FILE" ] && kill -0 $(cat "$PID_FILE") 2>/dev/null; then
    echo "$SERVICE_NAME Is Already Running with PID $(cat $PID_FILE)."
    exit 1
  fi
  echo "Starting $SERVICE_NAME..."
  nohup $JAVA_CMD > /dev/null 2>&1 &
  echo $! > "$PID_FILE"
  echo "$SERVICE_NAME started with PID $(cat $PID_FILE)."
}

stop() {
  if [ -f "$PID_FILE" ]; then
    PID=$(cat "$PID_FILE")
    if kill -0 $PID 2>/dev/null; then
      echo "Stopping $SERVICE_NAME with PID $PID..."
      kill $PID
      rm -f "$PID_FILE"
      echo "$SERVICE_NAME stopped."
    else
      echo "Process $PID not found. Cleaning up PID file."
      rm -f "$PID_FILE"
    fi
  else
    echo "No PID file found. Is $SERVICE_NAME running?"
  fi
}

status() {
  if [ -f "$PID_FILE" ] && kill -0 $(cat "$PID_FILE") 2>/dev/null; then
    echo "$SERVICE_NAME is running with PID $(cat $PID_FILE)."
  else
    echo "$SERVICE_NAME is not running."
  fi
}

case "$1" in
  start)
    start
    ;;
  stop)
    stop
    ;;
  status)
    status
    ;;
  *)
    echo "Usage: $0 {start|stop|status}"
    exit 1
    ;;
esac