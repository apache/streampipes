COMMIT_HASH := $(shell eval git rev-parse HEAD)

format:
	pre-commit run --all-files

doc:
	mkdocs build

livedoc: doc
	mkdocs serve --dirtyreload

rebase:
	git fetch && git rebase origin/master