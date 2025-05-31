#!/bin/bash

# 1. Java Home
JAVA_HOME=/app/java/jdk1.8.0  # JAVA 설치 경로 수정 필요

# 2. Application 설정
APP_HOME=$(pwd)
APP_NAME="jar파일이름"  # 실제 JAR 파일 이름으로 수정 필요
APPLICATION="$APP_HOME/$APP_NAME"
SERVICE_NAME=서비스이름  # 서비스 이름 지정

# 3. Java Options
MIN_HEAP_MEMORY=1024m
MAX_HEAP_MEMORY=1024m

JAVA_OPTS="-server"
JAVA_OPTS="$JAVA_OPTS -Dservice.name=$SERVICE_NAME"
JAVA_OPTS="$JAVA_OPTS -Xms$MIN_HEAP_MEMORY -Xmx$MAX_HEAP_MEMORY"
JAVA_OPTS="$JAVA_OPTS -Dspring.profiles.active=dev"
# JAVA_OPTS="$JAVA_OPTS -Dspring.profiles.active=prod"

# 4. PID 파일
PID_FILE="$SERVICE_NAME.pid"

# 5. 실행 명령어
JAVA_CMD="$JAVA_HOME/bin/java $JAVA_OPTS -jar $APPLICATION"



start() {
  if [ -f "$PID_FILE" ] && kill -0 $(cat "$PID_FILE") 2>/dev/null; then
    echo "$SERVICE_NAME is already running with PID $(cat $PID_FILE)."
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
    if kill -0 "$PID" 2>/dev/null; then
      echo "Stopping $SERVICE_NAME with PID $PID..."
      kill "$PID"
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

run() {
  if [ -f "$PID_FILE" ] && kill -0 $(cat "$PID_FILE") 2>/dev/null; then
    echo "$SERVICE_NAME is already running with PID $(cat $PID_FILE)."
    exit 1
  fi
  echo "Running $SERVICE_NAME in foreground..."
  exec $JAVA_CMD
}

print() {
  echo "Java CMD:"
  echo "$JAVA_CMD"
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
  run)
    run
    ;;
  print)
    print
    ;;
  *)
    echo "Usage: $0 {start|stop|status|run|print}"
    exit 1
    ;;
esac