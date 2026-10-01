# Docker configuration

How to build the Docker images and run the **mock-api** backend and **mock-api-ui**
frontend as containers, individually or together with Docker Compose.

## Contents

- [Overview](#overview)
- [Prerequisites](#prerequisites)
- [Project layout](#project-layout)
- [Quick start (Compose)](#quick-start-compose)
- [The images](#the-images)
  - [Backend image (`mock-api`)](#backend-image-mock-api)
  - [Frontend image (`mock-api-ui`)](#frontend-image-mock-api-ui)
- [Networking & the nginx reverse proxy](#networking--the-nginx-reverse-proxy)
- [Persistence](#persistence)
- [Configuration reference](#configuration-reference)
- [Building images individually](#building-images-individually)
- [Common operations](#common-operations)
- [Verifying the stack](#verifying-the-stack)
- [Troubleshooting](#troubleshooting)

## Overview

Two containers run the system:

| Service    | Image                | Host port | Container port | Role                                   |
|------------|----------------------|-----------|----------------|----------------------------------------|
| `backend`  | `mock-api:latest`    | **8080**  | 8080           | Spring Boot service (Java 11)          |
| `frontend` | `mock-api-ui:latest` | **8081**  | 80             | React SPA served by nginx (+ proxy)    |

The frontend's nginx serves the built SPA **and reverse-proxies** the backend
routes (`/config`, `/endpoint`, `/api/`) to the `backend` container. The browser
therefore talks to a single origin (`http://localhost:8081`), so **no CORS** is
involved in the containerized setup. The backend is also published on `:8080`
for direct access from Postman or curl.

```
                    ┌────────────────────────────────────────┐
  Browser  ───────▶ │ frontend (nginx :80)  →  host :8081     │
                    │   / , /apis/*        → SPA (index.html)  │
                    │   /config, /endpoint ┐                   │
                    │   /api/              │ proxy_pass        │
                    └──────────────────────┼───────────────────┘
                                           ▼
                    ┌────────────────────────────────────────┐
  Postman ────────▶ │ backend (Spring Boot :8080) → host :8080│
                    │   volume: mock_api_data → /data/config… │
                    └────────────────────────────────────────┘
```

## Prerequisites

- **Docker Engine** and the **Compose plugin**. Verify with:
  ```bash
  docker --version
  docker compose version
  ```
- Ports **8080** and **8081** free on the host.
- No local JDK or Node install is required — everything builds inside the images.

## Project layout

The two projects are siblings; the Compose file orchestrates both from the
parent directory.

```
workspace-practice/
├── docker-compose.yml            # orchestrates both services
├── docs/
│   └── DOCKER_CONFIG.md          # this file
├── mock-api/                     # backend build context
│   ├── Dockerfile
│   └── .dockerignore
└── mock-api-ui/                  # frontend build context
    ├── Dockerfile
    ├── .dockerignore
    └── nginx.conf                # SPA serving + reverse proxy
```

## Quick start (Compose)

From `workspace-practice/`:

```bash
# Build both images and start the stack in the background
docker compose up --build -d

# Open the UI
open http://localhost:8081        # macOS  (Linux: xdg-open)

# The backend is reachable directly too
curl http://localhost:8080/endpoint

# Follow logs (all services)
docker compose logs -f

# Stop and remove containers (KEEPS stored configurations)
docker compose down

# Stop and ALSO delete stored configurations
docker compose down -v
```

## The images

### Backend image (`mock-api`)

Multi-stage build (`mock-api/Dockerfile`):

1. **Build stage** — `eclipse-temurin:11-jdk-jammy`. Copies the Gradle wrapper and
   build files first (so dependency resolution caches independently of source
   changes), then the sources, and produces the executable jar:
   ```
   ./gradlew --no-daemon clean bootJar -x test
   ```
   Tests are skipped in the image build for speed — run `./gradlew test` locally
   for the Spock suite.
2. **Runtime stage** — `eclipse-temurin:11-jre-jammy` (slim JRE). Creates a
   non-root `spring` user, pre-creates `/data/configurations`, copies the jar, and
   starts it:
   ```
   ENTRYPOINT ["java", "-jar", "/app/app.jar"]
   ```

Key facts:
- Listens on **8080** (Spring Boot default; no `server.port` override).
- Runs as **non-root**.
- Writes YAML configurations to `FILES_UPLOAD_FOLDER=/data/configurations/`.

### Frontend image (`mock-api-ui`)

Multi-stage build (`mock-api-ui/Dockerfile`):

1. **Build stage** — `node:24-alpine`. Installs dependencies from the lockfile
   (`npm ci`) and builds the production bundle (`npm run build` → `tsc -b && vite build`).
   `VITE_API_BASE_URL` is intentionally left empty so the app issues **relative**
   requests that nginx proxies to the backend.
2. **Runtime stage** — `nginx:1.27-alpine`. Copies `nginx.conf` to
   `/etc/nginx/conf.d/default.conf` and the built bundle to
   `/usr/share/nginx/html`. Serves on **80** (published as host **8081**).

## Networking & the nginx reverse proxy

`mock-api-ui/nginx.conf` does two jobs:

- **Serve the SPA** with a client-side routing fallback:
  ```nginx
  location / {
      try_files $uri $uri/ /index.html;   # e.g. /apis/new resolves to the SPA
  }
  ```
- **Proxy the backend routes** to the `backend` service over the Compose network
  (`http://backend:8080`):
  ```nginx
  location /config   { proxy_pass http://backend:8080; ... }
  location /endpoint { proxy_pass http://backend:8080; ... }
  location /api/     { proxy_pass http://backend:8080; ... }   # trailing slash!
  ```

> **Why `/api/` has a trailing slash:** a bare `location /api` is a prefix match
> that would also capture the SPA route `/apis/...` and forward it to the backend
> (breaking `/apis/new`). Matching `/api/` restricts the proxy to real mock calls
> and lets SPA routes fall through to `index.html`.

`backend` resolves via Docker's internal DNS (the Compose service name); no
host networking or hard-coded IPs are needed.

## Persistence

The backend stores each configuration as `{apiName}.yaml` under
`FILES_UPLOAD_FOLDER`. In Compose this path is backed by the named volume
**`mock_api_data`**:

```yaml
volumes:
  - mock_api_data:/data/configurations
```

- Data **survives** `docker compose restart` and `docker compose down`.
- `docker compose down -v` **removes** the volume and wipes stored configurations.
- Inspect the volume:
  ```bash
  docker volume inspect workspace-practice_mock_api_data
  ```

## Configuration reference

Environment variables (set in `docker-compose.yml` or via `-e`):

| Variable              | Service | Default in image           | Purpose                                             |
|-----------------------|---------|----------------------------|-----------------------------------------------------|
| `FILES_UPLOAD_FOLDER` | backend | `/data/configurations/`    | Directory where YAML configurations are persisted.  |
| `cors.allowed-origins`| backend | `http://localhost:5173`    | CORS origins for `/config` & `/endpoint`. Not needed in the container setup (nginx makes calls same-origin); relevant only if the SPA calls the backend cross-origin. Pass as `CORS_ALLOWED_ORIGINS` / `--cors.allowed-origins`. |

Ports (host → container), defined under each service's `ports:`:

| Service  | Mapping     |
|----------|-------------|
| backend  | `8080:8080` |
| frontend | `8081:80`   |

To change a host port, edit the left-hand side, e.g. `"9090:8080"`, then
`docker compose up -d`.

## Building images individually

Without Compose (from `workspace-practice/`):

```bash
# Backend
docker build -t mock-api:latest ./mock-api

# Frontend
docker build -t mock-api-ui:latest ./mock-api-ui
```

Run them standalone (create a shared network so the proxy can resolve `backend`):

```bash
docker network create mockapi-net

docker run -d --name backend --network mockapi-net \
  -p 8080:8080 \
  -v mock_api_data:/data/configurations \
  mock-api:latest

docker run -d --name frontend --network mockapi-net \
  -p 8081:80 \
  mock-api-ui:latest
```

> The nginx config proxies to the host named `backend`, so the backend container
> must be named `backend` and share a network with the frontend.

## Common operations

```bash
# Rebuild after code changes and restart
docker compose up --build -d

# Rebuild only one service
docker compose build frontend
docker compose up -d frontend

# Status and ports
docker compose ps

# Logs for one service
docker compose logs -f backend

# Shell into a running container
docker compose exec backend sh
docker compose exec frontend sh

# Restart just the backend (data persists via the volume)
docker compose restart backend
```

## Verifying the stack

After `docker compose up --build -d`:

```bash
# 1. Backend up (direct)
curl http://localhost:8080/endpoint            # -> []  (empty on first run)

# 2. UI served
curl -o /dev/null -w "%{http_code}\n" http://localhost:8081/          # -> 200

# 3. SPA client route falls back to index.html
curl -o /dev/null -w "%{http_code}\n" http://localhost:8081/apis/new  # -> 200

# 4. UI -> backend proxy
curl http://localhost:8081/endpoint            # same as (1)

# 5. Create a configuration through the UI origin
curl -X POST http://localhost:8081/config \
  -H "Content-Type: application/json" \
  -d '{"name":"docker-demo","secured":false,"authConfig":null,
       "paths":{"PING":[{"method":"GET","body":"{\"ok\":true}","statusCode":200,"headers":null}]}}'
# -> 201

# 6. Exercise the mocked endpoint through the proxy
#    (mock routes require a JSON content type)
curl -H "Content-Type: application/json" http://localhost:8081/api/docker-demo/PING
# -> {"ok":true}
```

## Troubleshooting

**`npm ci` fails during the frontend build with `ENOTFOUND <corporate-host>`**
The `mock-api-ui/package-lock.json` must resolve against the public npm registry
(`registry.npmjs.org`). If a corporate registry host is baked into the lockfile
(from a developer's global `~/.npmrc`), a clean container build can't reach it.
Regenerate the lockfile against public npm and rebuild:
```bash
cd mock-api-ui
rm -rf node_modules package-lock.json
npm install --registry https://registry.npmjs.org/
cd .. && docker compose build frontend
```

**`/apis/new` (or other SPA routes) return 404 from the container**
Ensure the nginx proxy uses `location /api/` (with the trailing slash), not
`location /api`. See [Networking](#networking--the-nginx-reverse-proxy).

**Mocked API returns `415 Unsupported Media Type`**
The mock routes declare `consumes=application/json`; include the header:
`curl -H "Content-Type: application/json" http://localhost:8081/api/<name>/<op>`.
This is backend behavior, not a Docker issue.

**Port already in use**
Another process holds 8080/8081. Change the host port in `docker-compose.yml`
(e.g. `"9090:8080"`) or stop the conflicting process.

**Configurations disappeared**
`docker compose down -v` deletes the `mock_api_data` volume. Use plain
`docker compose down` to keep data.
