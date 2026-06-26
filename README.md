# Favicon Backend

공공·통계 데이터셋 포털 **Favicon**의 백엔드 API 서버입니다. 데이터셋 검색·테마/지역별 조회·인기 순위, S3 기반 파일 다운로드, JWT 인증과 이메일 OTP 회원 기능, 데이터 요청 게시판, 공지·FAQ·통계 등 관리자 기능, 그리고 데이터 분석·GPT 챗 기능을 REST API로 제공합니다.

> [favicon-data/back](https://github.com/favicon-data/back)을 **포크**하여 코드 품질·재사용성·코드 스타일을 개선한 캡스톤(졸업작품) 프로젝트입니다.

## 목차

- [문서](#문서)
- [리팩터링](#리팩터링)
- [주요 기능](#주요-기능)
- [기술 스택](#기술-스택)
- [아키텍처](#아키텍처)
- [API 개요](#api-개요)
- [인증 / 인가](#인증--인가)
- [스케줄러](#스케줄러)
- [환경 변수](#환경-변수)
- [빌드 & 실행](#빌드--실행)
- [API 문서](#api-문서)
- [참고 / 알려진 특이사항](#참고--알려진-특이사항)

## 문서

프로젝트 설계·운영 문서는 Notion에 정리되어 있습니다.

| 문서 | 내용 |
|------|------|
| [ERD](https://petite-ox-5a5.notion.site/ERD-21c045bbe19f80718089ecff3957e9c5) | 데이터베이스 엔티티·관계 다이어그램 |
| [API 명세](https://petite-ox-5a5.notion.site/API-21c045bbe19f80adad71d2710ceb402f) | 전체 REST API 상세 명세 |
| [Data Flow](https://petite-ox-5a5.notion.site/Data-Flow-21c045bbe19f80889dcbe0284912d01c) | 요청 처리·데이터 흐름도 |
| [CI/CD](https://petite-ox-5a5.notion.site/CI-CD-218045bbe19f8083b44cd1b4a7c77b0f) | 빌드·배포 파이프라인 |

## 리팩터링

원본 팀 프로젝트(2025년 상반기) 이후, 개인 작업으로 인증 구조와 설정·빌드를 중심으로 리팩터링했습니다.

**세션 → JWT 인증 전환** — 가장 큰 변경

- 세션 기반 로그인을 제거하고 **stateless JWT 인증**으로 전면 교체
- `JwtUtil` · `UserDetailsService` 구현, `User` 도메인이 `UserDetails`를 구현하도록 변경
- `JwtAuthenticationFilter` 추가 및 JWT 예외(만료·위조 등) 일관 처리
- 공개 / 관리자 / 인증 필요 엔드포인트를 `SecurityConfig`로 정리
- 비밀번호 **BCrypt 해싱** 도입

**코드 정리**

- `UserService` 전반 리팩터링
- 중복된 회원 삭제 코드, 미사용 로그아웃·데드 코드 제거

**설정 / 빌드 분리**

- `local` / `prod` 프로파일 분리, 시크릿을 **환경 변수로 외부화**
- `.gitignore` 정리, CI(GitHub Actions) 워크플로 트리거·배포 단계 정비

**런타임 구성 조정**

- 외부 의존(AWS S3 · OpenAI GPT) 빈을 런타임 등록에서 제외(`@ComponentScan` 필터) — 관련 자격증명 없이도 핵심 API가 구동되도록 함. (아래 [참고](#참고--알려진-특이사항)의 비활성 기능과 연결됩니다.)


## 주요 기능

- **데이터셋** — 전체/상세 조회, 테마·지역·연도·파일유형 필터, 검색·정렬, 인기 Top 9, 다운로드 카운트, 월별 등록 통계.
- **트렌드** — 다운로드 수 기반 순위를 매일 집계하고 일자·기간별 추이를 제공(전일 대비 상승/하락/유지).
- **파일 다운로드** — 데이터셋 파일을 AWS S3에서 내려받아 첨부파일로 응답.
- **회원/인증** — 이메일 OTP 인증 회원가입, 로그인, 계정 탈퇴. JWT 기반 stateless 인증.
- **데이터 요청 게시판** — 요청글 CRUD(파일 첨부), 질문·답변(Q&A), 검토 상태 관리, 요청 통계.
- **스크랩** — 사용자가 관심 데이터를 스크랩/조회/삭제.
- **관리자** — 회원 강제 탈퇴, 공지·FAQ 관리, 사용자/데이터 통계.
- **데이터 분석 · GPT** — 데이터 분석 실행 및 OpenAI 기반 챗 응답.


## 기술 스택

`Java 17` · `Spring Boot 3.3.4` · `PostgreSQL` · `Redis` · `Spring Security + JWT (jjwt 0.12.3)` · `AWS S3 (SDK v2)` · `OpenAI` · `springdoc` · `Gradle` · `Docker (Corretto 17)`

각 기술의 실제 쓰임과 선택 이유:

| 기술 | 이 프로젝트에서의 쓰임 | 선택 이유 |
|------|----------------------|-----------|
| **PostgreSQL + JPA** | 데이터셋·테마·리소스·회원·요청 등 다수 엔티티를 FK 연관관계(`@ManyToOne`/`@OneToOne`/`@OneToMany`)·제약(not null·unique)·cascade·orphanRemoval로 매핑 | 엔티티 간 **관계와 참조 무결성·연쇄 삭제**가 도메인의 핵심이라 관계형 DB가 적합. JPA로 객체–테이블 매핑과 연관관계를 선언적으로 표현 |
| **Redis** | 이메일 인증 OTP(6자리)를 **3분 TTL**로 임시 저장하고, 검증 성공 시 즉시 삭제(1회용) | 짧게 살고 자동 만료돼야 하는 휘발성 데이터에 **키별 TTL**이 그대로 들어맞음. RDB에 넣고 만료를 직접 관리할 필요가 없음 |
| **JWT (jjwt) + Spring Security** | stateless 인증. HS256 대칭키 서명, Access 1시간 / Refresh 7일, `userId`·`email` 클레임. 요청마다 Bearer 토큰을 검증해 `SecurityContext` 설정 | 서버 세션을 없애 **수평 확장**에 유리하고, SPA 프론트엔드와 토큰 기반으로 연동하기 깔끔함 (세션 기반에서 [리팩터링](#리팩터링)으로 전환) |
| **AWS S3** | 데이터셋 파일·요청 첨부파일을 객체로 저장하고 버퍼 단위 **스트리밍** 업로드/다운로드 | 대용량 파일을 앱 서버·DB와 분리해 보관·배포. 객체 스토리지의 본래 용도에 부합 |
| **WebClient** (spring-webflux) | OpenAI Chat API를 호출하는 HTTP 클라이언트 (`.block()`으로 동기 사용) | 외부 REST API 호출용 모던 HTTP 클라이언트로 사용. ※ 리액티브 서버가 아니라 **WebClient만** 쓰는 용도이며, 서버 자체는 Spring MVC(서블릿) 스택 |
| **Python** (ProcessBuilder) | 데이터 분석을 `analysis.py` 별도 프로세스로 실행하고 stdout의 JSON을 Jackson으로 파싱 | 데이터 분석은 **Python 생태계**가 강점이라 분석은 Python에 맡기고 Java는 오케스트레이션·API 제공에 집중 (Dockerfile이 `python3`를 포함하는 이유) |
| **Thymeleaf + Spring Mail** | 인증 메일 본문을 `email.html` 템플릿에 코드 변수를 바인딩해 렌더링한 뒤 HTML 메일로 발송 | 메일 HTML을 문자열로 조합하지 않고 **템플릿 + 변수**로 분리해 유지보수성↑ |
| **springdoc (Swagger)** | 실행 중 API 문서 자동 생성·탐색 | 컨트롤러로부터 OpenAPI 문서를 자동화해 프론트엔드와의 API 협업을 쉽게 |
| **Lombok** | 엔티티·서비스의 getter·setter·생성자·로거 보일러플레이트 제거 | 반복 코드를 줄여 도메인 로직에 집중 |
| **Docker (Amazon Corretto 17)** | `app.jar` + `python3` 런타임을 한 이미지로 패키징하고 compose로 Redis와 함께 구동 | 실행 환경(JVM·Python·의존성)을 일관되게 배포 |

## 아키텍처

도메인(`dataset`, `user`, `admin`, `aws`)별로 패키지를 나누고, 각 도메인 안에서 `controller → application(service) → domain(entity) → repository`의 계층 구조를 따릅니다.

> 요청 처리·데이터 흐름은 [Data Flow 문서 (Notion)](https://petite-ox-5a5.notion.site/Data-Flow-21c045bbe19f80889dcbe0284912d01c)를 참고하세요.

```
com.capstone.favicon
├── FaviconApplication.java       # 진입점 (@EnableScheduling)
├── config/                       # CORS, Redis, S3, Security, 공통 응답(APIResponse), UTF-8 필터
├── security/                     # JWT 필터·유틸·예외 처리, UserDetailsService
├── dataset/                      # 데이터셋·테마·지역·리소스·트렌드·분석·GPT·S3 다운로드
│   ├── controller/
│   ├── application/ (+ service/) # 인터페이스(service/) + 구현(*Impl)
│   ├── domain/
│   └── repository/
├── user/                         # 회원·인증·메일·OTP·Redis·데이터요청·스크랩
├── admin/                        # 관리자·FAQ·공지·통계
└── aws/                          # S3 업로드/삭제, 메타데이터 동기화·파싱
```

### 도메인 모델

- **dataset** — `Dataset`, `DatasetTheme`, `Region`, `Resource`, `Trend`, `Download`, `FileExtension`, `DataAnalysis`
- **user** — `User`, `Scrap`, `DataRequest`, `Question`, `Answer`
- **admin** — `Notice`, `FAQ`

> 전체 엔티티·관계 다이어그램은 [ERD 문서 (Notion)](https://petite-ox-5a5.notion.site/ERD-21c045bbe19f80718089ecff3957e9c5)를 참고하세요.

## API 개요

> 권한 표기: 🟢 공개(permitAll) · 🔵 인증 필요(로그인) · 🔴 관리자(ROLE_ADMIN). 권한은 [`SecurityConfig`](src/main/java/com/capstone/favicon/config/SecurityConfig.java) 기준입니다.
>
> 아래는 요약이며, 요청/응답 형식 등 **상세 명세는 [API 문서 (Notion)](https://petite-ox-5a5.notion.site/API-21c045bbe19f80adad71d2710ceb402f)** 또는 실행 중 Swagger UI를 참고하세요.

| 영역 | 엔드포인트 | 주요 권한 |
|------|:---:|------|
| 데이터셋 · 트렌드 · 분석 | 19 | 대부분 🟢 공개 |
| 회원 · 인증 · 스크랩 | 8 | 🟢 공개 (일부 컨트롤러 인증) |
| 데이터 요청 게시판 | 15 | 🔵 인증 필요 |
| 공지 · FAQ · 통계 · 관리자 | 15 | 🟢 조회 / 🔴 관리자 쓰기 |

<details>
<summary><b>전체 엔드포인트 목록 펼치기</b></summary>

### 데이터셋 · 트렌드 · 분석

| 권한 | 메서드 | 경로 | 설명 |
|:---:|:---:|------|------|
| 🟢 | GET | `/data-set` | 전체 데이터셋 목록 |
| 🟢 | GET | `/data-set/{datasetId}` | 데이터셋 상세 |
| 🟢 | GET | `/data-set/top9` | 다운로드 상위 9개 인기 데이터셋 |
| 🟢 | GET | `/data-set/count` | 전체 데이터셋 개수 |
| 🟢 | GET | `/data-set/ratio` | 테마별 비율 통계 |
| 🟢 | GET | `/data-set/stats` | 월별 등록 통계 |
| 🟢 | GET | `/data-set/theme` | 테마(카테고리)별 목록 |
| 🟢 | GET | `/data-set/category/{themeId}` | 특정 테마에 속한 데이터셋 |
| 🟢 | GET | `/data-set/group-by-theme` | 테마별 그룹화 목록 |
| 🟢 | GET | `/data-set/search-sorted` | 검색어 기반 정렬 검색 |
| 🟢 | GET | `/data-set/filter` | 지역·연도·파일유형 필터 |
| 🟢 | POST | `/data-set/incrementDownload/{datasetId}` | 다운로드 횟수 +1 |
| 🟢 | GET | `/data-set/download/{datasetId}` | S3에서 파일 다운로드 |
| 🟢 | GET | `/region` | 전체 지역명 목록 |
| 🟢 | POST | `/analysis` | 데이터 분석 실행 |
| 🟢 | GET | `/trend/daily` | 특정 일자 트렌드 순위 |
| 🟢 | GET | `/trend/{datasetId}` | 데이터셋 기간별 트렌드 추이 |
| 🟢 | GET | `/trend/rank/{datasetId}` | 데이터셋 현재 순위 |
| 🔵 | POST | `/gpt/chat` | GPT 챗 응답 *(현재 빌드 비활성, 아래 참고)* |

### 회원 · 인증 · 스크랩

| 권한 | 메서드 | 경로 | 설명 |
|:---:|:---:|------|------|
| 🟢 | POST | `/users/email-check` | 가입용 OTP 인증코드 발송 |
| 🟢 | POST | `/users/code-check` | OTP 인증코드 검증 |
| 🟢 | POST | `/users/register` | 회원가입 |
| 🟢 | POST | `/users/login` | 로그인(JWT 발급) |
| 🟢* | DELETE | `/users/delete-account` | 본인 계정 탈퇴 |
| 🟢* | POST | `/users/scrap/{data-id}` | 데이터 스크랩 추가 |
| 🟢* | DELETE | `/users/scrap/{scrap-id}` | 스크랩 삭제 |
| 🟢* | GET | `/users/scrap` | 스크랩 목록 조회 |

> *`/users/**` 전체가 `permitAll`로 설정되어 있어, 탈퇴·스크랩처럼 본인 식별이 필요한 엔드포인트도 시큐리티 레벨에서는 공개입니다(컨트롤러 레벨 인증에 의존).

### 데이터 요청 게시판 (전부 🔵 인증 필요)

| 메서드 | 경로 | 설명 |
|:---:|------|------|
| GET | `/request/list` | 요청글 목록 |
| POST | `/request/list` | 요청글 작성(multipart, 파일 첨부) |
| PUT | `/request/{requestId}` | 요청글 수정 |
| DELETE | `/request/{requestId}` | 요청글 삭제 |
| PUT | `/request/list/{requestId}/review` | 검토 상태 변경 |
| GET | `/request/stats` | 요청 통계 |
| GET | `/request/download/{requestId}` | 첨부파일 다운로드 |
| GET | `/request/question` | 질문 목록 |
| POST | `/request/question` | 질문 작성 |
| PUT | `/request/question/{questionId}` | 질문 수정 |
| DELETE | `/request/question/{questionId}` | 질문 삭제 |
| GET | `/request/answer` | 답변 목록 |
| POST | `/request/answer` | 답변 작성 |
| PUT | `/request/answer/{answerId}` | 답변 수정 |
| DELETE | `/request/answer/{answerId}` | 답변 삭제 |

### 공지 · FAQ · 통계 · 관리자

| 권한 | 메서드 | 경로 | 설명 |
|:---:|:---:|------|------|
| 🟢 | GET | `/notice/list` | 공지 목록 |
| 🟢 | GET | `/notice/view/{noticeId}` | 공지 상세(보기용) |
| 🔴 | POST | `/notice/create` | 공지 등록 |
| 🔴 | PUT | `/notice/{noticeId}` | 공지 수정 |
| 🔴 | DELETE | `/notice/{noticeId}` | 공지 삭제 |
| 🟢 | GET | `/faq/list` | FAQ 목록 |
| 🔴 | POST | `/faq/create` | FAQ 등록 |
| 🔴 | PUT | `/faq/{faqId}` | FAQ 수정 |
| 🔴 | DELETE | `/faq/{faqId}` | FAQ 삭제 |
| 🟢 | GET | `/statistics/user-stats` | 사용자 수·증가 추이 |
| 🟢 | GET | `/statistics/user-overview` | 월별 사용자 통계 |
| 🟢 | GET | `/statistics/all-user` | 전체 사용자 목록 |
| 🔴 | DELETE | `/admin/delete-user` | 회원 강제 탈퇴 |
| 🔴 | POST | `/s3/upload` | S3 파일 업로드·데이터셋 등록 *(비활성)* |
| 🔴 | DELETE | `/s3/delete/{resourceId}` | S3 파일·리소스 삭제 *(비활성)* |

> 참고: `/notice/{noticeId}`·`/faq/{faqId}`의 단건 GET은 `/notice/*`·`/faq/*` 매처에 걸려 관리자 권한이 필요합니다(목록 조회는 공개).

</details>

## 인증 / 인가

- **stateless JWT** — 세션을 쓰지 않고 모든 요청을 토큰으로 인증합니다.
- 로그인 성공 시 발급된 토큰을 `Authorization: Bearer <token>` 헤더로 전달합니다.
- `JwtAuthenticationFilter`가 토큰을 검증하고, 인증/인가 실패는 `JwtAuthenticationEntryPoint`·`JwtAccessDeniedHandler`가 JSON으로 처리합니다.
- 비밀번호는 `BCryptPasswordEncoder`로 해싱합니다.

## 스케줄러

`TrendSchedulerServiceImpl`이 매일 자정(`0 0 0 * * *`)에 전체 데이터셋을 다운로드 수 기준으로 정렬해 그날의 순위를 저장하고, 전일 순위와 비교해 **상승/하락/유지** 상태를 기록합니다.

## 환경 변수

설정은 모두 환경 변수로 주입됩니다(저장소에 하드코딩된 시크릿 없음). 활성 프로파일은 `ACTIVE`로 선택합니다(`local` / `prod`).

**공통**

| 변수 | 설명 |
|------|------|
| `ACTIVE` | 활성 프로파일 (`local` 또는 `prod`) |
| `JPA_DDL` | Hibernate `ddl-auto` 값 (`none`/`update`/`validate` 등) |
| `JWT_SECRET` | JWT 서명 비밀키 |
| `API_KEY` | OpenAI API 키 |
| `REDIS_HOST` | Redis 호스트 (포트 6379 고정) |
| `SPRING_MAIL_USERNAME` / `SPRING_MAIL_PASSWORD` | Gmail SMTP 계정 / 앱 비밀번호 |
| `AWS_S3_BUCKET` / `AWS_S3_REGION` / `AWS_S3_ACCESS_KEY_ID` / `AWS_S3_SECRET_ACCESS_KEY` | S3 설정 |
| `ADMIN_MAILS` | 관리자 이메일 목록 |

**`local` 프로파일**: `LOCAL_DB_URL`, `LOCAL_DB_USERNAME`, `LOCAL_DB_PASSWORD`
**`prod` 프로파일**: `SPRING_RDS_URL`, `SPRING_RDS_USERNAME`, `SPRING_RDS_PASSWORD`

## 빌드 & 실행

**요구 사항**: JDK 17, PostgreSQL, Redis. 위 환경 변수 설정 필요.

**로컬 실행**

```bash
./gradlew bootRun
```

**JAR 빌드**

```bash
./gradlew bootJar      # build/libs/app.jar 생성
java -jar build/libs/app.jar
```

**Docker**

`Dockerfile`은 `build/libs/app.jar`을 Amazon Corretto 17 이미지에 담아 실행합니다(데이터 분석용 `python3` 포함). 먼저 JAR을 빌드한 뒤 이미지를 만듭니다.

```bash
./gradlew bootJar
docker build -t favicon-backend .
```

`docker-compose.yml`은 Redis와 백엔드(8080 포트)를 함께 띄웁니다.

```bash
docker compose up -d
```

> 빌드·배포 파이프라인은 [CI/CD 문서 (Notion)](https://petite-ox-5a5.notion.site/CI-CD-218045bbe19f8083b44cd1b4a7c77b0f)를 참고하세요.

## API 문서

애플리케이션 실행 후 Swagger UI에서 확인할 수 있습니다.

- Swagger UI: `/swagger-ui.html`
- OpenAPI 스펙: `/v3/api-docs`

## 참고 / 알려진 특이사항

- **포크 프로젝트** — 원본은 [favicon-data/back](https://github.com/favicon-data/back)이며, 본 저장소는 리팩터링 버전입니다.
- **현재 빌드에서 비활성화된 기능** — `FaviconApplication`의 `@ComponentScan`이 `aws` 패키지 전체와 `GPTController`를 제외하고 있어, **S3 업로드/삭제·메타데이터 동기화**(`/s3/**`)와 **GPT 챗**(`/gpt/chat`)은 현재 빈으로 등록되지 않습니다. 활성화하려면 제외 필터를 풀어야 합니다.
- 루트의 `기후_강수_기상청.csv`는 샘플 데이터 파일입니다.
