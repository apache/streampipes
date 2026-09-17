# AGENTS Guide (Go Client)

## Scope

Applies to `streampipes-client-go/`. Root `AGENTS.md` applies for repository-wide rules;
the Java/Maven build commands there do not apply here.

## Toolchain and gates

- Module `github.com/apache/streampipes/streampipes-client-go`; CI uses **Go 1.21**
  (`.github/workflows/go-client.yml`).
- CI runs, from this directory:

```bash
goimports -l .      # must print nothing — run goimports -w . before committing
go test ./...
```

## Layout

- `streampipes/` — the client package: `streampipes_client.go`, one `*_api.go` per resource
  (`pipeline_api.go`, `adapter_api.go`, `data_lake_measure_api.go`, ...), `model/`,
  `config/`, `internal/`, `utils/`.
- `examples/` — runnable usage examples; keep them compiling when the API changes.
- `docs/` — Hugo documentation site; `docs/content/en/docs/contribution-guidelines.md`.

## Conventions

- Mirror the Java and Python clients' API surface and naming.
- Add a `*_api.go` file per new resource rather than growing `streampipes_client.go`.
- Every `.go` file carries the ASF license header (`//` comment block, see existing files).
- Add `_test.go` coverage for new API functions; the suite is small, so each one counts.
