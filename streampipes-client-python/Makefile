#
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

COMMIT_HASH := $(shell eval git rev-parse HEAD)

.PHONY: check
check: mypy lint unit-tests

.PHONY: doc
doc:
	mkdocs build

.PHONY: lint
lint:
	flake8 streampipes_client --max-line-length 120

.PHONY: livedoc
livedoc: doc
	mkdocs serve --dirtyreload

.PHONY: mypy
mypy:
	mypy streampipes_client --warn-unused-ignores --warn-redundant-casts --pretty

.PHONY: rebase
rebase:
	git fetch && git rebase origin/dev

.PHONY: reformat-all
reformat-all:
	pyupgrade --py38 --keep-runtime-typing
	autoflake -r --remove-all-unused-imports --expand-star-imports --in-place streampipes_client tests
	isort --profile black streampipes_client tests
	black --line-length=120 streampipes_client tests

.PHONY: pre-commit
pre-commit:
	pre-commit run --verbose --all-files

.PHONY: unit-tests
unit-tests:
	pytest --cov=streampipes_client tests/ --cov-fail-under=90 --cov-report term-missing --no-cov-on-fail
