---
name: jdbctemplate-convention
description: NamedParameterJdbcTemplate 기반 Repository 코드를 작성하거나 리뷰할 때 따라야 하는 사내 코딩 컨벤션. SELECT(단건/목록), INSERT(단건/배치), UPDATE를 JdbcTemplate으로 구현할 때, Repository 클래스를 새로 만들거나 기존 코드를 수정·리뷰할 때 반드시 사용한다. 사용자가 "repository 만들어줘", "이 쿼리 추가해줘", "jdbcTemplate으로 조회/저장/수정", "이 DAO 코드 리뷰해줘" 같은 요청을 하면 이 스킬을 적용한다. SQL을 직접 작성하거나 RowMapper, MapSqlParameterSource, batchUpdate를 다루는 모든 상황에 적용한다.
---

# JdbcTemplate 코딩 컨벤션

`NamedParameterJdbcTemplate`을 사용하는 Repository 계층의 작성 규칙이다. 모든 신규 Repository와 기존 코드 수정은 이 규칙을 따른다. 목적은 (1) SQL을 한눈에 읽히게 하고, (2) 운영 로그에서 쿼리 출처를 추적 가능하게 하며, (3) 파라미터 바인딩과 null 처리 방식을 팀 전체가 동일하게 가져가는 것이다.

## 핵심 원칙

- SQL은 `private final String` 상수로 분리하고, 그 상수를 사용하는 메서드 바로 위에 둔다. 코드에 SQL을 인라인으로 섞지 않는다.
- 모든 파라미터는 named parameter(`:name`)로 바인딩한다. 문자열 연결(`+`)로 값을 끼워 넣지 않는다 (SQL Injection 방지).
- 단건 조회 결과는 `Optional<T>`로 감싸 반환한다. null을 그대로 반환하지 않는다.
- 변경 메서드(INSERT/UPDATE)는 영향받은 행 수(`int`)를 반환한다.
- 트랜잭션은 원칙적으로 서비스 계층에서 관리한다. Repository 메서드의 `@Transactional`은 예외적인 경우에만 붙인다(§9 참고).

---

## 1. 클래스 구조

```java
@Repository
@RequiredArgsConstructor
public class XxxRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    // [SQL 상수 → 메서드] 쌍을 반복 ...
}
```

규칙:

- 클래스에 `@Repository`를 붙인다.
- 생성자 주입을 쓰되 직접 작성하지 말고 Lombok `@RequiredArgsConstructor`로 처리한다.
- 의존성은 `private final NamedParameterJdbcTemplate jdbcTemplate` 한 줄로 선언한다. non-named `JdbcTemplate`을 새로 도입하지 않는다.
- 한 클래스 안에 여러 쿼리가 있을 때는 `[SQL 상수 + 그 상수를 쓰는 메서드]`를 하나의 묶음으로 보고, 묶음끼리 위아래로 배치한다.

---

## 2. SQL 상수 정의 규칙

SQL은 `private final String` 상수로 선언하고 Java 텍스트 블록(`"""`)을 사용한다. 상수를 사용하는 메서드 **바로 위**에 둔다.

```java
private final String FIND_BY_SCHD_ID_SQL = """
    SELECT SCHD_ID /** ApiMsgTransfer */
          ,USER_ID
          ,SEND_TYPE
          ,STATUS
    FROM API_SCHD_INFO
    WHERE DEL_YN = 'N'
      AND SCHD_ID = :schdId
""";
```

### 2-1. 상수 네이밍

- **SQL 상수명은 `대문자_스네이크` 표기에 `_SQL` 접미사를 붙인다.**
    - 예: `SAVE_SQL`, `FIND_BY_SCHD_ID_SQL`, `UPDATE_SCHD_STATUS_SQL`
- **그 상수를 사용하는 메서드는 상수명에서 `_SQL`을 뗀 동일한 이름을 camelCase로 쓴다.** 즉 상수와 메서드가 1:1로 같은 베이스 이름을 공유한다.
    - `SAVE_SQL` ↔ `save()`
    - `FIND_BY_SCHD_ID_SQL` ↔ `findBySchdId()`
    - `UPDATE_SCHD_STATUS_SQL` ↔ `updateSchdStatus()`
- **예외 — 하나의 SQL 상수를 여러 메서드가 재사용하는 경우**(예: 배치 메서드가 단건 INSERT의 SQL을 재사용): 1:1 네이밍 규칙은 그 SQL의 대표 메서드에만 적용하고, 재사용하는 쪽 메서드는 동작에 맞는 이름을 따로 짓는다(예: `SAVE_SQL`을 재사용하는 배치 메서드 `saveList()`).

### 2-2. SQL 포맷

1. **키워드·컬럼명·테이블명은 대문자.** `SELECT`, `FROM`, `WHERE`, 컬럼명, 테이블명 모두 대문자.
2. **선행 콤마(leading comma) 스타일** — 컬럼이나 `SET` 절을 **여러 줄로 나열할 때만** 적용한다. 콤마를 줄 앞에 붙여 컬럼 추가/삭제 diff를 깔끔하게 하고 콤마 누락을 줄인다.
   ```sql
   SELECT SCHD_ID
         ,USER_ID
         ,STATUS
   ```
    - INSERT의 컬럼/값 목록처럼 한 줄(또는 소수의 줄)로 적는 경우는 일반 콤마 구분으로 둔다(§6 참고).
3. **소프트 삭제 필터** — 논리 삭제(`DEL_YN`)를 쓰는 테이블은 조회 시 `DEL_YN = 'N'` 조건을 포함한다. `WHERE` 절 내 위치(맨 앞/뒤)는 강제하지 않는다.

### 2-3. 쿼리 식별 주석 (권장)

`SELECT` 첫 컬럼 뒤(또는 `INSERT INTO 테이블명` 뒤)에 `/** 식별자 */` 형태로 쿼리 출처를 남긴다. 운영 DB의 slow query 로그 등에서 어느 기능이 실행한 쿼리인지 추적하기 위함이다.

```sql
SELECT SCHD_ID /** ApiMsgTransfer */
```
```sql
INSERT INTO API_MSG_SENT_LIST /** apiMsgTransfer */
```

> ⚠️ 현재 코드베이스에서 이 주석은 일관되게 적용되어 있지 않다(SELECT엔 대체로 있으나 UPDATE엔 없는 경우가 많고, 식별자 대소문자도 `ApiMsgTransfer`/`apiMsgTransfer`로 섞여 있음). **추적 효과를 보려면 모든 쿼리에 일관되게 적용하고 식별자 표기를 하나로 통일하는 것을 권장한다.** (팀에서 표기 규칙을 정해주면 이 섹션에 명시하겠음.)

---

## 3. 파라미터 바인딩 규칙

`MapSqlParameterSource`를 `SqlParameterSource` 타입으로 받아 named parameter에 바인딩한다.

```java
SqlParameterSource params = new MapSqlParameterSource()
        .addValue("status", status)
        .addValue("schdId", schdId);
```

- 변수명은 `SqlParameterSource params`로 통일한다.
- `.addValue("키", 값)` 체이닝으로 작성하고, 키 이름은 SQL의 `:키`와 정확히 일치시킨다.
- 값을 SQL 문자열에 직접 연결하는 것(`"... = " + value`)은 금지한다.

---

## 4. SELECT — 단건 조회

단건은 `Optional<T>`를 반환하고, 결과가 없을 때 발생하는 `EmptyResultDataAccessException`을 잡아 `Optional.empty()`로 변환한다.

```java
public Optional<ApiSchdInfo> findBySchdId(Long schdId) {
    SqlParameterSource params = new MapSqlParameterSource()
            .addValue("schdId", schdId);

    try {
        return Optional.ofNullable(jdbcTemplate.queryForObject(FIND_BY_SCHD_ID_SQL, params, (rs, rowNum) ->
                ApiSchdInfo.builder()
                        .schdId(rs.getLong("SCHD_ID"))
                        .userId(rs.getString("USER_ID"))
                        .sendType(rs.getString("SEND_TYPE"))
                        .registerDate(rs.getTimestamp("REGISTER_DATE") != null
                                ? rs.getTimestamp("REGISTER_DATE").toLocalDateTime() : null)
                        .reserveDate(rs.getTimestamp("RESERVE_DATE") != null
                                ? rs.getTimestamp("RESERVE_DATE").toLocalDateTime() : null)
                        .build()
        ));
    } catch (EmptyResultDataAccessException e) {
        return Optional.empty();
    }
}
```

규칙:

- 반환 타입은 `Optional<T>`.
- `queryForObject(...)` 결과를 `Optional.ofNullable(...)`로 감싼다.
- 결과가 없으면 `queryForObject`가 `EmptyResultDataAccessException`을 던지므로 `try-catch`로 잡아 `Optional.empty()`를 반환한다.
- RowMapper는 인라인 람다 `(rs, rowNum) -> ...`로 작성하고 엔티티는 빌더 패턴으로 매핑한다.
- `rs.getXxx("컬럼명")`의 컬럼명은 SQL과 동일하게 대문자.
- **timestamp/date 매핑은 null-safe하게.** `rs.getTimestamp(...)`는 null일 수 있으므로 `!= null` 체크 후 `.toLocalDateTime()`을 호출한다.

---

## 5. SELECT — 목록 조회

여러 건은 `query(...)`로 조회하고 `List<T>`를 반환한다. 결과가 없으면 빈 리스트가 반환되므로 별도 예외 처리가 필요 없다.

```java
@Transactional(readOnly = true)
public List<ApiMsgTargetList> findBySchdId(long schdId) {
    SqlParameterSource params = new MapSqlParameterSource()
            .addValue("schdId", schdId);

    return jdbcTemplate.query(FIND_BY_SCHD_ID_SQL, params, (rs, rowNum) ->
            ApiMsgTargetList.builder()
                    .targetId(rs.getLong("TARGET_ID"))
                    .schdId(rs.getLong("SCHD_ID"))
                    .phoneNumber(rs.getString("PHONE_NUMBER"))
                    .mappingVarsJson(rs.getString("MAPPING_VARS_JSON"))
                    .build()
    );
}
```

규칙:

- 반환 타입은 `List<T>`. 빈 리스트가 자연스럽게 반환되므로 `Optional`로 감싸지 않는다.
- RowMapper는 단건과 동일한 람다 + 빌더 방식을 재사용한다.

---

## 6. INSERT — 단건

`update(...)`로 실행하고 영향받은 행 수(`int`)를 반환한다.

```java
private final String SAVE_SQL = """
    INSERT INTO API_MSG_SENT_LIST /** apiMsgTransfer */
        ( TARGET_ID, SCHD_ID, MSG_TYPE, SEND_NUMBER, PHONE_NUMBER, TITLE, CONTENT, REGISTER_DATE, RESULT_CODE, TRANSFER_ID )
    VALUES
        ( :targetId, :schdId, :msgType, :sendNumber, :phoneNumber, :title, :content, :registerDate, :resultCode, :transferId )
""";

public int save(ApiMsgSentList apiMsgSentList) {
    SqlParameterSource params = new MapSqlParameterSource()
            .addValue("targetId", apiMsgSentList.getTargetId())
            .addValue("schdId", apiMsgSentList.getSchdId())
            // ... 나머지 컬럼
            .addValue("transferId", apiMsgSentList.getTransferId());

    return jdbcTemplate.update(SAVE_SQL, params);
}
```

규칙:

- 반환 타입은 `int`(처리된 행 수).
- **컬럼 목록과 `VALUES` 목록은 한 줄(또는 소수의 줄)로 적고 일반 콤마로 구분한다.** 컬럼 순서와 `VALUES`의 named parameter 순서를 1:1로 맞춰 가독성을 유지한다.
- 단건 INSERT 메서드에는 보통 `@Transactional`을 붙이지 않는다(트랜잭션은 서비스 계층에서 관리). §9 참고.

---

## 7. INSERT — 배치(대량)

대량 삽입은 `batchUpdate(...)`에 `SqlParameterSource[]`를 넘긴다. 한 번에 너무 많은 양을 보내지 않도록 `batchSize` 단위로 나눠 처리한다. INSERT SQL 상수는 단건과 **동일한 상수를 재사용**한다.

```java
@Transactional(propagation = Propagation.REQUIRES_NEW)
public int saveList(List<ApiMsgSentList> apiMsgSentLists, int batchSize) {
    int totalInserted = 0;
    for (int i = 0; i < apiMsgSentLists.size(); i += batchSize) {
        List<ApiMsgSentList> batch = apiMsgSentLists.subList(
                i, Math.min(i + batchSize, apiMsgSentLists.size())
        );
        SqlParameterSource[] batchParams = new SqlParameterSource[batch.size()];
        for (int j = 0; j < batch.size(); j++) {
            ApiMsgSentList item = batch.get(j);
            batchParams[j] = new MapSqlParameterSource()
                    .addValue("targetId", item.getTargetId())
                    // ... 나머지 컬럼
                    .addValue("transferId", item.getTransferId());
        }
        int[] inserted = jdbcTemplate.batchUpdate(SAVE_SQL, batchParams);
        totalInserted += inserted.length;
    }
    return totalInserted;
}
```

규칙:

- 단건 INSERT의 SQL 상수(`SAVE_SQL`)를 그대로 재사용한다. 메서드명은 단건 `save()`와 구분되도록 `saveList()`처럼 동작에 맞게 짓는다(§2-1 예외 규칙).
- 입력 리스트를 `batchSize`로 잘라 `subList` → `SqlParameterSource[]` 구성 → `batchUpdate` 순으로 처리한다.
- 누적 처리 건수를 `int`로 반환한다.
- 배치 작업이 호출 트랜잭션과 독립적으로 커밋돼야 하면 `@Transactional(propagation = Propagation.REQUIRES_NEW)`를 붙인다.

---

## 8. UPDATE

```java
private final String UPDATE_SCHD_STATUS_SQL = """
    UPDATE API_SCHD_INFO
    SET STATUS = :status
       ,FILTER_CNT = :filterCnt
       ,SENT_CNT = :sentCnt
       ,TRANSFER_DATE = :transferDate
    WHERE SCHD_ID = :schdId
""";

public int updateSchdStatus(ApiSchdInfo apiSchdInfo) {
    SqlParameterSource params = new MapSqlParameterSource()
            .addValue("status", apiSchdInfo.getStatus())
            .addValue("filterCnt", apiSchdInfo.getFilterCnt())
            .addValue("sentCnt", apiSchdInfo.getSentCnt())
            .addValue("transferDate", apiSchdInfo.getTransferDate())
            .addValue("schdId", apiSchdInfo.getSchdId());
    return jdbcTemplate.update(UPDATE_SCHD_STATUS_SQL, params);
}
```

규칙:

- `update(...)`로 실행하고 `int`를 반환한다.
- `SET` 절이 여러 컬럼이면 선행 콤마 스타일로 정렬한다. 한 컬럼이면 한 줄로 둔다(`SET STATUS = :status`).
- **`WHERE` 조건을 반드시 명시한다.** 조건 없는 전체 UPDATE는 금지한다.
- 단건 UPDATE 메서드에는 보통 `@Transactional`을 붙이지 않는다(서비스 계층 관리). §9 참고.

---

## 9. 트랜잭션 규칙

이 코드베이스는 **트랜잭션을 원칙적으로 서비스 계층에서 관리**한다. 따라서 Repository 메서드의 `@Transactional`은 대부분 생략되며, 다음 경우에만 선택적으로 붙인다:

- **독립 커밋이 필요한 배치 작업**: `@Transactional(propagation = Propagation.REQUIRES_NEW)` (§7)
- **읽기 전용 조회를 명시하고 싶을 때**: `@Transactional(readOnly = true)` — 현재 코드에서는 일부 조회에만 적용되어 있다.
- 어노테이션은 `org.springframework.transaction.annotation.Transactional`을 사용한다(Jakarta/JTA 버전 아님).

> ⚠️ 현재 조회 메서드의 `readOnly = true` 적용이 일관되지 않다(어떤 단건 조회엔 없고 어떤 목록 조회엔 있음). **모든 조회에 `readOnly = true`를 붙일지 / 서비스 계층에만 둘지 팀 정책을 정하는 것을 권장한다.**

---

## 10. DELETE (예시 미확보 — 확인 필요)

> 현재 받은 코드에는 DELETE 메서드가 없다. 다만 모든 테이블에 `DEL_YN` 컬럼이 있고 조회 시 `DEL_YN = 'N'`으로 거르는 것으로 보아 **논리 삭제(soft delete)** 를 쓸 가능성이 높다. 실제 삭제 패턴(물리 삭제 vs `UPDATE SET DEL_YN = 'Y'`)을 알려주면 이 섹션을 확정하겠다. 어느 쪽이든 `update(...)` 호출 + `int` 반환 규칙은 동일하게 적용한다.

---

## 11. 작성·리뷰 체크리스트

- [ ] `@Repository` + `@RequiredArgsConstructor` + `private final NamedParameterJdbcTemplate`
- [ ] SQL 상수가 `대문자_스네이크` + `_SQL` 접미사이고, 메서드는 `_SQL`을 뗀 동일 베이스 이름(camelCase)인가
- [ ] SQL이 텍스트 블록 상수로 분리되고, 사용하는 메서드 바로 위에 있는가
- [ ] SQL 키워드·컬럼명이 대문자인가
- [ ] 여러 줄 컬럼/SET 나열에 선행 콤마 스타일을 쓰는가 (INSERT 컬럼·값 목록은 한 줄 일반 콤마)
- [ ] 쿼리 식별 주석 `/** 식별자 */`을 (가능하면 일관되게) 남겼는가
- [ ] 모든 값이 named parameter(`:name`)로 바인딩되고 문자열 연결이 없는가
- [ ] 조회 시 `DEL_YN = 'N'` 필터가 있는가 (해당 테이블인 경우)
- [ ] 단건 조회가 `Optional<T>` + `EmptyResultDataAccessException` 처리를 따르는가
- [ ] timestamp/date 매핑이 null-safe한가
- [ ] 목록 조회가 `List<T>`를 반환하는가
- [ ] INSERT/UPDATE가 `int`(행 수)를 반환하는가
- [ ] 대량 INSERT가 `batchUpdate` + `batchSize` 분할 패턴을 따르는가
- [ ] UPDATE에 `WHERE` 조건이 있는가
- [ ] 트랜잭션이 서비스 계층 원칙을 따르고, Repository엔 필요한 경우(REQUIRES_NEW 배치, readOnly 조회)만 붙었는가