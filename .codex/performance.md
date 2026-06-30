# 성능 테스트 기록

이 문서는 택배왕 백엔드에서 수행한 성능 테스트 결과와 개선 판단 근거를 기록한다.

## 1. DB 기반 API 권한 검사 성능 테스트

### 배경

기존 `EndPointAuthorizationFilter`는 보호 API 요청이 들어올 때마다 `end_points` 테이블의 전체 권한 정책을 조회했다.

흐름은 다음과 같았다.

1. 요청 URI와 HTTP method 확인
2. 인증 principal의 role 확인
3. `EndPointRepository.findAll()` 호출
4. DB에서 모든 endpoint 권한 정책 조회
5. `AntPathMatcher`로 URL pattern 비교
6. method와 role이 맞으면 통과

이 방식은 운영 중 DB 값을 수정하면 즉시 권한 정책이 반영되는 장점이 있다. 하지만 모든 보호 API 요청마다 같은 권한 테이블을 다시 읽기 때문에, 트래픽이 늘면 DB 부하가 권한 검사 단계에서 반복 발생한다.

### 테스트 목적

- 요청마다 DB 권한 정책을 조회하는 구조의 기준 성능을 측정한다.
- 권한 검사 개선 전후 비교 기준을 남긴다.
- 실제 병목이 발생하기 전, 구조적 위험을 수치로 확인한다.

### 테스트 대상

- Endpoint: `GET /api/v1/notifications/me/unread-count`
- 인증 방식: 로그인 후 발급된 cookie 사용
- 테스트 도구: ApacheBench `ab`
- 서버: 로컬 Spring Boot 서버
- DB: 로컬 개발 MySQL

알림 unread-count API를 선택한 이유는 비즈니스 로직이 비교적 가볍고, 인증/권한 필터를 반드시 통과하는 보호 API이기 때문이다. 즉, 권한 필터 비용을 관찰하기 좋다.

### 테스트 명령

```bash
ab -n 1000 -c 50 -C "accessToken=..." http://localhost:8080/api/v1/notifications/me/unread-count
```

```bash
ab -n 5000 -c 100 -C "accessToken=..." http://localhost:8080/api/v1/notifications/me/unread-count
```

### 기준 성능 측정 결과

#### 1,000 requests / concurrency 50

| 항목 | 결과 |
| --- | ---: |
| Total requests | 1,000 |
| Concurrency | 50 |
| Failed requests | 0 |
| Requests per second | 847.18 req/sec |
| Mean latency | 59 ms |
| p95 latency | 126 ms |
| p99 latency | 184 ms |
| Max latency | 234 ms |

#### 5,000 requests / concurrency 100

| 항목 | 결과 |
| --- | ---: |
| Total requests | 5,000 |
| Concurrency | 100 |
| Failed requests | 0 |
| Requests per second | 1,342.82 req/sec |
| Mean latency | 74 ms |
| p95 latency | 136 ms |
| p99 latency | 180 ms |
| Max latency | 295 ms |

### 해석

로컬 기준 수치만 보면 즉시 장애가 날 수준은 아니다. 하지만 기존 구조는 요청 수만큼 `end_points` 전체 조회가 반복된다.

예를 들어 초당 1,000개의 보호 API 요청이 들어오면, 비즈니스 쿼리와 별개로 권한 검사만으로도 초당 1,000번의 `end_points` 전체 조회가 발생한다. endpoint 수가 늘어나거나 DB connection pool이 바빠지면, 실제 병목은 API 비즈니스 로직보다 권한 필터에서 먼저 생길 수 있다.

따라서 문제는 단순 평균 응답 시간이 아니라 다음 구조적 비용이다.

- 모든 보호 API 요청마다 동일한 권한 정책을 DB에서 반복 조회
- endpoint 수 증가에 따라 권한 검사 비용 증가
- 트래픽 증가 시 DB connection pool 점유 증가
- 권한 테이블은 변경 빈도가 낮은데 읽기 빈도는 매우 높은 불균형

### 개선 방향

권한 정책은 변경 빈도가 낮고, 요청 시점에는 읽기만 필요하다. 따라서 애플리케이션 메모리에 캐시하고, 운영자가 필요할 때 명시적으로 갱신하는 구조가 적합하다.

개선 구조:

1. 서버 시작 시 `EndPointAutoRegistrar`가 컨트롤러의 `@EndpointAccess` 선언을 스캔한다.
2. DB에 없는 신규 endpoint만 annotation 기준 기본 권한으로 등록한다.
3. 이미 DB에 존재하는 endpoint의 roles는 운영 권한 정책으로 보고 자동 덮어쓰지 않는다.
4. 등록 확인 후 `EndPointAuthorizationCache.reload()`를 호출해 DB 권한 정책을 메모리에 적재한다.
5. 요청마다 `EndPointAuthorizationFilter`는 DB를 조회하지 않고 메모리 캐시에서 URL, method, role을 검사한다.
6. 운영 중 DB 권한 정책을 수정한 경우, 관리자 API로 dry-run 후 reload한다.

### 개선 후 운영 API

#### dry-run

```http
GET /api/v1/admin/end-points/cache/dry-run
```

역할:

- DB `end_points` 정책과 현재 메모리 캐시의 차이를 조회한다.
- 실제 캐시는 변경하지 않는다.
- 운영 중 권한 변경 전후 영향 범위를 확인하는 용도다.

응답 주요 필드:

| 필드 | 의미 |
| --- | --- |
| `cacheCount` | 현재 메모리 캐시에 적재된 endpoint 수 |
| `databaseCount` | DB에 저장된 endpoint 수 |
| `hasChanges` | DB와 캐시 사이 변경 여부 |
| `added` | reload 시 캐시에 추가될 endpoint |
| `removed` | reload 시 캐시에서 제거될 endpoint |
| `changed` | roles 또는 description이 변경될 endpoint |

#### reload

```http
POST /api/v1/admin/end-points/cache/reload
```

역할:

- DB `end_points` 정책을 다시 읽어 메모리 캐시를 갱신한다.
- 서버 재시작 없이 운영 중 권한 변경을 반영한다.

응답 주요 필드:

| 필드 | 의미 |
| --- | --- |
| `cacheCount` | reload 후 캐시에 적재된 endpoint 수 |
| `reloadedAt` | 갱신 시각 |

### 개선 후 기대 효과

- 보호 API 요청마다 발생하던 `end_points` 전체 조회 제거
- 권한 검사 비용을 DB I/O에서 메모리 조회로 전환
- DB connection pool 부하 감소
- 권한 정책 운영성 유지
- 서버 재시작 없이 권한 변경 가능
- dry-run으로 운영 반영 전 변경 범위 확인 가능

### 개선 후 성능 측정 결과

측정 일시:

- 2026-06-25 20:08 KST

측정 조건:

- 현재 작업 코드 기준
- 서버 포트: `8081`
- DB: 로컬 개발 MySQL `Logistics_King`
- Redis: 로컬 Redis
- 테스트 계정: 성능 테스트용 `VENDOR` 계정
- Warm-up: `ab -n 100 -c 10`
- 측정 API: `GET /api/v1/notifications/me/unread-count`

측정 명령:

```bash
ab -n 1000 -c 50 -C "accessToken=..." http://127.0.0.1:8081/api/v1/notifications/me/unread-count
```

```bash
ab -n 5000 -c 100 -C "accessToken=..." http://127.0.0.1:8081/api/v1/notifications/me/unread-count
```

#### 1,000 requests / concurrency 50

| 항목 | 개선 전 | 개선 후 | 변화 |
| --- | ---: | ---: | ---: |
| Total requests | 1,000 | 1,000 | - |
| Concurrency | 50 | 50 | - |
| Failed requests | 0 | 0 | - |
| Requests per second | 847.18 req/sec | 1,099.27 req/sec | +29.8% |
| Mean latency | 59 ms | 45 ms | -23.7% |
| p95 latency | 126 ms | 113 ms | -10.3% |
| p99 latency | 184 ms | 177 ms | -3.8% |
| Max latency | 234 ms | 232 ms | -0.9% |

#### 5,000 requests / concurrency 100

| 항목 | 개선 전 | 개선 후 | 변화 |
| --- | ---: | ---: | ---: |
| Total requests | 5,000 | 5,000 | - |
| Concurrency | 100 | 100 | - |
| Failed requests | 0 | 0 | - |
| Requests per second | 1,342.82 req/sec | 2,583.01 req/sec | +92.4% |
| Mean latency | 74 ms | 39 ms | -47.3% |
| p95 latency | 136 ms | 77 ms | -43.4% |
| p99 latency | 180 ms | 127 ms | -29.4% |
| Max latency | 295 ms | 233 ms | -21.0% |

#### 개선 후 로그 관찰

개선 후 부하 테스트 중 Hibernate SQL 로그에서는 알림 unread-count 비즈니스 쿼리가 반복 실행되는 것을 확인했다.

```sql
select
    count(nje1_0.id)
from
    notifications nje1_0
where
    nje1_0.receiver_user_id=?
    and nje1_0.read_at is null
    and nje1_0.created_at>=?
```

반면 요청마다 `end_points` 전체 조회가 반복되는 패턴은 부하 테스트 구간에서 관찰되지 않았다. `end_points` 조회는 서버 시작 시 신규 endpoint 등록 확인과 캐시 reload 단계에서 발생하고, 보호 API 요청 시점의 권한 검사는 메모리 캐시에서 처리된다.

### 결과 해석

개선 후 같은 알림 unread-count API에서 처리량이 증가하고 평균 지연 시간이 감소했다.

특히 `5000/c100` 조건에서 개선 폭이 더 크게 나타났다.

- RPS: `1,342.82` -> `2,583.01`
- Mean latency: `74 ms` -> `39 ms`
- p95 latency: `136 ms` -> `77 ms`
- p99 latency: `180 ms` -> `127 ms`

이는 동시성이 높아질수록 요청마다 DB 권한 정책을 다시 읽는 비용이 더 크게 드러났고, 메모리 캐시 전환으로 그 반복 I/O가 제거되었기 때문으로 해석할 수 있다.

다만 unread-count API 자체도 `notifications` count 쿼리를 수행한다. 따라서 현재 수치는 권한 캐시만의 순수 성능 차이는 아니며, 실제 API 비즈니스 쿼리 비용이 함께 포함된 end-to-end HTTP 성능이다.

### 검증한 테스트

단위 테스트로 다음을 확인했다.

- `EndPointAuthorizationFilter`는 요청마다 repository를 다시 조회하지 않는다.
- `EndPointAuthorizationCache.dryRun()`은 DB와 캐시 차이를 반환하고 캐시는 변경하지 않는다.
- `EndPointAuthorizationCache.reload()`는 DB 기준으로 캐시를 갱신한다.

실행한 검증:

```bash
./gradlew compileKotlin
```

```bash
./gradlew test --tests logisticsking.com.logisticskingbackendspring.infra.security.EndPointAuthorizationCacheTest --tests logisticsking.com.logisticskingbackendspring.infra.security.EndPointAuthorizationFilterTest
```

```bash
./gradlew test
```

```bash
git diff --check
```

### 측정 한계

이번 성능 테스트는 로컬 환경에서 수행했다. 따라서 결과 수치는 운영 성능을 대표하지 않는다.

또한 개선 전 baseline은 기존 8080 서버에서 측정했고, 개선 후 측정은 현재 코드 서버를 8081 포트에 띄워 수행했다. DB와 테스트 API는 같지만 JVM 프로세스, 서버 실행 시점, 로컬 머신 상태는 완전히 동일하게 통제하지 못했다.

따라서 이 결과는 운영 부하 한계치가 아니라, DB 기반 권한 조회 제거의 방향성과 로컬 기준 개선 폭을 확인한 자료로 해석해야 한다.

### 다음 측정 계획

더 정확한 개선 전후 비교를 하려면 다음 순서로 진행한다.

1. 동일 DB, 동일 JVM 옵션, 동일 profile로 서버 실행
2. 같은 테스트 계정으로 로그인 cookie 발급
3. `GET /api/v1/notifications/me/unread-count`를 같은 조건으로 부하 테스트
4. `-n 1000 -c 50`, `-n 5000 -c 100` 결과 비교
5. 가능하면 MySQL general log 또는 datasource proxy로 `end_points` 조회 횟수 비교
6. 비즈니스 쿼리가 없는 테스트 전용 보호 API를 추가해 권한 필터 비용만 분리 측정

비교 시 핵심 지표:

- Requests per second
- Mean latency
- p95 latency
- p99 latency
- Failed requests
- DB `end_points` 조회 횟수
- DB connection pool 사용률

### 결론

DB 기반 권한 관리는 운영자가 권한 정책을 관리하기 쉽다는 장점이 있다. 하지만 요청마다 DB를 조회하는 구조는 트래픽 증가 시 반복 I/O 병목이 될 수 있다.

현재 개선안은 DB를 운영 권한 정책의 source of truth로 두고, `@EndpointAccess`는 신규 API 누락 방지를 위한 최초 등록 기본값으로만 사용한다. 요청 시점에는 DB 권한 정책을 메모리 캐시로 읽고, 운영 변경은 `dry-run`과 `reload` API로 통제한다. 이 방식은 성능과 운영성을 함께 가져가는 절충안이다.
