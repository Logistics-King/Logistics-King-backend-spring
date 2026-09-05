# 백엔드 CI와 수동 배포

## 실행 조건

- develop/main push(머지 또는 직접 push)에만 CI를 실행한다. 기능 브랜치 push와 PR 생성/업데이트에서는 실행하지 않는다.
- 실행 순서: Java 21 → Gradle test/bootJar → Docker build.
- develop/main push: 검증 통과 후 GHCR에 `ghcr.io/<소문자 owner>/<소문자 repository>:sha-<전체 commit SHA>`를 업로드한다.
- 현재 테스트는 실제 MySQL/Redis를 띄우는 통합 검증이 아니다. CI 성공만으로 DB 연결, 스키마, 인증, 실제 API가 검증됐다고 판단하지 않는다.
- Docker builder에서도 test/bootJar를 수행하므로 독립적인 로컬 이미지 빌드에서도 테스트를 생략하지 않는다.
- SSH 접속, 자동 서버 배포, DB 변경, 커밋은 수행하지 않는다.

## GitHub 설정

- Actions와 packages write 권한을 조직/저장소 정책에서 허용한다.
- 업로드는 자동 제공되는 GITHUB_TOKEN을 사용한다. AWS/GCP 키 또는 별도 업로드 PAT는 필요하지 않다.
- 기존 GHCR package를 재사용한다면 해당 저장소의 Actions 접근 권한을 확인한다.
- `.gitignore`의 `*.yml` 예외는 `.github/workflows/*.yml`에만 적용한다. 개발용 application.yml은 계속 Git에서 제외한다.
- PR 검사를 실행하지 않으므로 이 workflow의 `verify`, `image`를 PR 병합 필수 검사로 설정하지 않는다. 기존 필수 검사 설정이 있다면 조정해야 병합 대기가 발생하지 않는다.
- 머지를 통해서만 반영하려면 develop/main 직접 push를 브랜치 보호 규칙으로 제한한다. CI 오류는 머지 후 발견될 수 있다.

## 운영 설정: 이미지 실행 전 필수

현재 application.yml은 Git에 없으며 Docker context에서도 제외한다. 이 이미지는 서버에서 운영 설정을 공급하기 전에는 정상 기동을 보장하지 않는다.

- 운영 application.yml을 서버에 따로 만들고 `/app/config/application.yml`에 read-only로 마운트한다. `/app`은 컨테이너 작업 디렉터리다.
- 설정에는 datasource, Redis, auth.jwt 및 auth.cookie의 필수 속성을 포함한다. 개발 파일을 그대로 복사하지 않는다.
- DB/Redis 주소는 localhost가 아닌 Compose 내부 서비스명 또는 관리형 DB 주소로 지정한다.
- 운영 전용 JWT_SECRET과 DB 비밀번호는 서버에서 주입한다. 개발용 JWT 기본값을 사용하지 않는다.
- HTTPS에서는 쿠키 Secure=true, HttpOnly=true로 설정하고 SameSite/CSRF 정책을 검증한다.
- 스키마를 명시적으로 적용한 뒤 ddl-auto=validate를 사용한다. 운영 데이터에 create/update 자동 변경을 적용하지 않는다.
- 현재 백엔드 CORS/Origin 허용 정책도 배포 도메인 기준으로 확인해야 한다.
- DB 전용 최소 권한 계정, Redis 접근 제한, 영구 볼륨, 외부 백업 및 복원 검증이 필요하다.

## 수동 배포

- 현재 CI 이미지는 linux/amd64 전용이다. AWS/GCP의 x86_64 서버를 사용한다. ARM/Graviton은 별도 빌드 설정이 필요하다.
- private package는 서버에서 접근 가능한 계정의 최소 read:packages 토큰으로 GHCR 로그인한다. GITHUB_TOKEN은 서버 로그인용이 아니다.
- Compose의 backend image를 전체 SHA reference로 고정한다.
- `docker compose pull backend` 후 `docker compose up -d --no-deps backend`로 교체한다. backend 서비스 정의는 후속 Compose 작업이다.
- 실행 포트는 8080, 실행 사용자는 UID 10001이다. 마운트한 설정 파일에 해당 사용자의 읽기 권한을 부여한다.
- Nginx만 외부에 공개하고 8080/MySQL/Redis는 내부 통신으로 제한한다.
- 실패 시 이전 이미지 reference로 되돌려 재생성한다. DB 스키마 변경은 이미지 롤백으로 취소되지 않는다.
- 배포 후 기동 로그, DB/Redis 연결, 로그인/갱신/로그아웃, 권한, 주요 API와 SSE를 확인한다.

## 로컬 이미지 빌드

```sh
docker build -t logistics-king-backend:local .
```

운영 설정, Compose, Nginx/HTTPS, 스키마 적용 및 백업은 이번 CI 작업과 별도다.
