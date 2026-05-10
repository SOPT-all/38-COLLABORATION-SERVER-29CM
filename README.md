# Collaboration Server 29CM

Spring Boot 기반 협업 서버입니다.

## Requirements

- Java 17
- Gradle Wrapper

## Project Docs

- [AI Agent Instructions](AGENTS.md)
- [API Spec Snapshot](docs/api/API_SPEC_SNAPSHOT.md)
- [Requirements](docs/spec/REQUIREMENTS.md)
- [ERD](docs/erd/ERD.md)
- [API Implementation Design](docs/design/API_IMPLEMENTATION.md)

## Run

```bash
./gradlew bootRun
```

## Build

```bash
./gradlew build
```

## Health Check

```bash
curl http://localhost:8080/api/v1/health
```
