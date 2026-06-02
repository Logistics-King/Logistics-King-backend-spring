# Backend Orchestration

## Purpose

This document defines how AI agents should handle backend feature work in the 택배왕 backend repository.

The goal is not to make every task complex. The goal is to keep feature work predictable:

- analyze the requirement before editing
- confirm API/domain contracts before implementation
- choose serial or parallel execution intentionally
- verify with compile/test/API checks
- hand off API specs clearly when frontend integration is needed

## Entry Rules

Use this orchestration flow when the user asks for feature implementation, ticket work, backend API work, auth/permission work, domain work, or says phrases such as:

```text
구현해줘
티켓 구현해줘
#<number> 작업해줘
API 만들어줘
기능 추가해줘
```

If the user does not provide a ticket number, infer it from the current branch name when possible.

Small documentation edits, tiny bug fixes, simple command output requests, and direct questions do not need the full flow.

## Required References

Before planning implementation, read only the references needed for the task:

- `AGENTS.md`
- `.codex/domain.md`
- `.codex/architecture.md`
- `.codex/api.md`
- `.codex/error-handling.md`
- `.codex/persistence.md`
- `.codex/querydsl.md` when Querydsl is involved
- `.codex/commit.md` before staging or committing

## Gates

### 1. Planning Gate

Clarify:

- user goal
- affected domain
- affected API
- affected tables
- auth/permission impact
- likely files/packages
- serial vs parallel execution

Output a short implementation strategy when the task is non-trivial.

### 2. Approval Gate

Before editing repo-tracked implementation files, get user approval for non-trivial work.

The following count as approval:

```text
ㄱㄱ
ㅇㅋ ㄱㄱ
구현 ㄱㄱ
진행해
바로 해
```

For tiny fixes or when the user already explicitly says to implement, proceed without asking again.

### 3. Contract Gate

Before implementation, decide the shared contract:

- package path
- URL path and HTTP method
- request DTO name
- response DTO name
- command/result DTO name
- usecase interface name
- domain service method
- repository interface changes
- transaction boundary
- DDL/DML ownership
- auth role and `end_points` behavior
- error codes
- API response shape

This contract belongs to the orchestrator. Workers must follow it.

### 4. Implementation Gate

Default to serial mode unless parallel work clearly helps.

Use parallel workers only after the Contract Gate is stable and write sets are disjoint.

### 5. Integration Gate

The orchestrator owns final integration:

- merge worker outputs
- resolve import/package issues
- check dependency direction
- check module/package boundaries
- make code compile
- ensure API and persistence contracts match

### 6. Verification Gate

Minimum verification:

```bash
./gradlew compileKotlin
./gradlew test
```

For API work, also run or prepare:

- local curl/Postman request
- DB read-only SELECT check
- Swagger endpoint check when relevant

### 7. Closeout Gate

Final report should include:

- what changed
- API changes
- DB/DDL/DML changes
- tests run
- remaining risks
- commit split recommendation or created commits
- frontend handoff note when API changed

## Sub Agents

### backend-orchestrator

Team lead agent.

Responsibilities:

- control gates
- decide serial vs parallel
- own shared contract
- assign worker scopes
- integrate worker output
- run final verification
- prepare final report

### backend-implementer

General implementation worker.

Use for small and medium tasks where splitting by layer is unnecessary.

Allowed write scope:

```text
src/main/kotlin/logisticsking/com/logisticskingbackendspring/app/**
src/main/kotlin/logisticsking/com/logisticskingbackendspring/domain/**
src/main/kotlin/logisticsking/com/logisticskingbackendspring/infra/**
src/main/resources/sql/**
```

### backend-tester

Testing and verification worker.

Allowed write scope:

```text
src/test/**
```

May run:

```bash
./gradlew compileKotlin
./gradlew test
```

May use `$postman-local-test` for local API/DB verification.

### backend-reviewer

Read-only reviewer by default.

Checks:

- architecture boundary violations
- domain/app/infra dependency direction
- API response format
- ErrorCode usage
- JPA/domain separation
- auth/permission risks
- missing tests
- unsafe defaults

### api-spec-handoff

Frontend handoff worker.

Use when backend API changed and frontend integration is expected.

Output must be copy-pasteable Markdown and include the same API/request/response/DB verification facts as `$postman-local-test`.

## Extended Worker Split

For large tasks, split implementation by layer.

### backend-domain-worker

Write scope:

```text
src/main/kotlin/logisticsking/com/logisticskingbackendspring/domain/**
src/test/kotlin/logisticsking/com/logisticskingbackendspring/domain/**
```

Owns:

- domain object
- domain service
- repository interface
- domain validation
- ErrorCode

### backend-app-worker

Write scope:

```text
src/main/kotlin/logisticsking/com/logisticskingbackendspring/app/**
```

Owns:

- controller
- request/response DTO
- command/result DTO
- usecase interface
- API mapping

### backend-infra-worker

Write scope:

```text
src/main/kotlin/logisticsking/com/logisticskingbackendspring/infra/**
src/main/resources/sql/**
```

Owns:

- JPA entity
- Spring Data repository
- repository implementation
- Redis adapter
- Security adapter
- Querydsl implementation
- DDL/DML files

## Serial Mode

Use serial mode when:

- task is small
- write sets overlap
- API contract is unclear
- domain boundary is unclear
- transaction policy is unclear
- DDL/DML/backfill decision is central
- test expectations conflict with requirements
- parallel overhead is larger than the task

Examples:

- README update
- Swagger annotation edit
- single error-code fix
- small security config fix
- typo or package import fix

## Parallel Mode

Use parallel mode when:

- task is medium or large
- contract is clear
- write sets are disjoint
- domain/app/infra/test can progress independently
- review can run read-only while implementation continues

Examples:

- contract request feature
- proposal feature
- profile feature
- auth/permission feature
- frontend integration issue with backend API changes

## Permission Rules

New protected APIs are automatically inserted into `end_points` with `ADMIN` role by default.

Before production deployment:

- review all automatically registered endpoints
- insert required `VENDOR`, `AGENCY`, or `DRIVER` rows through DML or an approved admin flow
- avoid silently opening new APIs to non-admin roles

Public auth endpoints under `/api/v1/auth/**` are excluded from endpoint authorization.

## Frontend Handoff

When API behavior changes, prepare a frontend handoff with `$api-spec-handoff`.

Include:

- endpoint
- request JSON
- response JSON
- cookie behavior
- auth requirements
- DB verification result
- error response shape
- frontend fetch example with `credentials: "include"` when cookie auth is needed

## Commit Guidance

Use `$commit-splitter` for commit creation.

Commit message format:

```text
<prefix> : #<issue-number> <Korean summary>
```

Split commits by requirement and implementation intent, not by file count.
