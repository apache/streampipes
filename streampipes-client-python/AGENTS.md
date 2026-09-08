# AGENTS Guide (Python Client)

## Scope

Applies to `streampipes-client-python/`. Root `AGENTS.md` applies for repository-wide
rules; the Java/Maven build commands there do not apply here.

## Toolchain

- Poetry manages the environment: `poetry install --with dev` (add `docs,stubs,deployment`
  for everything). Install the hook once: `poetry run pre-commit install`
  (`.pre-commit-config.yaml`).
- Python 3.8 compatibility is enforced (`pyupgrade --py38`).

## Gates (all enforced in CI and by the pre-commit hook)

```bash
make check          # = make mypy + make lint + make unit-tests
make reformat-all   # pyupgrade, autoflake, isort, black — run before committing
```

- `flake8` with `--max-line-length 120`; `black` with `line-length = 120` (`pyproject.toml`).
- `mypy` on `streampipes` and `tests` (`[tool.mypy]` in `pyproject.toml`).
- `pytest --cov=streampipes --cov-fail-under=90` — coverage below 90 % fails the build.
- `interrogate` requires **100 % docstring coverage** (`fail-under = 100`); every public
  module, class and function needs a docstring.

## Conventions (from DEVELOPMENT.md)

- **numpy-style docstrings**; the mkdocs site (`make doc`, `make livedoc`) is generated from them.
- Mirror the Java client's API (`streampipes-client/.../StreamPipesClient.java`) so the
  clients stay consistent.
- Write unit tests alongside the change; the 90 % gate is per run, not per file.
- Every `.py` file carries the ASF license header.

## Layout

- `streampipes/` — package source; `tests/` — unit tests; `docs/` + `mkdocs.yml` — site.
