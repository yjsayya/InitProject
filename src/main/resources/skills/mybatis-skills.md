---
name: mybatis-convention
description: MyBatis 기반 Mapper 코드를 작성하거나 리뷰할 때 따라야 하는 사내 코딩 컨벤션. Mapper 인터페이스(`XxxMapper.java`)와 매퍼 XML(`XxxMapper.xml`) 한 쌍으로 SELECT/INSERT/UPDATE/DELETE를 구현할 때, Mapper를 새로 만들거나 기존 코드를 수정·리뷰할 때 반드시 사용한다. 사용자가 "mapper 만들어줘", "이 쿼리 매퍼에 추가해줘", "mybatis로 조회/저장/수정/삭제", "이 매퍼 XML 리뷰해줘", "@Mapper 인터페이스 작성" 같은 요청을 하면 이 스킬을 적용한다. resultMap, @Param, namespace, `#{}` 바인딩을 다루는 모든 상황에 적용한다.
---

# MyBatis 코딩 컨벤션

MyBatis 매퍼 계층의 작성 규칙이다. 하나의 매퍼는 **인터페이스(`XxxMapper.java`) + XML(`XxxMapper.xml`)** 한 쌍으로 구성한다. 모든 신규 매퍼와 기존 코드 수정은 이 규칙을 따른다.

## 핵심 원칙

1. **메서드 네이밍은 Spring Data JPA 명명 규칙을 따른다.** (`findById`, `findByUserId`, `findAllByStatus`, `existsByUserId`, `countByStatus`, `save`, `deleteById` 등) 단, MyBatis는 JPA처럼 이름에서 쿼리를 자동 생성하지 않으므로 **SQL은 직접 작성**해야 한다. JPA 네이밍은 "읽히는 메서드 이름 어휘"를 통일하기 위한 규칙이다.
2. **단건 조회는 반드시 `Optional<T>`로 반환한다.** null을 그대로 반환하지 않는다.
3. **파라미터는 반드시 `@Param`으로 명시하고, XML에는 `parameterType`을 쓰지 않는다.** `@Param`으로 이름이 정해지면 `parameterType`이 불필요하다.
4. **INSERT/UPDATE/DELETE에서 변수가 3개 이상이면 개별 파라미터 대신 해당 엔티티를 파라미터로 넘긴다.** (이때도 `@Param`으로 엔티티에 이름을 붙인다 — 원칙 3 유지.)

> ※ 규칙 [5]는 아직 비어 있음. 추가 규칙이 정해지면 이 목록과 본문에 반영한다.

---

## 1. Mapper 인터페이스 규칙

```java
@Mapper
public interface ApiSendValidationConfigMapper {

    Optional<ApiSendValidationConfig> findByUserId(@Param("userId") String userId);

}
```

규칙:

- 인터페이스에 `@Mapper`를 붙인다.
- 메서드명은 JPA 명명 규칙을 따른다(§4 참고).
- **단건 조회 메서드의 반환 타입은 `Optional<T>`.** 목록은 `List<T>`, 존재 여부는 `boolean`, 건수는 `long`/`int`, 변경(INSERT/UPDATE/DELETE)은 `int`(영향 행 수).
- **모든 파라미터에 `@Param("이름")`을 붙인다.** 파라미터가 하나여도 붙인다. 여기서 정한 이름이 곧 XML의 `#{이름}`이 된다.
- select문(find) 메서드들이 위에 그리고 insert,update,delete문이 순차적으로 나오게 구성한다

---

## 2. 매퍼 XML 규칙

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="bizapi.gateway.common.db.mapper.ApiSendValidationConfigMapper">

    <resultMap id="apiSendValidationConfigMap" type="bizapi.common.entity.ApiSendValidationConfig">
        <result property="id"                   column="ID"/>
        <result property="userId"               column="USER_ID"/>
        <result property="commonSendConfigJson" column="COMMON_SEND_CONFIG_JSON"/>
        <result property="emailSendConfigJson"  column="EMAIL_SEND_CONFIG_JSON"/>
        <result property="msgSendConfigJson"    column="MSG_SEND_CONFIG_JSON"/>
        <result property="alSendConfigJson"     column="AL_SEND_CONFIG_JSON"/>
        <result property="bmSendConfigJson"     column="BM_SEND_CONFIG_JSON"/>
        <result property="delYn"                column="DEL_YN"/>
        <result property="regDt"                column="REG_DT"/>
        <result property="uptDt"                column="UPT_DT"/>
    </resultMap>

    <sql id="columns">
        ID
        ,USER_ID
        ,COMMON_SEND_CONFIG_JSON
        ,EMAIL_SEND_CONFIG_JSON
        ,MSG_SEND_CONFIG_JSON
        ,AL_SEND_CONFIG_JSON
        ,BM_SEND_CONFIG_JSON
        ,DEL_YN
        ,REG_DT
        ,UPT_DT
    </sql>

    <select id="findByUserId" resultMap="apiSendValidationConfigMap">
        SELECT
            <include refid="columns"/>
        FROM API_SEND_VALIDATION_CONFIG
        WHERE USER_ID = #{userId}
    </select>

</mapper>
```

### 2-1. 기본 구조

- 맨 위에 XML 선언과 MyBatis Mapper DOCTYPE을 둔다(위 예시 그대로).
- **`namespace`는 매퍼 인터페이스의 FQN(패키지 포함 전체 경로)과 정확히 일치시킨다.** 불일치하면 MyBatis가 인터페이스 메서드를 SQL에 바인딩하지 못한다.
- **`<select|insert|update|delete>`의 `id`는 인터페이스 메서드명과 정확히 일치시킨다.** (예: 메서드 `findByUserId` ↔ `<select id="findByUserId">`)
- **`parameterType` 속성은 쓰지 않는다.** 파라미터는 `@Param`으로 식별된다(원칙 3).

### 2-2. resultMap

- 조회 결과 매핑은 명시적 `<resultMap>`을 사용한다. 컬럼명(`UPPER_SNAKE`)과 프로퍼티명(`camelCase`)이 다르므로 매핑을 명시해 의도를 분명히 한다.
- `resultMap`의 `id`는 `엔티티명(camelCase) + Map` 형태로 짓는다. (예: `ApiSendValidationConfig` → `apiSendValidationConfigMap`)
- `type`은 엔티티의 FQN.
- `<result property="..." column="..."/>`에서 `property`는 camelCase(자바 필드), `column`은 대문자(DB 컬럼). **`property`/`column` 값을 세로로 정렬**해 가독성을 높인다(위 예시 정렬 참고).
- `<select>`에는 `resultType` 대신 이 `resultMap`을 참조한다.

### 2-3. 컬럼 목록은 `<sql>` 조각으로 재사용

조회 컬럼 목록은 `<sql>` 조각으로 한 번만 정의하고, `<select>`에서 `<include refid="...">`로 가져다 쓴다. 컬럼이 추가/변경될 때 한 곳만 고치면 되고, 같은 테이블을 조회하는 여러 쿼리가 컬럼 목록을 공유할 수 있다.

```xml
<sql id="columns">
    ID
    ,USER_ID
    ,COMMON_SEND_CONFIG_JSON
    ,DEL_YN
    ,REG_DT
    ,UPT_DT
</sql>

<select id="findByUserId" resultMap="apiSendValidationConfigMap">
    SELECT
        <include refid="columns"/>
    FROM API_SEND_VALIDATION_CONFIG
    WHERE USER_ID = #{userId}
</select>
```

규칙:

- `<sql>` 조각 안의 컬럼 목록도 선행 콤마(leading comma) 스타일을 유지한다. 첫 컬럼(`ID`)만 콤마 없이 시작한다.
- `SELECT` 키워드는 `<sql>` 조각에 넣지 말고 `<select>` 쪽에 두며, 조각에는 컬럼 목록만 담는다. (`SELECT` 뒤에 `<include>`를 붙이는 형태)
- `<sql>`의 `id`는 의미를 드러내는 이름으로 짓는다(예: 기본 조회 컬럼이면 `columns`). 한 매퍼에 여러 조각이 필요하면 용도를 구분하는 이름을 쓴다.
- 같은 테이블의 다른 `<select>`(목록 조회 등)도 동일한 `<include refid="columns"/>`를 재사용한다.


### 2-4. SQL 포맷

SQL 본문 포맷은 사내 SQL 컨벤션과 동일하게 맞춘다:

- 키워드·컬럼명·테이블명은 **대문자**.
- 컬럼을 여러 줄로 나열할 때는 **선행 콤마(leading comma)** 스타일.
- 파라미터는 `#{이름}`으로 바인딩한다. 이름은 인터페이스의 `@Param("이름")`과 일치시킨다. 엔티티를 넘긴 경우 `#{엔티티이름.필드}`로 접근한다(§5).
- `#{}` 는 PreparedStatement 바인딩이므로 SQL Injection에 안전하다. 동적 컬럼명 등 불가피한 경우가 아니면 `${}`(문자열 치환)는 쓰지 않는다.

> ℹ️ 논리 삭제(`DEL_YN`) 테이블의 조회 시 `AND DEL_YN = 'N'` 필터를 넣을지는 SQL 컨벤션과 동일하게 적용한다. (제공된 예시에는 필터가 없는데, 사내 정책에 맞춰 추가할지 확인 필요.)

---

## 3. resultMap vs 단순 매핑

기본은 `<resultMap>` 명시다. 다만 다음 보조 수단도 알아둔다:

- **단순 스칼라 반환**(`COUNT(*)`, 단일 컬럼 등)은 `resultType="long"`처럼 기본형/단일 타입으로 받아도 된다.
- 프로젝트에 `mapUnderscoreToCamelCase=true` 설정이 있다면 컬럼↔프로퍼티 자동 매핑이 가능하지만, **본 컨벤션은 명시적 `resultMap`을 기본**으로 한다(매핑 의도가 코드에 드러나도록).

---

## 4. 메서드 네이밍 (JPA 명명 규칙)

MyBatis는 이름으로 쿼리를 만들어주지 않지만, 메서드 이름은 JPA 규칙 어휘를 그대로 사용해 일관성을 유지한다. 대표 패턴:

| 의도 | 메서드명 예시 | 반환 타입 |
|------|--------------|-----------|
| PK로 단건 조회 | `findById(@Param("id") Long id)` | `Optional<T>` |
| 특정 필드로 단건 조회 | `findByUserId(@Param("userId") String userId)` | `Optional<T>` |
| 조건으로 목록 조회 | `findAllByStatus(@Param("status") String status)` | `List<T>` |
| 전체 조회 | `findAll()` | `List<T>` |
| 존재 여부 | `existsByUserId(@Param("userId") String userId)` | `boolean` |
| 건수 | `countByStatus(@Param("status") String status)` | `long` |
| 저장 | `save(@Param("config") ApiSendValidationConfig config)` | `int` |
| 수정 | `update(@Param("config") ApiSendValidationConfig config)` | `int` |
| PK로 삭제 | `deleteById(@Param("id") Long id)` | `int` |

- 조건 결합은 JPA 어휘를 따른다: `And`, `Or`, `OrderBy`, `In`, `Between`, `GreaterThan`, `LessThan`, `Like` 등 (예: `findAllByStatusAndDelYn`). **이름과 실제 SQL의 조건이 일치하도록** 직접 작성한다.

---

## 5. 파라미터 규칙

원칙 3·4를 합치면 다음과 같다.

### 5-1. 스칼라 파라미터 (1~2개)

개별 값을 `@Param`으로 넘긴다.

```java
Optional<ApiSendValidationConfig> findByUserId(@Param("userId") String userId);

List<ApiSendValidationConfig> findAllByUserIdAndDelYn(
        @Param("userId") String userId,
        @Param("delYn") String delYn);
```
```xml
<select id="findAllByUserIdAndDelYn" resultMap="apiSendValidationConfigMap">
    SELECT
        <include refid="columns"/>
    FROM API_SEND_VALIDATION_CONFIG
    WHERE USER_ID = #{userId}
      AND DEL_YN = #{delYn}
</select>
```

### 5-2. 변수 3개 이상 (INSERT/UPDATE/DELETE)

값이 3개 이상이면 개별 파라미터를 늘어놓지 말고 **엔티티를 넘긴다.** 이때도 `@Param`으로 이름을 붙이고, XML에서는 `#{이름.필드}`로 접근한다.

```java
int save(@Param("config") ApiSendValidationConfig config);
```
```xml
<insert id="save">
    INSERT INTO API_SEND_VALIDATION_CONFIG
        ( USER_ID, COMMON_SEND_CONFIG_JSON, EMAIL_SEND_CONFIG_JSON, MSG_SEND_CONFIG_JSON, DEL_YN, REG_DT )
    VALUES
        ( #{config.userId}, #{config.commonSendConfigJson}, #{config.emailSendConfigJson}, #{config.msgSendConfigJson}, 'N', NOW() )
</insert>
```

- 엔티티 프로퍼티는 `#{config.userId}`처럼 `@Param` 이름을 접두로 붙여 참조한다.
- 변수가 1~2개인 INSERT/UPDATE/DELETE는 스칼라 `@Param`을 그대로 써도 된다.

---

## 6. SELECT — 단건 / 목록

- **단건**: 인터페이스 반환 타입 `Optional<T>`. XML은 동일하게 `<select>`로 작성하고 행이 없으면 MyBatis가 빈 결과 → `Optional.empty()`로 매핑된다(MyBatis가 `Optional` 반환 타입을 지원).
- **목록**: 반환 타입 `List<T>`. 결과 없으면 빈 리스트.
- 둘 다 `resultMap`을 참조한다.

```java
Optional<ApiSendValidationConfig> findByUserId(@Param("userId") String userId);   // 단건
List<ApiSendValidationConfig> findAllByDelYn(@Param("delYn") String delYn);        // 목록
```

---

## 7. INSERT / UPDATE / DELETE

- XML 태그는 의미에 맞게 `<insert>`, `<update>`, `<delete>`를 사용한다(전부 `<update>`로 뭉뚱그리지 않는다).
- 반환 타입은 `int`(영향 행 수).
- **UPDATE/DELETE에는 반드시 `WHERE` 조건을 명시**한다. 조건 없는 전체 변경은 금지.
- 변수 3개 이상이면 엔티티 파라미터(§5-2).
- INSERT의 컬럼/값 목록은 SQL 컨벤션대로(한 줄 일반 콤마 또는 정렬), UPDATE의 `SET`이 여러 줄이면 선행 콤마 스타일.

```xml
<update id="update">
    UPDATE API_SEND_VALIDATION_CONFIG
    SET COMMON_SEND_CONFIG_JSON = #{config.commonSendConfigJson}
       ,MSG_SEND_CONFIG_JSON    = #{config.msgSendConfigJson}
       ,UPT_DT                  = NOW()
    WHERE USER_ID = #{config.userId}
</update>
```

> 논리 삭제 정책을 쓰는 테이블이면 `<delete>` 대신 `DEL_YN = 'Y'` UPDATE로 처리할지 SQL 컨벤션과 맞춘다. (DELETE 표준 패턴 확정 필요.)

---

## 8. 작성·리뷰 체크리스트

- [ ] 인터페이스에 `@Mapper`가 있는가
- [ ] 메서드명이 JPA 명명 규칙을 따르는가
- [ ] 단건 조회가 `Optional<T>`를 반환하는가
- [ ] 모든 파라미터에 `@Param("이름")`이 붙어 있는가 (1개여도)
- [ ] 변수 3개 이상인 INSERT/UPDATE/DELETE가 엔티티를 파라미터로 받는가
- [ ] XML `namespace`가 인터페이스 FQN과 정확히 일치하는가
- [ ] `<select|insert|update|delete>`의 `id`가 메서드명과 일치하는가
- [ ] XML에 `parameterType`이 없는가
- [ ] 조회가 명시적 `<resultMap>`(`엔티티명+Map`)을 참조하고, property=camelCase / column=대문자로 정렬돼 있는가
- [ ] 조회 컬럼 목록을 `<sql>` 조각으로 정의하고 `<select>`에서 `<include refid="...">`로 재사용하는가
- [ ] SQL이 대문자 키워드 + 선행 콤마 스타일이고 `#{}` 바인딩을 쓰는가 (`${}` 미사용)
- [ ] UPDATE/DELETE에 `WHERE` 조건이 있는가
- [ ] 변경 메서드가 `int`를 반환하는가