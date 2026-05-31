# Persistence/JPA 작성 규칙

## 목표

택배왕은 프로토타입 단계에서도 MySQL을 기본 DB로 사용한다.

아키텍처 기준은 `.codex/architecture.md`를 따른다.

```text
domain = repository interface + domain object
infra.persistence = JPA Entity + Spring Data Repository + repository 구현체
```

## 기본 JPA 세팅

프로토타입 기본 DB는 로컬 MySQL을 사용한다.

`build.gradle`:

```gradle
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    runtimeOnly 'com.mysql:mysql-connector-j'
}
```

`application.yml`:

```yaml
spring:
  application:
    name: Logistics-King

  datasource:
    url: jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:logistics_king}?serverTimezone=Asia/Seoul&characterEncoding=UTF-8
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:}

  jpa:
    hibernate:
      ddl-auto: create
    show-sql: true
    properties:
      hibernate:
        format_sql: true

  sql:
    init:
      mode: never
      encoding: UTF-8
```

로컬 기본값은 `localhost:3306/logistics_king`, 사용자명 `root`, 비밀번호 없음이다. 환경마다 `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`로 override한다.

프로젝트 이름 `Logistics-King`은 MySQL에서 다루기 쉽도록 snake_case로 정규화해 기본 database 이름을 `logistics_king`으로 사용한다.

## SQL 파일 관리

모든 DDL/DML 스크립트는 `src/main/resources/sql` 아래에서 관리한다.

```text
src/main/resources/sql
  ddl
    000_create_database.sql
    001_schema.sql
  dml
    001_seed.sql
```

규칙:

- database 생성 스크립트는 `sql/ddl/000_create_database.sql`에 둔다.
- schema DDL은 `sql/ddl/001_schema.sql`에 둔다.
- seed/reference DML은 `sql/dml/001_seed.sql`에 둔다.
- 로컬 개발 환경에서는 Hibernate `ddl-auto=create`를 유지한다.
- SQL init은 자동 실행하지 않는다. `spring.sql.init.mode=never`를 유지한다.
- DDL/DML 파일은 스키마와 데이터 변경 이력을 사람이 확인하고 필요할 때 수동 실행하기 위한 관리 파일이다.
- 새 테이블이나 컬럼은 JPA Entity만 수정하지 말고 DDL 파일도 함께 수정한다.
- 로컬 DB가 없으면 먼저 `000_create_database.sql`을 MySQL에 수동 실행한다.

## Entity와 Domain 분리

domain 객체와 JPA Entity는 분리한다.

```text
domain.contract.ContractRequest
infra.persistence.contract.ContractRequestJpaEntity
```

규칙:

- domain 객체는 JPA annotation을 모른다.
- JPA Entity는 `infra.persistence` 밖으로 노출하지 않는다.
- repository 구현체에서 JPA Entity와 domain 객체를 변환한다.
- app controller나 domain service가 JPA Entity를 직접 참조하면 안 된다.

## Repository 규칙

repository interface는 `domain`에 둔다.

```kotlin
interface ContractRequestRepository {
    fun save(contractRequest: ContractRequest): ContractRequest
    fun findById(id: Long): ContractRequest?
}
```

repository 구현체는 `infra.persistence`에 둔다.

```kotlin
@Repository
class ContractRequestRepositoryImpl(
    private val jpaRepository: ContractRequestJpaRepository,
) : ContractRequestRepository {

    override fun save(contractRequest: ContractRequest): ContractRequest {
        return jpaRepository.save(ContractRequestJpaEntity.from(contractRequest)).toDomain()
    }

    override fun findById(id: Long): ContractRequest? {
        return jpaRepository.findById(id).orElse(null)?.toDomain()
    }
}
```

Spring Data repository는 JPA Entity만 다룬다.

```kotlin
interface ContractRequestJpaRepository : JpaRepository<ContractRequestJpaEntity, Long>
```

## ID 규칙

`User` PK는 시간 순서 정렬이 가능한 UUID를 사용한다.

규칙:

- UUID 생성은 `domain.common.IdGenerator`를 통해 추상화한다.
- 실제 구현체는 `infra.id.TimeOrderedUuidGenerator`를 사용한다.
- 생성되는 UUID는 UUIDv7 형식이다.
- 같은 millisecond 안에서 생성된 ID도 sequence로 생성 순서를 유지한다.
- JPA Entity의 `User` ID 타입은 `UUID`를 사용한다.

예시:

```kotlin
@Id
@Column(columnDefinition = "BINARY(16)")
val id: UUID
```

`users` 테이블 기본 컬럼:

```text
id BINARY(16) PRIMARY KEY
login_id VARCHAR(50) UNIQUE NOT NULL
email VARCHAR(255) UNIQUE NOT NULL
encoded_password VARCHAR(255) NOT NULL
name VARCHAR(50) NOT NULL
role VARCHAR(30) NOT NULL
created_at DATETIME(6) NOT NULL
updated_at DATETIME(6) NOT NULL
```

다른 aggregate의 ID 타입은 요구사항에 따라 정하되, 새 aggregate에서 외부 노출이 큰 식별자는 UUID 사용을 우선 검토한다.

## Entity 작성 규칙

아래 예시는 일반 JPA Entity 작성 형태를 보여준다. `User` Entity는 위 ID 규칙에 따라 UUID를 사용한다.

```kotlin
@Entity
@Table(name = "contract_requests")
class ContractRequestJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val shipperId: Long,

    val pickupRegion: String,

    val monthlyVolume: Int,

    @Enumerated(EnumType.STRING)
    val status: ContractRequestStatus,
) {
    fun toDomain(): ContractRequest {
        return ContractRequest.restore(
            id = id,
            shipperId = shipperId,
            pickupRegion = pickupRegion,
            monthlyVolume = monthlyVolume,
            status = status,
        )
    }

    companion object {
        fun from(domain: ContractRequest): ContractRequestJpaEntity {
            return ContractRequestJpaEntity(
                id = domain.id,
                shipperId = domain.shipperId,
                pickupRegion = domain.pickupRegion,
                monthlyVolume = domain.monthlyVolume,
                status = domain.status,
            )
        }
    }
}
```

규칙:

- table 이름은 snake_case 복수형을 사용한다.
- column 이름은 필요한 경우 명시한다.
- enum은 `@Enumerated(EnumType.STRING)`을 사용한다.
- Kotlin JPA plugin이 있으므로 entity open/no-arg 처리는 plugin에 맡긴다.
- entity 생성자는 가능한 단순하게 유지한다.

## 연관관계 규칙

프로토타입 초기에는 객체 연관관계보다 ID 참조를 우선한다.

예시:

```kotlin
val contractRequestId: Long
val agencyId: Long
val selectedProposalId: Long?
```

이유:

- 도메인 객체와 JPA Entity 분리를 쉽게 유지한다.
- fetch 전략, cascade, orphanRemoval 문제를 줄인다.
- API 요구사항 변경에 더 쉽게 대응한다.

객체 연관관계는 다음 조건을 만족할 때만 도입한다.

- 생명주기가 강하게 묶여 있다.
- 함께 저장/삭제되어야 한다.
- 조회 성능상 명확한 이점이 있다.
- 테스트로 cascade/fetch 동작을 검증할 수 있다.

## Audit 필드

`created_at`, `updated_at`은 각 Entity에 반복해서 선언하지 않는다. 모든 JPA Entity는 `infra.persistence.common.BaseJpaEntity`를 상속해 audit 필드를 관리한다.

```kotlin
@MappedSuperclass
abstract class BaseJpaEntity {

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime? = null
        protected set

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime? = null
        protected set

    @PrePersist
    protected fun prePersist() {
        val now = LocalDateTime.now()
        createdAt = now
        updatedAt = now
    }

    @PreUpdate
    protected fun preUpdate() {
        updatedAt = LocalDateTime.now()
    }
}
```

규칙:

- JPA Entity는 `BaseJpaEntity`를 상속한다.
- `created_at`, `updated_at` 컬럼은 DDL 파일에도 함께 작성한다.
- domain 객체에는 audit 필드를 무조건 넣지 않는다. 도메인 규칙에 필요한 경우에만 별도 필드로 둔다.
- audit 정책이 복잡해지면 Spring Data JPA auditing 도입을 검토한다.

## Querydsl 위치

Querydsl 코드는 `infra.persistence` 아래 repository 구현체 또는 query repository에 둔다.

Querydsl 작성 규칙은 `.codex/querydsl.md`를 따른다.

금지:

- app controller에서 Querydsl 사용
- domain service에서 Querydsl 사용
- JPA Entity를 API response로 직접 반환

## Migration 기준

초기 프로토타입:

- MySQL
- 로컬 개발 환경은 `ddl-auto=create`
- SQL init 자동 실행 없음
- schema migration 도구 없음

운영 DB 도입 시:

- profile별 datasource 분리
- `ddl-auto=validate` 또는 `none`
- Flyway 또는 Liquibase 도입 검토

## 테스트 기준

- repository 구현체는 `@DataJpaTest`로 검증한다.
- domain service 테스트에서는 repository를 fake/mock으로 대체한다.
- JPA Entity와 domain 변환 로직은 repository 테스트에서 함께 검증한다.
- Querydsl query는 조건별 검색 결과를 통합 테스트로 검증한다.
