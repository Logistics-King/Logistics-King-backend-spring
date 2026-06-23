# AGENTS.md

## Project

This repository is the backend for `택배왕`, a logistics contract platform.

- Language: Kotlin 2.2.21
- Framework: Spring Boot 4.0.6
- Java: 21
- Build tool: Gradle
- Main domain reference: `.codex/domain.md`
- Main architecture reference: `.codex/architecture.md`
- API reference: `.codex/api.md`
- Code convention reference: `.codex/code-convention.md`
- Commit reference: `.codex/commit.md`
- Orchestration reference: `.codex/orchestration.md`
- Error handling reference: `.codex/error-handling.md`
- Persistence/JPA reference: `.codex/persistence.md`
- Querydsl reference: `.codex/querydsl.md`

## Output style

- Use caveman style by default for user-facing prose.
- Keep technical accuracy.
- Keep code blocks, commands, commit messages, PR descriptions, filenames, API names, and error messages unchanged.
- Use normal clear prose for security warnings, destructive confirmations, or when compressed wording could cause ambiguity.
- Stop caveman style only when the user says "normal mode" or "stop caveman".

## Domain

- Official service name: `택배왕`
- Do not use `딜링크` or `DealLink` as the service name.
- Before designing or implementing domain behavior, read `.codex/domain.md`.
- Treat `.codex/domain.md` as the source of truth for detailed domain notes.
- Market size, parcel volume, and growth-rate numbers are planning-document figures and need validation against official statistics before being presented as verified facts.

## Core concepts

- 화주: Sends parcels, such as a seller, online shop, or business.
- 대리점: Regional sales and pickup hub of a parcel company.
- 택배사: Logistics network operator such as CJ, Hanjin, Lotte, or Logen.
- 배송기사: Person who performs pickup and delivery through the 대리점 structure.
- 계약 요청: Shipping contract demand registered by a 화주.
- 제안: Price and service terms submitted by a 대리점 for a 계약 요청.
- 계약: Completed transaction when a 화주 selects a 제안.
- 비교 항목: Unit price, rating, review, Saturday delivery, cold-chain support, return policy, and pickup time.

## Business rules

- 택배왕 connects 화주 and 대리점 through private bidding.
- 대리점 proposals are visible to the 화주, not to competing 대리점.
- 화주는 proposals on one screen and chooses based on price, service terms, rating, and reviews.
- Lowest price is not always the winning factor.
- The same 택배사 can have different 대리점 with different prices and service terms.
- A 계약 요청 should include enough details for accurate proposals: pickup region, expected monthly volume, box size, product type, desired pickup time, and required service terms.
- Platform responsibility is brokerage and contract flow. Actual pickup, delivery, and driver operations remain with existing 택배사 and 대리점 structures unless a feature explicitly changes that model.

## Example scenario

An apparel shop owner in Ansan Il-dong sends about 800 boxes per month. Today, the owner must call CJ Il-dong, CJ Bono, Hanjin Sa-dong, Lotte Gojan-dong, and Logen Seonbu-dong one by one to compare 60-size box price, Saturday delivery, cold-chain support, and return policy.

In 택배왕, the owner registers one contract request:

```text
지역: 경기도 안산시 일동
월 예상 물량: 800박스
물품 종류: 일반 의류
박스 크기: S박스 또는 60사이즈 중심
픽업 희망 시간: 오전 9시 ~ 오후 6시
필요 조건: 토요일 배송, 반품 처리 조건 확인
```

Nearby 대리점 submit private proposals, and the 화주 compares them in one view.

## Implementation guidance

- Before implementing code, read `.codex/architecture.md`.
- For non-trivial feature implementation, follow `.codex/orchestration.md`.
- Before adding or changing APIs, read `.codex/api.md`.
- Before broad formatting or naming cleanup, read `.codex/code-convention.md`.
- Before staging or committing changes, read `.codex/commit.md`.
- Before adding or changing exceptions, read `.codex/error-handling.md`.
- Before adding or changing JPA persistence code, read `.codex/persistence.md`.
- Before writing Querydsl queries, read `.codex/querydsl.md`.
- Use the domain terms consistently in code, APIs, tests, and documentation.
- Prefer names that map directly to the domain: `shipper`, `agency`, `carrier`, `contractRequest`, `proposal`, `contract`, and `comparisonItem`.
- When adding a new feature, first check `.codex/domain.md` sections:
  - `주요 도메인 객체 후보`
  - `기본 매칭 기준`
  - `입찰과 비교 규칙`
- Keep domain logic explicit. Do not hide bidding, matching, or contract-selection rules in generic helper code.
- Avoid adding unrelated market, revenue, or business-plan detail to code comments. Keep those notes in `.codex/domain.md`.

## Verification

- Run tests with:

```bash
./gradlew test
```

- Check Kotlin compilation with:

```bash
./gradlew compileKotlin
```

- For documentation-only changes to `AGENTS.md` or `.codex/domain.md`, inspecting the rendered Markdown and checking old service-name references is enough.
