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

.PHONY: check
check: mypy lint unit-tests
	poetry run interrogate -c pyproject.toml

.PHONY: doc
doc:
	poetry run mkdocs build

.PHONY: lint
lint:
	poetry run flake8 streampipes --max-line-length 120

.PHONY: livedoc
livedoc: doc
	poetry run mkdocs serve --dirtyreload

.PHONY: mypy
mypy:
	poetry run mypy streampipes --config-file pyproject.toml

.PHONY: rebase
rebase:
	git fetch && git rebase origin/dev

.PHONY: reformat-all
reformat-all:
	poetry run pyupgrade --py38 --keep-runtime-typing
	poetry run autoflake -r --remove-all-unused-imports --expand-star-imports --in-place streampipes tests
	poetry run isort --settings-file pyproject.toml
	poetry run black --line-length=120 streampipes tests

.PHONY: pre-commit
pre-commit:
	git ls-files -- 'streampipes/**/*' | xargs poetry run pre-commit run --verbose --files

.PHONY: unit-tests
unit-tests:
	poetry run pytest --cov=streampipes tests/ --cov-fail-under=90 --cov-report term-missing --no-cov-on-fail
