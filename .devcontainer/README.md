# VS Code Devcontainer

This setup runs third-party services in Docker Compose and starts StreamPipes
from source inside the VS Code devcontainer.

## Services

- CouchDB
- InfluxDB
- NATS
- One workspace container for Maven, Java and Node.js tooling

Core, IIoT extensions and the UI are started with VS Code tasks or debug
launches, not as prebuilt application images.

## First Start

1. Open the repository in VS Code.
2. Run `Dev Containers: Reopen in Container`.
3. Start the app with either:
   - `Run and Debug: Full StreamPipes Dev` for core, IIoT extensions and UI
   - `Tasks: Run Task: dev: start ui` if you only need the Angular dev server

The default URLs are:

- UI: <http://localhost:8082>
- Core: <http://localhost:8030>
- IIoT extensions: <http://localhost:8090>
- CouchDB: <http://localhost:5984>
- InfluxDB: <http://localhost:8086>
- NATS monitoring: <http://localhost:8222>

The default development stack uses NATS for pipeline transport and for
core-to-extension communication.

`SP_DEBUG` is intentionally set to `false` in the devcontainer and VS Code
launch settings. VS Code Java debugging still works through JDWP, but
StreamPipes debug mode rewrites broker hostnames to `localhost`; that breaks
NATS because the broker runs as the sibling Compose service `nats`.

## Isolated Checkouts

For a second checkout or PR worktree, copy `.devcontainer/.env.example` to
`.devcontainer/.env` and change at least `COMPOSE_PROJECT_NAME`.

If two environments should run at the same time, also change the exposed host
ports in `.devcontainer/.env`.

Example:

```bash
COMPOSE_PROJECT_NAME=streampipes-pr-123
SP_CORE_PORT=8130
SP_EXTENSIONS_IIOT_PORT=8190
SP_UI_PORT=8182
SP_COUCHDB_PORT=6984
SP_TS_STORAGE_PORT=9086
SP_NATS_PORT=5222
SP_NATS_MONITORING_PORT=9222
SP_CORE_DEBUG_PORT=5105
SP_EXTENSIONS_IIOT_DEBUG_PORT=5106
```

There is also a helper for GitHub pull requests:

```bash
tools/dev/create-pr-worktree.sh 123
```

It creates `../streampipes-pr-123`, fetches the PR into a local `pr/123`
branch, and writes an isolated `.devcontainer/.env` into that worktree.

## Reset Data

To remove only the data for the current devcontainer project:

```bash
docker compose --env-file .devcontainer/.env -f .devcontainer/docker-compose.yml down -v
```

If no `.devcontainer/.env` exists, omit `--env-file`; Compose uses the default
project name `streampipes-dev`.
