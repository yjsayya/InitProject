GRAFANA_HOME="/app/grafana-v10.3.0"
BINARY="./grafana-server"
LOG_FILE="grafana_run.log"
# ----------------------------------------------

cd $GRAFANA_HOME/bin

case "$1" in
    start)
        # 실행 중인지 확인
        PID=$(pgrep -f grafana-server)
        if [ -z "$PID" ]; then
            echo "Grafana를 백그라운드에서 시작합니다..."
            nohup $BINARY web > ../$LOG_FILE 2>&1 &
            echo "시작 완료! (로그: $LOG_FILE)"
        else
            echo "Grafana가 이미 실행 중입니다. (PID: $PID)"
        fi
        ;;
    stop)
        PID=$(pgrep -f grafana-server)
        if [ -z "$PID" ]; then
            echo "중지할 Grafana 프로세스가 없습니다."
        else
            echo "Grafana(PID: $PID)를 종료합니다..."
            kill $PID
            echo "종료되었습니다."
        fi
        ;;
    status)
        PID=$(pgrep -f grafana-server)
        if [ -z "$PID" ]; then
            echo "Grafana가 정지된 상태입니다."
        else
            echo "Grafana가 실행 중입니다. (PID: $PID)"
            netstat -plnt | grep 3000
        fi
        ;;
    log)
        tail -f ../$LOG_FILE
        ;;
    *)
        echo "사용법: $0 {start|stop|status|log}"
        exit 1
esac

exit 0