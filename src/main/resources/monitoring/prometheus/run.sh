#!/bin/bash

BASE_DIR="$(cd "$(dirname "$0")" && pwd)"

PROMETHEUS_BIN="${BASE_DIR}/prometheus"
CONFIG_FILE="${BASE_DIR}/prometheus.yml"
DATA_DIR="${BASE_DIR}/data"
LOG_FILE="${BASE_DIR}/prometheus.log"

mkdir -p "${DATA_DIR}"

echo "Starting Prometheus..."
echo "Config : ${CONFIG_FILE}"
echo "Data   : ${DATA_DIR}"
echo "Log    : ${LOG_FILE}"

nohup "${PROMETHEUS_BIN}" \
  --config.file="${CONFIG_FILE}" \
  --storage.tsdb.path="${DATA_DIR}" \
  > "${LOG_FILE}" 2>&1 &

PID=$!

sleep 2

if ps -p ${PID} > /dev/null 2>&1; then
    echo "Prometheus started successfully. PID=${PID}"
else
    echo "Failed to start Prometheus."
    echo "Check log: ${LOG_FILE}"
fi