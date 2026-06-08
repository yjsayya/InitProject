---
name: entity-convention
description: DB 테이블에 매핑되는 Entity(순수 POJO) 클래스를 작성하거나 리뷰할 때 따라야 하는 사내 코딩 컨벤션. DDL을 보고 엔티티 클래스를 만들거나, 컬럼을 추가/수정하거나, 기존 엔티티를 리뷰할 때 반드시 사용한다. 사용자가 "엔티티 만들어줘", "이 테이블 매핑 클래스 작성", "DDL을 엔티티로", "VO/DTO 클래스 작성", "이 엔티티 리뷰해줘" 같은 요청을 하거나 BIGINT/DATETIME/Y·N 컬럼을 자바 타입으로 매핑하는 상황에 적용한다. 이 엔티티는 JdbcTemplate RowMapper 및 MyBatis resultMap과 함께 쓰이는 POJO이며 JPA `@Entity`가 아니다.
---

# Entity(POJO) 코딩 컨벤션

DB 테이블에 매핑되는 엔티티 클래스의 작성 규칙이다. 이 엔티티는 **JPA `@Entity`가 아닌 순수 POJO**로, JdbcTemplate의 RowMapper나 MyBatis의 resultMap이 채워 넣는 단순 데이터 보관 객체다. 따라서 JPA 어노테이션(`@Entity`, `@Id`, `@Column` 등)이나 필드 어노테이션은 붙이지 않는다.

## 핵심 원칙

1. **Lombok 어노테이션은 정해진 한 세트로 통일한다.** 모든 엔티티가 동일한 어노테이션 블록을 같은 순서로 사용한다(§1).
2. **DDL → 자바 타입 매핑 규칙을 고정한다.** 특히 `BIGINT → Long`, `DATETIME → LocalDateTime`(§2).
3. **주석을 달지 않는다.** 필드/클래스에 Javadoc·라인 주석을 넣지 않는다(§4).
4. **필드명은 컬럼명을 camelCase로 변환**하고, 어노테이션 없는 순수 필드로 선언한다(§3).

---

## 1. Lombok 어노테이션 (고정 세트)

모든 엔티티 클래스 위에 **아래 6개를 이 순서대로** 붙인다. 엔티티마다 다른 조합을 쓰거나 `@Data`로 대체하지 않는다.

```java
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ApiAccount {
    ...
}
```

- `@Builder`는 전체 필드 생성자를 필요로 하므로 `@AllArgsConstructor`와 함께 둔다.
- 프레임워크(매핑 등)가 기본 생성자를 요구하므로 `@NoArgsConstructor`도 함께 둔다.
- `@Data`는 사용하지 않는다(equals/hashCode 자동 생성 등 의도치 않은 동작을 피하고, 위 세트로 명시적으로 통일하기 위함).

---

## 2. DDL → 자바 타입 매핑

| DDL 타입 | 자바 타입 | 비고 |
|----------|-----------|------|
| `BIGINT` | `Long` | PK 등. ⚠️ 아래 주석 참고 |
| `INT` / `INTEGER` | `int` | 기본형 |
| `VARCHAR` / `CHAR` / `TEXT` | `String` | |
| `CHAR(1)` Y/N 플래그 | `String` | **boolean이 아니라 `String`** (`"Y"`/`"N"`) |
| JSON 컬럼 | `String` | JSON 문자열을 그대로 보관 (`...Json` 접미사) |
| `DATETIME` / `TIMESTAMP` | `LocalDateTime` | |
| `TIME` | `LocalTime` | |
| `DATE` | `LocalDate` | (예시엔 없음 — 표준 권장) |
| `DECIMAL` / `NUMERIC` | `BigDecimal` | (예시엔 없음 — 표준 권장) |

규칙:

- **`BIGINT`는 `Long`, `DATETIME`은 `LocalDateTime`으로 매핑한다.**
- **Y/N 플래그 컬럼은 `String`으로 둔다.** `blockYn`, `delYn` 등은 `"Y"`/`"N"` 문자열을 그대로 들고 있는다(boolean으로 변환하지 않는다).
- **JSON 컬럼은 `String`으로 둔다.** 필드명에 `Json` 접미사를 붙인다(예: `sendPriceJson`).
- 날짜/시간은 `java.util.Date`/`Timestamp`가 아니라 `java.time` 타입(`LocalDateTime`/`LocalDate`/`LocalTime`)을 쓴다.

> ⚠️ **`Long` vs `long`**: 규칙은 "BIGINT → long"으로 말씀하셨지만 예시 코드는 래퍼 타입 `Long id`를 씁니다. 본 문서는 예시를 따라 **`BIGINT → Long`(래퍼)** 로 잡았습니다. null을 표현할 수 없는 기본형 `long`으로 강제하려면 이 항목을 바꿔야 합니다. (PK는 insert 전 null일 수 있어 보통 래퍼 `Long`을 씁니다.) 어느 쪽으로 통일할지 확정 바랍니다.
>
> ※ `DATE`/`DECIMAL` 행은 예시에 없어 일반 표준으로 채운 것이니, 사내에서 다르게 쓰면 알려주세요.

---

## 3. 필드 네이밍 · 선언 · 정렬

- **필드명은 컬럼명(`UPPER_SNAKE`)을 camelCase로 변환**한다. (`USER_ID` → `userId`, `SEND_PRICE_JSON` → `sendPriceJson`)
- 필드는 `private`로 선언하고 **어노테이션을 붙이지 않는다**(순수 POJO).
- 의미가 같은 필드끼리 묶고 **그룹 사이를 빈 줄로 구분**한다(아래 예시의 그룹 구분 참고).
- **감사(audit) 필드 `delYn`, `regDt`, `uptDt`는 클래스 맨 아래**에 모아 둔다.
- 필드 순서는 가급적 테이블 컬럼 순서를 따른다.

---

## 4. 주석 금지

- 엔티티 클래스와 필드에 **주석(Javadoc, 라인 주석)을 달지 않는다.** 필드명과 타입으로 의미가 드러나도록 한다.

---

## 5. 전체 예시

```java
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ApiAccount {

    private Long id;
    private String userId;
    private String apiKey;

    private String sendPriceJson;
    private int dailySendLimitCnt;
    private LocalTime dailyLimitStartTime;
    private LocalTime dailyLimitEndTime;
    private int sendLimtCntPerReq;

    private String blockYn;
    private String checkIpYn;
    private String checkSendYn;
    private String checkSendNumberYn;
    private String checkPrePaymentYn;

    private String delYn;
    private LocalDateTime regDt;
    private LocalDateTime uptDt;

}
```

---

## 6. 작성·리뷰 체크리스트

- [ ] Lombok이 고정 세트(`@Getter @Setter @Builder @ToString @NoArgsConstructor @AllArgsConstructor`)를 같은 순서로 쓰는가 (`@Data` 미사용)
- [ ] JPA/필드 어노테이션 없이 순수 POJO인가
- [ ] `BIGINT → Long`, `DATETIME → LocalDateTime`으로 매핑됐는가
- [ ] Y/N 플래그가 `String`인가 (boolean 아님)
- [ ] JSON 컬럼이 `String`이고 `Json` 접미사를 쓰는가
- [ ] 날짜/시간이 `java.time` 타입인가
- [ ] 필드명이 컬럼명의 camelCase 변환인가
- [ ] 클래스/필드에 주석이 없는가
- [ ] 필드가 논리 그룹별로 빈 줄 구분되고, `delYn`/`regDt`/`uptDt`가 맨 아래에 있는가