# AI Agent Instructions

이 문서는 AI 개발 도구가 이 서버 프로젝트에서 작업할 때 가장 먼저 읽는 진입점이다.

## Read Order

작업을 시작하기 전에 아래 순서대로 문서를 확인한다.

1. [docs/api/API_SPEC_SNAPSHOT.md](docs/api/API_SPEC_SNAPSHOT.md)
2. [docs/spec/REQUIREMENTS.md](docs/spec/REQUIREMENTS.md)
3. [docs/erd/ERD.md](docs/erd/ERD.md)
4. [docs/erd/29cm.dbml](docs/erd/29cm.dbml)
5. Local team wiki: `../38-COLLABORATION-SERVER-29CM.wiki`
6. Existing implementation code

API 작업이 포함되면 아래 "API Specification Access"를 먼저 확인한다.

## Core Rules

- Java version is 17.
- This is a Spring Boot 3.5.x REST API server.
- API path, request, response, HTTP status, and error code follow the teamspace Notion API specification as the SSoT.
- `docs/api/API_SPEC_SNAPSHOT.md` is a dated local snapshot for AI access and local development convenience.
- Product and server behavior follows `docs/spec/REQUIREMENTS.md`.
- ERD and table design follow `docs/erd/29cm.dbml` and `docs/erd/ERD.md`.
- Code structure and conventions follow the local wiki and existing code.
- Do not invent product policy, API fields, response shapes, or error codes.
- If documents conflict, prefer the teamspace Notion API specification for API contracts and `docs/spec/REQUIREMENTS.md` for product/server requirements.
- Keep implementation simple. Do not add unused abstractions, dependencies, security layers, or infrastructure code.

## API Specification Access

- The actual shared API specification lives in the teamspace Notion workspace.
- AI agents may not have permission to access the teamspace Notion document directly.
- A personal/local Notion copy is not the SSoT unless the user explicitly says it has been synchronized from the teamspace document.
- The current local snapshot is `docs/api/API_SPEC_SNAPSHOT.md`.
- If an API task requires exact endpoint, request, response, status, or error code details, ask the user for one of the following before implementing:
  - the relevant section copied from the teamspace API specification,
  - an exported Markdown snapshot of the teamspace API specification,
  - confirmation that a local snapshot file in this repository is up to date.
- Treat the local API snapshot only as a dated snapshot. When the snapshot conflicts with user-provided teamspace content, follow the teamspace content.
- Do not silently rely on stale API details from memory or old deleted AI documents.

## Important Sources

- Original planning spec: https://github.com/lkic1625/sopt-co-se-teams
- Shared API specification: teamspace Notion API document. Ask the user for the current content when API details are needed.
- Original team wiki: `../38-COLLABORATION-SERVER-29CM.wiki`
