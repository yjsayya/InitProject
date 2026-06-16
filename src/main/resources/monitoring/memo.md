# Spring Boot 3.x 대용량 트래픽 관제 가이드

## 목표

부하 테스트 시 단순히 CPU나 메모리를 보는 것이 아니라,

- 사용자가 느끼는 응답 성능
- Spring Boot 처리 성능
- JVM 상태
- DB 병목
- 서버 자원

을 종합적으로 분석하여 병목 지점을 찾는 것을 목표로 한다.

---

# 관제 우선순위

관제는 아래 순서대로 확인한다.

```text
사용자 응답
↓
Spring Boot
↓
Tomcat
↓
DB Pool
↓
JVM
↓
OS
```

---

# 1. 사용자 관점

## TPS (Throughput)

초당 처리 요청 수

```promql
rate(http_server_requests_seconds_count[1m])
```

확인 항목

- 현재 TPS
- 목표 TPS
- TPS 증가 시 응답시간 변화

---

## P95 응답시간

95% 요청이 이 시간 안에 처리됨

```promql
histogram_quantile(
  0.95,
  rate(http_server_requests_seconds_bucket[5m])
)
```

기준

- 500ms 이하 : 양호
- 1초 이상 : 주의
- 3초 이상 : 위험

---

## P99 응답시간

```promql
histogram_quantile(
  0.99,
  rate(http_server_requests_seconds_bucket[5m])
)
```

실무에서 가장 중요

평균 응답시간보다 P99를 우선 확인

---

## 에러율

```promql
rate(
  http_server_requests_seconds_count{
    status=~"5.."
  }[1m]
)
```

기준

- 0% : 정상
- 0.1% 이상 : 확인 필요
- 1% 이상 : 장애

---

# 2. Spring Boot

## 상태코드 분포

```promql
sum by(status)(
  rate(http_server_requests_seconds_count[1m])
)
```

확인 항목

- 200
- 400
- 404
- 500

---

## API 별 응답시간

```promql
histogram_quantile(
  0.95,
  rate(http_server_requests_seconds_bucket[5m])
)
```

확인 항목

- /send
- /message
- /update
- /login

어떤 API가 느린지 확인

---

# 3. Tomcat

## Busy Thread

```promql
tomcat_threads_busy
```

---

## Max Thread

```promql
tomcat_threads_config_max
```

---

## Thread 사용률

```promql
(
 tomcat_threads_busy
 /
 tomcat_threads_config_max
) * 100
```

기준

- 70% 이하 : 정상
- 80% 이상 : 주의
- 90% 이상 : 위험

---

# 4. DB Pool

## Active Connection

```promql
hikaricp_connections_active
```

---

## Pending Connection

```promql
hikaricp_connections_pending
```

---

## Connection Pool 사용률

```promql
(
 hikaricp_connections_active
 /
 hikaricp_connections_max
) * 100
```

기준

- 70% 이하 : 정상
- 80% 이상 : 주의
- 90% 이상 : 위험

Pending이 0보다 크면 병목 가능성 높음

---

# 5. JVM

## Heap 사용률

```promql
(
 jvm_memory_used_bytes{area="heap"}
 /
 jvm_memory_max_bytes{area="heap"}
) * 100
```

기준

- 70% 이하 : 정상
- 80% 이상 : 주의
- 90% 이상 : 위험

---

## GC 횟수

```promql
rate(jvm_gc_pause_seconds_count[1m])
```

---

## GC 시간

```promql
rate(jvm_gc_pause_seconds_sum[1m])
```

GC 시간이 증가하면

- Heap 부족
- 객체 생성 과다
- 메모리 누수

가능성 확인

---

## Live Thread

```promql
jvm_threads_live_threads
```

급격히 증가하면 Thread Leak 의심

---

# 6. Linux OS

(Node Exporter 필요)

## CPU 사용률

```promql
100 - (
 avg(rate(node_cpu_seconds_total{mode="idle"}[1m])) * 100
)
```

---

## Memory 사용률

```promql
(
 1 -
 (
  node_memory_MemAvailable_bytes
  /
  node_memory_MemTotal_bytes
 )
) * 100
```

---

## Load Average

```promql
node_load1
```

CPU Core 수와 비교

예)

8 Core 서버

Load Average 8 이상이면 포화 상태

---

## Disk 사용률

```promql
(
 100 -
 (
  node_filesystem_avail_bytes
  /
  node_filesystem_size_bytes
 ) * 100
)
```

---

# 부하 테스트 시 분석 순서

1. TPS 증가
2. P95 확인
3. P99 확인
4. 5xx 발생 확인
5. Tomcat Thread 확인
6. Hikari Pool 확인
7. JVM Heap 확인
8. GC 확인
9. CPU 확인
10. 병목 지점 분석

---

# 최종 관제 TOP 10

1. TPS
2. P95 Latency
3. P99 Latency
4. 5xx Error Rate
5. Tomcat Busy Thread
6. Hikari Active Connection
7. Hikari Pending Connection
8. JVM Heap Usage
9. GC Pause Time
10. CPU Usage

이 10개만 제대로 관찰해도 대부분의 성능 병목을 분석할 수 있다.