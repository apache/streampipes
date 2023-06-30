<!--
  ~ Licensed to the Apache Software Foundation (ASF) under one or more
  ~ contributor license agreements.  See the NOTICE file distributed with
  ~ this work for additional information regarding copyright ownership.
  ~ The ASF licenses this file to You under the Apache License, Version 2.0
  ~ (the "License"); you may not use this file except in compliance with
  ~ the License.  You may obtain a copy of the License at
  ~
  ~    http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  ~
  -->

# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).


# [0.92.0]

## What's Changed

### Enhancement 🌟

* [[#1564](https://github.com/apache/streampipes/issues/1564)]: Add feature to assign labels to StreamPipes resources
* [[#1522](https://github.com/apache/streampipes/issues/1522)]: Return 404 if requested pipeline does not exist
* [[#1520](https://github.com/apache/streampipes/issues/1520)]: Map http 404 to Java Optional.empty in Java Client
* [[#1518](https://github.com/apache/streampipes/issues/1518)]: Compiling maven submodule with checkstyle check
* [[#1467](https://github.com/apache/streampipes/pull/1467)]: Implement create method in Java client PipelineApi
* [[#1405](https://github.com/apache/streampipes/issues/1405)]: Enable search for Python docs
* [[#1339](https://github.com/apache/streampipes/pull/1339)]: Support backend-only development mode (#1327)
* [[#1319](https://github.com/apache/streampipes/pull/1319)]: chore(ui): provide some more references for support in the 'about' view
* [[#1307](https://github.com/apache/streampipes/issues/1307)]: Geometry Validation processor
* [[#1296](https://github.com/apache/streampipes/issues/1296)]: Swinging Door Trending (SDT) Filter Processor
* [[#1272](https://github.com/apache/streampipes/issues/1272)]: Buffer Processor
* [[#1261](https://github.com/apache/streampipes/issues/1261)]: Add Python changes to release notes
* [[#1259](https://github.com/apache/streampipes/issues/1259)]: Verify authentication on startup of Python client
* [[#1113](https://github.com/apache/streampipes/issues/1113)]: Add buttons to start and stop all adapters
* [[#1107](https://github.com/apache/streampipes/issues/1107)]: Build Github worfklow with OSV scanner
* [[#1101](https://github.com/apache/streampipes/issues/1101)]: Extend StreamPipes API by a health-check endpoint


### Bug fixes 🧰

* [[#1547](https://github.com/apache/streampipes/issues/1547)]: standalone dashborad empty
* [[#1527](https://github.com/apache/streampipes/issues/1527)]: Fix logo url of email templates
* [[#1501](https://github.com/apache/streampipes/pull/1501)]: [hotfix] Fix handling of count queries in data explorer
* [[#1479](https://github.com/apache/streampipes/issues/1479)]: Truncate does not work in data lake configuration
* [[#1439](https://github.com/apache/streampipes/issues/1439)]: Past data exists twice in raw widget
* [[#1391](https://github.com/apache/streampipes/issues/1391)]: Data Explorer filter doesn't work for boolean and `false` value
* [[#1333](https://github.com/apache/streampipes/issues/1333)]: Endpoint to receive pipelines is broken
* [[#1329](https://github.com/apache/streampipes/issues/1329)]: Data Lake measurements endpoint returns no data series when no data between `startDate` and `endDate`
* [[#1327](https://github.com/apache/streampipes/issues/1327)]: Failed to start local backend enviroment
* [[#1308](https://github.com/apache/streampipes/issues/1308)]: [CI] label-pr workflow fails on fork-based PRs
* [[#1291](https://github.com/apache/streampipes/issues/1291)]: Can't aggregate calculation results of the Math processing element
* [[#1199](https://github.com/apache/streampipes/issues/1199)]: UI Semantik Type field is too small to see full term
* [[#793](https://github.com/apache/streampipes/issues/793)]: Empty string as API token name


### Breaking Change 💣

* [[#1040](https://github.com/apache/streampipes/issues/1040)]: Change Event Runtime Name of geometry field


### Deprecation ⚠️

* [[#1115](https://github.com/apache/streampipes/discussions/1115)] **IMPORTANT** In the future we will remove support for data set adapters


### Documentation & Website 📚

* [[#1623](https://github.com/apache/streampipes/pull/1623)]: chore: mark streampipes-wrapper-python as deprecated
* [[#1535](https://github.com/apache/streampipes/pull/1535)]: Add vulnerability report
* [[#1442](https://github.com/apache/streampipes/issues/1442)]: Extend Python docs with dark mode
* [[#1415](https://github.com/apache/streampipes/pull/1415)]: [doc](readme)Enhancement in some Hyperlinks.
* [[#1411](https://github.com/apache/streampipes/issues/1411)]: Create custom error page for Python docs
* [[#1405](https://github.com/apache/streampipes/issues/1405)]: Enable search for Python docs
* [[#1327](https://github.com/apache/streampipes/issues/1327)]: Failed to start local backend enviroment
* [[#1326](https://github.com/apache/streampipes/pull/1326)]: chore(docs): add information on how to develop the UI locally
* [[#1051](https://github.com/apache/streampipes/issues/1051)]: Add further badges to README of Python client


### Dependency Updates 📦

* [[#1623](https://github.com/apache/streampipes/pull/1623)]: chore: mark streampipes-wrapper-python as deprecated
* [[#1577](https://github.com/apache/streampipes/pull/1577)]: Bump tubemq-client from 1.5.0 to 1.6.0
* [[#1574](https://github.com/apache/streampipes/pull/1574)]: Bump amqp-client from 5.16.0 to 5.17.0
* [[#1571](https://github.com/apache/streampipes/pull/1571)]: Remove guice from dependencies
* [[#1570](https://github.com/apache/streampipes/pull/1570)]: Bump annotations from 16.0.2 to 24.0.1
* [[#1566](https://github.com/apache/streampipes/pull/1566)]: Bump mypy from 1.2.0 to 1.3.0 in /streampipes-client-python
* [[#1562](https://github.com/apache/streampipes/pull/1562)]: Bump jackson-databind from 2.14.1 to 2.15.0
* [[#1560](https://github.com/apache/streampipes/pull/1560)]: Bump google-maps-services from 2.1.2 to 2.2.0
* [[#1559](https://github.com/apache/streampipes/pull/1559)]: Bump pyupgrade from 3.3.1 to 3.4.0 in /streampipes-client-python
* [[#1558](https://github.com/apache/streampipes/pull/1558)]: Bump types-requests from 2.29.0.0 to 2.30.0.0 in /streampipes-client-python
* [[#1552](https://github.com/apache/streampipes/pull/1552)]: Bump opencsv from 5.5.2 to 5.7.1
* [[#1550](https://github.com/apache/streampipes/pull/1550)]: Bump commons-compress from 1.22 to 1.23.0
* [[#1549](https://github.com/apache/streampipes/pull/1549)]: Bump engine.io and socket.io in /ui
* [[#1545](https://github.com/apache/streampipes/pull/1545)]: Bump consul from 1.17.6 to 1.18.0
* [[#1542](https://github.com/apache/streampipes/pull/1542)]: Bump jackson.version from 2.14.1 to 2.15.0
* [[#1541](https://github.com/apache/streampipes/pull/1541)]: Bump pre-commit from 3.2.0 to 3.3.0 in /streampipes-client-python
* [[#1540](https://github.com/apache/streampipes/pull/1540)]: Bump flask from 1.1.2 to 2.3.2 in /streampipes-wrapper-python
* [[#1539](https://github.com/apache/streampipes/pull/1539)]: chore(deps): bump several dependencies to resolve vulnerability issues
* [[#1536](https://github.com/apache/streampipes/pull/1536)]: Bump types-requests from 2.28.11.7 to 2.29.0.0 in /streampipes-client-python
* [[#1525](https://github.com/apache/streampipes/pull/1525)]: Bump mkdocs-gen-files from 0.4.0 to 0.5.0 in /streampipes-client-python
* [[#1516](https://github.com/apache/streampipes/pull/1516)]: Bump karma-chrome-launcher from 3.1.1 to 3.2.0 in /ui/projects/streampipes/platform-services
* [[#1512](https://github.com/apache/streampipes/pull/1512)]: Bump jetty-server from 10.0.10 to 10.0.14
* [[#1511](https://github.com/apache/streampipes/pull/1511)]: Bump spring-core from 6.0.7 to 6.0.8 & spring-bot from 3.0.5 to 3.0.6 & spring-security from 6.0.2 to 6.0.3
* [[#1510](https://github.com/apache/streampipes/pull/1510)]: Bump type-parser from 0.7.0 to 0.8.1
* [[#1507](https://github.com/apache/streampipes/pull/1507)]: Bump maven-plugin-plugin from 3.7.0 to 3.8.1
* [[#1506](https://github.com/apache/streampipes/pull/1506)]: Bump pandas-stubs from 1.5.2.230105 to 2.0.0.230412 in /streampipes-client-python
* [[#1503](https://github.com/apache/streampipes/pull/1503)]: Bump peter-evans/create-pull-request from 4 to 5
* [[#1502](https://github.com/apache/streampipes/pull/1502)]: Bump mkdocs-awesome-pages-plugin from 2.8.0 to 2.9.0 in /streampipes-client-python
* [[#1499](https://github.com/apache/streampipes/pull/1499)]: Bump log4j.version from 2.19.0 to 2.20.0
* [[#1498](https://github.com/apache/streampipes/pull/1498)]: Bump pytest from 7.2.1 to 7.3.0 in /streampipes-client-python
* [[#1472](https://github.com/apache/streampipes/pull/1472)]: Bump rdf4j.version from 3.5.0 to 3.7.7


### Uncategorized ❓

* [[#1532](https://github.com/apache/streampipes/pull/1532)]: [hotfix] exclude versions file from pydocs update
* [[#1524](https://github.com/apache/streampipes/pull/1524)]: feature: add workflow that stales PRs
* [[#1517](https://github.com/apache/streampipes/pull/1517)]: [FEATURE] add division as operation for data harmonization rules
* [[#1504](https://github.com/apache/streampipes/issues/1504)]: Data streams are removed from pipelines when exported
* [[#1497](https://github.com/apache/streampipes/pull/1497)]: [hotfix] Move sp-split-section to shared-ui module
* [[#1484](https://github.com/apache/streampipes/pull/1484)]: [hotfix] Improve handling of count queries, improve table widget
* [[#1478](https://github.com/apache/streampipes/issues/1478)]: Distinguish between Consumer and Publisher for the broker
* [[#1475](https://github.com/apache/streampipes/issues/1475)]: Fulfill PEP 561 compatibility
* [[#1465](https://github.com/apache/streampipes/pull/1465)]: [hotfix] Fix configuration for pipeline element development
* [[#1445](https://github.com/apache/streampipes/pull/1445)]: Improve Log info in Processor Test
* [[#1433](https://github.com/apache/streampipes/pull/1433)]: update archetypes template
* [[#1428](https://github.com/apache/streampipes/pull/1428)]: Maintain the same directory structure for source and test
* [[#1406](https://github.com/apache/streampipes/issues/1406)]: Cleanup data explorer query management
* [[#1385](https://github.com/apache/streampipes/issues/1385)]: Remove unused features from data explorer module
* [[#1383](https://github.com/apache/streampipes/issues/1383)]: Introduce environment variable to provide path to file storage of backend
* [[#1368](https://github.com/apache/streampipes/pull/1368)]: Improve create client model script
* [[#1367](https://github.com/apache/streampipes/issues/1367)]: Add `QueryResult` as data type for `DataLakeMeasureEndpoint`
* [[#1362](https://github.com/apache/streampipes/issues/1362)]: Support Kafka in Python client
* [[#1361](https://github.com/apache/streampipes/pull/1361)]: Fix UI container cannot resolve backend IP correctly
* [[#1350](https://github.com/apache/streampipes/pull/1350)]: [hotfix] Use try-with-resource way to fix the potential InfluxDB connection leak in `DataExplorerQueryV4#executeQuery`
* [[#1336](https://github.com/apache/streampipes/issues/1336)]: Create builder for SpQueryResult
* [[#1311](https://github.com/apache/streampipes/pull/1311)]: chore(ci): improve caching for dependencies in pr-validation workflow
* [[#1293](https://github.com/apache/streampipes/issues/1293)]: Rename java module name `streampipes-extensions-management`
* [[#1264](https://github.com/apache/streampipes/issues/1264)]: CLI Installer clean does not delete volumes anymore
* [[#841](https://github.com/apache/streampipes/issues/841)]: Include supported Java versions in CI
* [[#777](https://github.com/apache/streampipes/issues/777)]: 1-class processor model and stylechecks on all processors.geo.jvm
* [[#655](https://github.com/apache/streampipes/issues/655)]: Support change of username and password in profile view


# [0.91.0]

## Appreciation
We would like to express our sincere thanks to Котко Владислав for helping us fix a potential security vulnerability.
In February 2023, he pointed out to us that @streampipes could be a potential target for dependency confusion in NPM.
As a result, we have taken all necessary countermeasures so that there is no longer a threat.
We are very grateful for the support and professional handling.

## What's Changed

### Enhancement 🌟

* [[#1375](https://github.com/apache/streampipes/issues/1375)]: Versioning for Python docs
* [[#1363](https://github.com/apache/streampipes/pull/1363)]: Add support for the Kafka broker in Python
* [[#1254](https://github.com/apache/streampipes/issues/1254)]: Adapt data lake measure endpoint's `get()` method to process query parameter in Python
* [[#1191](https://github.com/apache/streampipes/pull/1191)]: [#1190] Apache TubeMQ (InLong) Adapter & Sink
* [[#1182](https://github.com/apache/streampipes/issues/1182)]: Output data streams for python functions
* [[#1149](https://github.com/apache/streampipes/pull/1149)]: Extend REST API by endpoint to get a specific function definition
* [[#1133](https://github.com/apache/streampipes/issues/1133)]: Support output streams in functions
* [[#1121](https://github.com/apache/streampipes/issues/1121)]: Add option for single file replay in FileStreamAdapter
* [[#1099](https://github.com/apache/streampipes/pull/1099)]: [hotfix] Fix failing e2e tests
* [[#1096](https://github.com/apache/streampipes/issues/1096)]: Improve structure of pipeline execution
* [[#1091](https://github.com/apache/streampipes/issues/1091)]: Reduce warnings on service startup
* [[#1085](https://github.com/apache/streampipes/issues/1085)]: Refactor `FileStreamProtocol`
* [[#1081](https://github.com/apache/streampipes/pull/1081)]: restrict appearance of dependabot PRs
* [[#1077](https://github.com/apache/streampipes/issues/1077)]: Remove legacy method `getNElements` in `IProtocol`
* [[#1069](https://github.com/apache/streampipes/pull/1069)]: Sp 1065
* [[#1065](https://github.com/apache/streampipes/issues/1065)]: Provide e2e tests to validate the different formats of generic adapters
* [[#1050](https://github.com/apache/streampipes/pull/1050)]: Extend metadata for python client publishing
* [[#1031](https://github.com/apache/streampipes/pull/1031)]: [hotfix] Remove unused message interface
* [[#1026](https://github.com/apache/streampipes/issues/1026)]: Fix JUnit tests and activate them in build pipeline
* [[#881](https://github.com/apache/streampipes/pull/881)]: Enable dependabot for ui and bump all minor versions
* [[#859](https://github.com/apache/streampipes/issues/859)]: Create PR validation workflow for streampipes-website
* [[#854](https://github.com/apache/streampipes/issues/854)]: Implement StreamPipesFunctions for Python Client
* [[#792](https://github.com/apache/streampipes/issues/792)]: Rename Python Client
* [[#569](https://github.com/apache/streampipes/issues/569)]: Reorganize streampipes-container modules

### Bug fixes 🧰

* [[#1423](https://github.com/apache/streampipes/issues/1423)]: Update Archetypes to reflect module structure
* [[#1274](https://github.com/apache/streampipes/issues/1274)]: Messaging protocol is not overridden when importing data
* [[#1267](https://github.com/apache/streampipes/pull/1267)]: Fix logging configuration (#1266)
* [[#1266](https://github.com/apache/streampipes/issues/1266)]: Fix logging configuration
* [[#1146](https://github.com/apache/streampipes/issues/1146)]: Session based implementation of IoTDB sink (fix issues in current JDBC based implementation)
* [[#964](https://github.com/apache/streampipes/issues/964)]: CLI command `env` outputs success message in case of an error
* [[#962](https://github.com/apache/streampipes/issues/962)]: Example code in docs for Python code is rendered as markdown
* [[#959](https://github.com/apache/streampipes/issues/959)]: Siddhi processors could not be started in a pipeline
* [[#878](https://github.com/apache/streampipes/issues/878)]: Fix README.md
* [[#862](https://github.com/apache/streampipes/issues/862)]: fix speed calculation divide by 0 error
* [[#858](https://github.com/apache/streampipes/issues/858)]: Remove checkboxes from issue templates

### Breaking changes 💣

* [[#958](https://github.com/apache/streampipes/issues/958)]: Extract REST resource classes from extensions services into own module
* [[#957](https://github.com/apache/streampipes/pull/957)]: Use ExtensionsModelSubmitter over StandaloneModelSubmitter (#956)
* [[#956](https://github.com/apache/streampipes/issues/956)]: Remove deprecated module streampipes-container-standalone
* [[#569](https://github.com/apache/streampipes/issues/569)]: Reorganize streampipes-container modules

### Deprecation ⚠️

* [[#953](https://github.com/apache/streampipes/issues/953)]: Improve handling of Consul env variables
* [[#883](https://github.com/apache/streampipes/pull/883)]: Add checkstyle to streampipes-wrapper-* modules (#820)

### Documentation & Website 📚

* [[#1496](https://github.com/apache/streampipes/pull/1496)]: [hotfix] Adapt broken image links in Python docs
* [[#1419](https://github.com/apache/streampipes/pull/1419)]: docs: spring cleaning for Python docs
* [[#1409](https://github.com/apache/streampipes/pull/1409)]: docs: add matomo tracking to python docs
* [[#1375](https://github.com/apache/streampipes/issues/1375)]: Versioning for Python docs
* [[#1364](https://github.com/apache/streampipes/pull/1364)]: chore: fix example code & add disclaimer for doc versioning
* [[#1253](https://github.com/apache/streampipes/issues/1253)]: Update links in Python example notebooks
* [[#1228](https://github.com/apache/streampipes/pull/1228)]: chore(python-client): rename python package to `streampipes`
* [[#1167](https://github.com/apache/streampipes/pull/1167)]: refactor: remove outdated readme files
* [[#962](https://github.com/apache/streampipes/issues/962)]: Example code in docs for Python code is rendered as markdown
* [[#878](https://github.com/apache/streampipes/issues/878)]: Fix README.md
* [[#859](https://github.com/apache/streampipes/issues/859)]: Create PR validation workflow for streampipes-website
* [[#858](https://github.com/apache/streampipes/issues/858)]: Remove checkboxes from issue templates

### Dependency Updates 📦

* [[#1424](https://github.com/apache/streampipes/issues/1424)]: Add `slf4j-api` to `streampipes-service-base` module
* [[#1419](https://github.com/apache/streampipes/pull/1419)]: docs: spring cleaning for Python docs
* [[#1363](https://github.com/apache/streampipes/pull/1363)]: Add support for the Kafka broker in Python
* [[#1356](https://github.com/apache/streampipes/pull/1356)]: chore: reify project url in package metadata
* [[#1228](https://github.com/apache/streampipes/pull/1228)]: chore(python-client): rename python package to `streampipes`
* [[#1158](https://github.com/apache/streampipes/issues/1158)]: Replace `consul-client` library with `consul-api` library
* [[#1081](https://github.com/apache/streampipes/pull/1081)]: restrict appearance of dependabot PRs
* [[#1080](https://github.com/apache/streampipes/pull/1080)]: chore: Bump @auth0/angular-jwt from 5.0.2 to 5.1.2 in /ui
* [[#1060](https://github.com/apache/streampipes/pull/1060)]: Upgrade Siddhi version to v5.1.27
* [[#1016](https://github.com/apache/streampipes/pull/1016)]: Bump Spring versions, migrate Spring Security classes (#1015)
* [[#1015](https://github.com/apache/streampipes/issues/1015)]: Bump Spring Boot to v3
* [[#911](https://github.com/apache/streampipes/pull/911)]: chore: remove peer dependencies and use postinstall

### Uncategorized ❓

* [[#1263](https://github.com/apache/streampipes/pull/1263)]: feature(gh-actions): create workflow that provides python docs as artifact weekly
* [[#1258](https://github.com/apache/streampipes/issues/1258)]: Simplify/restructure data stream generator
* [[#1230](https://github.com/apache/streampipes/issues/1230)]: Build GitHub workflow to deploy Python library to Pypi
* [[#1229](https://github.com/apache/streampipes/pull/1229)]: chore(gh-actions): configure custom label for GitHub actions
* [[#1227](https://github.com/apache/streampipes/pull/1227)]: chore(asf-yaml): add basic branch protection setup
* [[#1223](https://github.com/apache/streampipes/issues/1223)]: Harmonize handling of environment variables
* [[#1220](https://github.com/apache/streampipes/pull/1220)]: chore(gh-actions): improve pr labeling workflow
* [[#1200](https://github.com/apache/streampipes/issues/1200)]: Refactor InfluxDB adapter and sink
* [[#1197](https://github.com/apache/streampipes/issues/1197)]: Update actions versions in GitHub workflow
* [[#1162](https://github.com/apache/streampipes/issues/1162)]: Change GitHub notification settings in .asf.yaml
* [[#1157](https://github.com/apache/streampipes/issues/1157)]: Upgrade third-party services
* [[#1147](https://github.com/apache/streampipes/pull/1147)]: refactor: further clean up python data model & introduce function definition
* [[#1140](https://github.com/apache/streampipes/issues/1140)]: Redirect to previous view after login
* [[#1132](https://github.com/apache/streampipes/issues/1132)]:  Update e2e tests to use FileStream adapter instead of FileSet
* [[#1126](https://github.com/apache/streampipes/pull/1126)]: refactor: introduce messaging endpoint for python client
* [[#1114](https://github.com/apache/streampipes/issues/1114)]: Create an adapter without starting it
* [[#1104](https://github.com/apache/streampipes/issues/1104)]: Add GitHub wofklow that tags PR
* [[#1100](https://github.com/apache/streampipes/pull/1100)]: chore: adapt setup of python dependencies
* [[#1098](https://github.com/apache/streampipes/pull/1098)]: [hotfix] add stream example to Mkdocs
* [[#1088](https://github.com/apache/streampipes/issues/1088)]: Python MkDocs use old StreamPipes logo
* [[#1058](https://github.com/apache/streampipes/issues/1058)]: Cleanup Streampipes model classes
* [[#1048](https://github.com/apache/streampipes/pull/1048)]: remove outdated maven profile
* [[#1028](https://github.com/apache/streampipes/pull/1028)]:  [hotfix] Add checkstyle to module wrapper-siddhi
* [[#1027](https://github.com/apache/streampipes/pull/1027)]: Sp 1026
* [[#1013](https://github.com/apache/streampipes/issues/1013)]: Update Java version to 17
* [[#1008](https://github.com/apache/streampipes/pull/1008)]: Chore/align pom structure
* [[#989](https://github.com/apache/streampipes/pull/989)]: chore: introduce another layer of abstraction for endpoints
* [[#973](https://github.com/apache/streampipes/pull/973)]: extend python unit tests to Windows
* [[#955](https://github.com/apache/streampipes/issues/955)]: Add e2e test to validate that API docs work
* [[#954](https://github.com/apache/streampipes/pull/954)]: Improve handling of Consul env variables (#953)
* [[#909](https://github.com/apache/streampipes/pull/909)]: Chore/refactor class hierarchy python client
* [[#907](https://github.com/apache/streampipes/pull/907)]: Add example junit test to archetype extensions-jvm
* [[#877](https://github.com/apache/streampipes/issues/877)]: Apply UI linting to all modules
* [[#876](https://github.com/apache/streampipes/pull/876)]: Add ci job for formatting and linting
* [[#846](https://github.com/apache/streampipes/pull/846)]: Add example files python client
* [[#820](https://github.com/apache/streampipes/issues/820)]: Enable check style for the project
* [[#797](https://github.com/apache/streampipes/issues/797)]: Implementing possibility for reprojection Coordinates
* [[#771](https://github.com/apache/streampipes/issues/771)]: UI linting as pre-commit hook

## New Contributors
* @kulbachcedric made their first contribution in https://github.com/apache/streampipes/pull/134
* @smlabt made their first contribution in https://github.com/apache/streampipes/pull/875
* @Ndace-hash made their first contribution in https://github.com/apache/streampipes/pull/921
* @jadireddi made their first contribution in https://github.com/apache/streampipes/pull/1049
* @CryoSolace made their first contribution in https://github.com/apache/streampipes/pull/1059
* @parthsali made their first contribution in https://github.com/apache/streampipes/pull/1095
* @Harry262530 made their first contribution in https://github.com/apache/streampipes/pull/1142
* @WaterLemons2k made their first contribution in https://github.com/apache/streampipes/pull/1205


# [0.90.0]

## What's Changed
### New Features 🚀

* [[#821](https://github.com/apache/streampipes/issues/821)]: Mark data views and dashboards as private elements
* [[#818](https://github.com/apache/streampipes/issues/818)]: Add Nats-based version to installation options
* [[#817](https://github.com/apache/streampipes/issues/817)]: Sort data explorer fields by name
* [[#813](https://github.com/apache/streampipes/issues/813)]: Add "Asset Managment" role to user roles
* [[#805](https://github.com/apache/streampipes/issues/805)]: Create release notes with GitHub issues
* [[#801](https://github.com/apache/streampipes/issues/801)]: Update REST API for data streams
* [[#751](https://github.com/apache/streampipes/issues/751)]: Standalone Functions
* [[#737](https://github.com/apache/streampipes/issues/737)]: Refactor the Pulsar Protocol Adapter
* [[#736](https://github.com/apache/streampipes/issues/736)]: Volume for NGINX configuration
* [[#735](https://github.com/apache/streampipes/issues/735)]: Technical information of data sources in the UI
* [[#732](https://github.com/apache/streampipes/issues/732)]: Display error messages in StreamPipes Connect
* [[#729](https://github.com/apache/streampipes/issues/729)]: Add data model and API to organize StreamPipes views
* [[#694](https://github.com/apache/streampipes/issues/694)]: Refactor the Pulsar sink element
* [[#630](https://github.com/apache/streampipes/issues/630)]: Element Monitoring
* [[#768](https://github.com/apache/streampipes/issues/768)]: Create a framework to perform integration tests
* [[#767](https://github.com/apache/streampipes/issues/767)]: Create and receive storage measure objects in StreamPipes client
* [[#763](https://github.com/apache/streampipes/issues/763)]: Add Nats adapter
* [[#762](https://github.com/apache/streampipes/issues/762)]: Add Nats as supported messaging protocol
* [[#749](https://github.com/apache/streampipes/issues/749)]: Collect and display logs and errors of extensions services
* [[#784](https://github.com/apache/streampipes/issues/784)]: Provide RocketMQ 5.0.0 Dockerfile and docker-compose.yml files
* [[#779](https://github.com/apache/streampipes/issues/779)]: RocketMQ Support
* [[#776](https://github.com/apache/streampipes/issues/776)]: refactor streampipes-processors-change-detection-jvm
* [[#772](https://github.com/apache/streampipes/issues/772)]: Update REST API for pipelines

### Bug fixes 🧰

* [[#1063](https://github.com/apache/streampipes/pull/1063)]: [hotfix] fix header style in release.yaml
* [[#822](https://github.com/apache/streampipes/issues/822)]: Ignore case when logging in with email address
* [[#759](https://github.com/apache/streampipes/issues/759)]: Update GitHub README file
* [[#758](https://github.com/apache/streampipes/issues/758)]: For data export the name of timestamp field is lost
* [[#757](https://github.com/apache/streampipes/issues/757)]: Broken links on homepage's versions overview
* [[#755](https://github.com/apache/streampipes/issues/755)]: Events with missing properties are not displayed in data explorer
* [[#754](https://github.com/apache/streampipes/issues/754)]: Data Explorer does not properly cleanup widget subscriptions
* [[#753](https://github.com/apache/streampipes/issues/753)]: Data Explorer: Unexpected edit mode behaviour
* [[#752](https://github.com/apache/streampipes/issues/752)]: Data Explorer time selector changes upon refresh
* [[#750](https://github.com/apache/streampipes/issues/750)]: Stopped adapters are started on container restart
* [[#740](https://github.com/apache/streampipes/issues/740)]: StreamPipes can not be build with Java 18
* [[#739](https://github.com/apache/streampipes/issues/739)]: Check license file for wrong MIT classifications
* [[#738](https://github.com/apache/streampipes/issues/738)]: Replace current year in NOTICE file with year range
* [[#734](https://github.com/apache/streampipes/issues/734)]: Download data (configured query)
* [[#745](https://github.com/apache/streampipes/issues/745)]: Data Lake download ignores rows with missing values

### Deprecation ⚠

* [[#906](https://github.com/apache/streampipes/pull/906)]: add section about dependency updates to release notes
* [[#805](https://github.com/apache/streampipes/issues/805)]: Create release notes with GitHub issues
* [[#800](https://github.com/apache/streampipes/issues/800)]: unclear missleading  example code writing own processor on website
* [[#769](https://github.com/apache/streampipes/issues/769)]: Improve tutorials
* [[#757](https://github.com/apache/streampipes/issues/757)]: Broken links on homepage's versions overview
* [[#756](https://github.com/apache/streampipes/issues/756)]: Make API Docs available within StreamPipes
* [[#790](https://github.com/apache/streampipes/issues/790)]: Check website and documentation for incubating references


### Dependency Updates 📦

* [[#748](https://github.com/apache/streampipes/issues/748)]: Bump Angular version


### Other changes

* [[#838](https://github.com/apache/streampipes/issues/838)]: streampipes-container-extensions
* [[#834](https://github.com/apache/streampipes/issues/834)]: streampipes-commons
* [[#833](https://github.com/apache/streampipes/issues/833)]: streampipes-archetype-pe-processors-flink
* [[#832](https://github.com/apache/streampipes/issues/832)]: streampipes-archetype-pe-sinks-flink
* [[#830](https://github.com/apache/streampipes/issues/830)]: streampipes-archetype-extensions-jvm
* [[#802](https://github.com/apache/streampipes/issues/802)]: Migrate Jira issues to Github
* [[#791](https://github.com/apache/streampipes/issues/791)]: Update incubator status file
* [[#789](https://github.com/apache/streampipes/issues/789)]: Activate Github discussions
* [[#788](https://github.com/apache/streampipes/issues/788)]: Remove incubating references from source code
* [[#786](https://github.com/apache/streampipes/issues/786)]: Post-graduation tasks
* [[#770](https://github.com/apache/streampipes/issues/770)]: Migrate from Jira to Github
* [[#741](https://github.com/apache/streampipes/issues/741)]: Add Apache header to "strings.en" files in resources
* [[#798](https://github.com/apache/streampipes/issues/798)]: Add license header check to checkstyle configuration

# [0.70.0]

## Sub-task

*   [[STREAMPIPES-535](https://issues.apache.org/jira/browse/STREAMPIPES-535)] - Support JWT signing with private/public key

## Bug

*   [[STREAMPIPES-243](https://issues.apache.org/jira/browse/STREAMPIPES-243)] - Configuration of And Processor is broken
*   [[STREAMPIPES-255](https://issues.apache.org/jira/browse/STREAMPIPES-255)] - Error when importing AdapterDescriptions with file upload references
*   [[STREAMPIPES-515](https://issues.apache.org/jira/browse/STREAMPIPES-515)] - Missing mapping in dev compose files for new docker-compose versions
*   [[STREAMPIPES-521](https://issues.apache.org/jira/browse/STREAMPIPES-521)] - Filter can not be deleted in data explorer
*   [[STREAMPIPES-524](https://issues.apache.org/jira/browse/STREAMPIPES-524)] - No data is shown in data explorer
*   [[STREAMPIPES-529](https://issues.apache.org/jira/browse/STREAMPIPES-529)] - Newly created pipelines break dashboard
*   [[STREAMPIPES-540](https://issues.apache.org/jira/browse/STREAMPIPES-540)] - Data download returns error
*   [[STREAMPIPES-542](https://issues.apache.org/jira/browse/STREAMPIPES-542)] - Web UI pipelines won't import multiple pipelines
*   [[STREAMPIPES-543](https://issues.apache.org/jira/browse/STREAMPIPES-543)] - Using UI can't choose a source for the new dashboard
*   [[STREAMPIPES-547](https://issues.apache.org/jira/browse/STREAMPIPES-547)] - Fix repeating colors for time-series chart
*   [[STREAMPIPES-548](https://issues.apache.org/jira/browse/STREAMPIPES-548)] - Aggregation settings for data-explorer partially not persisted
*   [[STREAMPIPES-550](https://issues.apache.org/jira/browse/STREAMPIPES-550)] - Empty property configuration in data-explorer visualization config
*   [[STREAMPIPES-551](https://issues.apache.org/jira/browse/STREAMPIPES-551)] - Missing naming for (multiple) data sources in visualization config of data-explorer
*   [[STREAMPIPES-553](https://issues.apache.org/jira/browse/STREAMPIPES-553)] - Lite configuration for k8s does not include message broker
*   [[STREAMPIPES-554](https://issues.apache.org/jira/browse/STREAMPIPES-554)] - Data-explorer widgets reload when token is renewed
*   [[STREAMPIPES-564](https://issues.apache.org/jira/browse/STREAMPIPES-564)] - Group by fields don't change in data explorer
*   [[STREAMPIPES-572](https://issues.apache.org/jira/browse/STREAMPIPES-572)] - Fix automatic lower casing when persisting data in connect
*   [[STREAMPIPES-578](https://issues.apache.org/jira/browse/STREAMPIPES-578)] - Data Explorer download does not update measurement
*   [[STREAMPIPES-579](https://issues.apache.org/jira/browse/STREAMPIPES-579)] - Larger live dashboards become unresponsive

## New Feature

*   [[STREAMPIPES-209](https://issues.apache.org/jira/browse/STREAMPIPES-209)] - FileStaticProperty should support filtering for extensions
*   [[STREAMPIPES-534](https://issues.apache.org/jira/browse/STREAMPIPES-534)] - Support authentication for extensions services
*   [[STREAMPIPES-539](https://issues.apache.org/jira/browse/STREAMPIPES-539)] - Support full screen data view in data explorer
*   [[STREAMPIPES-546](https://issues.apache.org/jira/browse/STREAMPIPES-546)] - Support data download of configured query in data explorer
*   [[STREAMPIPES-549](https://issues.apache.org/jira/browse/STREAMPIPES-549)] - Add extensions service for IIoT-related processors and sinks
*   [[STREAMPIPES-559](https://issues.apache.org/jira/browse/STREAMPIPES-559)] - Support templates for adapter configurations
*   [[STREAMPIPES-561](https://issues.apache.org/jira/browse/STREAMPIPES-561)] - Add breadcrumb navigation
*   [[STREAMPIPES-565](https://issues.apache.org/jira/browse/STREAMPIPES-565)] - Allow to export and import StreamPipes resources
*   [[STREAMPIPES-569](https://issues.apache.org/jira/browse/STREAMPIPES-569)] - Export data from data lake configuration
*   [[STREAMPIPES-570](https://issues.apache.org/jira/browse/STREAMPIPES-570)] - Import multiple files at once
*   [[STREAMPIPES-573](https://issues.apache.org/jira/browse/STREAMPIPES-573)] - Make CSV delimiter selectable in download dialog

## Improvement

*   [[STREAMPIPES-192](https://issues.apache.org/jira/browse/STREAMPIPES-192)] - A user has to enter too many names when using the system
*   [[STREAMPIPES-223](https://issues.apache.org/jira/browse/STREAMPIPES-223)] - Add connection retry to consul for pipeline elements when starting up
*   [[STREAMPIPES-228](https://issues.apache.org/jira/browse/STREAMPIPES-228)] - Edit dashboard
*   [[STREAMPIPES-517](https://issues.apache.org/jira/browse/STREAMPIPES-517)] - Update UI to Angular 13
*   [[STREAMPIPES-522](https://issues.apache.org/jira/browse/STREAMPIPES-522)] - Deleting adapter instance after previously stopping adapter throws error
*   [[STREAMPIPES-528](https://issues.apache.org/jira/browse/STREAMPIPES-528)] - Support images in data explorer
*   [[STREAMPIPES-531](https://issues.apache.org/jira/browse/STREAMPIPES-531)] - Extract shared UI modules to Angular library
*   [[STREAMPIPES-533](https://issues.apache.org/jira/browse/STREAMPIPES-533)] - Bump Spring dependencies
*   [[STREAMPIPES-536](https://issues.apache.org/jira/browse/STREAMPIPES-536)] - Escape asterisk in installer/upgrade_versions.sh
*   [[STREAMPIPES-552](https://issues.apache.org/jira/browse/STREAMPIPES-552)] - Cancel subscriptions in data explorer when config changes
*   [[STREAMPIPES-556](https://issues.apache.org/jira/browse/STREAMPIPES-556)] - Add silent period to notifications sink
*   [[STREAMPIPES-557](https://issues.apache.org/jira/browse/STREAMPIPES-557)] - Move notifications icon from iconbar to toolbar
*   [[STREAMPIPES-558](https://issues.apache.org/jira/browse/STREAMPIPES-558)] - Change navigation of connect module
*   [[STREAMPIPES-560](https://issues.apache.org/jira/browse/STREAMPIPES-560)] - Add confirm dialog before leaving data explorer widget view
*   [[STREAMPIPES-575](https://issues.apache.org/jira/browse/STREAMPIPES-575)] - Migrate Math operators from Flink to plain JVM wrapper
*   [[STREAMPIPES-576](https://issues.apache.org/jira/browse/STREAMPIPES-576)] - Migrate transformation processors from Flink to JVM

## Task

*   [[STREAMPIPES-463](https://issues.apache.org/jira/browse/STREAMPIPES-463)] - Merge StreamPipes repos into a single repo
*   [[STREAMPIPES-555](https://issues.apache.org/jira/browse/STREAMPIPES-555)] - Remove feedback button from UI
*   [[STREAMPIPES-581](https://issues.apache.org/jira/browse/STREAMPIPES-581)] - Restructure documentantion

# [0.69.0]

** Sub-task
* [STREAMPIPES-427](https://issues.apache.org/jira/browse/STREAMPIPES-427) - Remove username from REST API paths
* [STREAMPIPES-434](https://issues.apache.org/jira/browse/STREAMPIPES-434) - Use auto-setup over manual setup dialog
* [STREAMPIPES-435](https://issues.apache.org/jira/browse/STREAMPIPES-435) - Add service account authentication
* [STREAMPIPES-436](https://issues.apache.org/jira/browse/STREAMPIPES-436) - Prepare StreamPipes client for pipeline elements and adapters
* [STREAMPIPES-437](https://issues.apache.org/jira/browse/STREAMPIPES-437) - Add initial authorization model
* [STREAMPIPES-439](https://issues.apache.org/jira/browse/STREAMPIPES-439) - Add UI to manage users
* [STREAMPIPES-441](https://issues.apache.org/jira/browse/STREAMPIPES-441) - Add email sending capability
* [STREAMPIPES-453](https://issues.apache.org/jira/browse/STREAMPIPES-453) - Add object-level permission management
* [STREAMPIPES-455](https://issues.apache.org/jira/browse/STREAMPIPES-455) - Add configuration option to provide external hostname
* [STREAMPIPES-457](https://issues.apache.org/jira/browse/STREAMPIPES-457) - Add self-registration and password reset
* [STREAMPIPES-458](https://issues.apache.org/jira/browse/STREAMPIPES-458) - Secure adapter endpoints
* [STREAMPIPES-469](https://issues.apache.org/jira/browse/STREAMPIPES-469) - Merge LICENSE and NOTICE files
* [STREAMPIPES-470](https://issues.apache.org/jira/browse/STREAMPIPES-470) - Fix Maven setup



** Bug
* [STREAMPIPES-226](https://issues.apache.org/jira/browse/STREAMPIPES-226) - Check usage of running adapter instances before deletion
* [STREAMPIPES-237](https://issues.apache.org/jira/browse/STREAMPIPES-237) - Domain properties of imported connect adapter templates are not used
* [STREAMPIPES-259](https://issues.apache.org/jira/browse/STREAMPIPES-259) - Use suitable descriptions in pipeline element config
* [STREAMPIPES-396](https://issues.apache.org/jira/browse/STREAMPIPES-396) - Data Set Adapter does not work with JSON Array
* [STREAMPIPES-407](https://issues.apache.org/jira/browse/STREAMPIPES-407) - Unable to register custom sink
* [STREAMPIPES-424](https://issues.apache.org/jira/browse/STREAMPIPES-424) - ISS Adapter is broken
* [STREAMPIPES-425](https://issues.apache.org/jira/browse/STREAMPIPES-425) - Time-Range in Data Explorer is not updated
* [STREAMPIPES-429](https://issues.apache.org/jira/browse/STREAMPIPES-429) - Error on sinks.notifications.jvm.email
* [STREAMPIPES-440](https://issues.apache.org/jira/browse/STREAMPIPES-440) - Access „Http Server“ adapter endpoint over ui container
* [STREAMPIPES-444](https://issues.apache.org/jira/browse/STREAMPIPES-444) - Fix asset dashboard
* [STREAMPIPES-460](https://issues.apache.org/jira/browse/STREAMPIPES-460) - Runtime options resolver does not support service discovery
* [STREAMPIPES-479](https://issues.apache.org/jira/browse/STREAMPIPES-479) - Custom streampipes extensions missing Spring method
* [STREAMPIPES-484](https://issues.apache.org/jira/browse/STREAMPIPES-484) - Replace SDK methods to receive files with StreamPipes Client requests
* [STREAMPIPES-495](https://issues.apache.org/jira/browse/STREAMPIPES-495) - Icons in documentation are missing
* [STREAMPIPES-496](https://issues.apache.org/jira/browse/STREAMPIPES-496) - streampipes-backend/api/v2/users/profile returning status 405 Method Not Allowed


** New Feature
* [STREAMPIPES-263](https://issues.apache.org/jira/browse/STREAMPIPES-263) - Add bundled extensions module for standalone jvm adapter and pipeline elements
* [STREAMPIPES-295](https://issues.apache.org/jira/browse/STREAMPIPES-295) - HTTP Server adapter
* [STREAMPIPES-335](https://issues.apache.org/jira/browse/STREAMPIPES-335) - Toggle slider as static property
* [STREAMPIPES-398](https://issues.apache.org/jira/browse/STREAMPIPES-398) - Provide custom theme settings
* [STREAMPIPES-408](https://issues.apache.org/jira/browse/STREAMPIPES-408) - Provide time settings in data explorer as query parameters
* [STREAMPIPES-409](https://issues.apache.org/jira/browse/STREAMPIPES-409) - Support count query in data explorer
* [STREAMPIPES-410](https://issues.apache.org/jira/browse/STREAMPIPES-410) - Support auto-aggregation in Data Explorer API v4
* [STREAMPIPES-412](https://issues.apache.org/jira/browse/STREAMPIPES-412) - Add histogram widget to data explorer
* [STREAMPIPES-413](https://issues.apache.org/jira/browse/STREAMPIPES-413) - Add density widget to data explorer
* [STREAMPIPES-414](https://issues.apache.org/jira/browse/STREAMPIPES-414) - Support filtering in Data Explorer API
* [STREAMPIPES-415](https://issues.apache.org/jira/browse/STREAMPIPES-415) - Add pie chart widget to data explorer
* [STREAMPIPES-417](https://issues.apache.org/jira/browse/STREAMPIPES-417) - Provide dashboard id as url parameter in live dashboard
* [STREAMPIPES-421](https://issues.apache.org/jira/browse/STREAMPIPES-421) - Configure collection static properties CSV file
* [STREAMPIPES-423](https://issues.apache.org/jira/browse/STREAMPIPES-423) - Support other languages in CodeInput static property
* [STREAMPIPES-428](https://issues.apache.org/jira/browse/STREAMPIPES-428) - Add map widget to data explorer
* [STREAMPIPES-501](https://issues.apache.org/jira/browse/STREAMPIPES-501) - Add throughput monitoring processor
* [STREAMPIPES-504](https://issues.apache.org/jira/browse/STREAMPIPES-504) - Support tree-based static property configuration


** Improvement
* [STREAMPIPES-204](https://issues.apache.org/jira/browse/STREAMPIPES-204) - Loading time of install pipeline element page takes long
* [STREAMPIPES-229](https://issues.apache.org/jira/browse/STREAMPIPES-229) - Change location of save button in pipeline editor
* [STREAMPIPES-281](https://issues.apache.org/jira/browse/STREAMPIPES-281) - Update consul version
* [STREAMPIPES-376](https://issues.apache.org/jira/browse/STREAMPIPES-376) - Store pipeline element descriptions in CouchDB storage
* [STREAMPIPES-380](https://issues.apache.org/jira/browse/STREAMPIPES-380) - Refactor connect UI
* [STREAMPIPES-383](https://issues.apache.org/jira/browse/STREAMPIPES-383) - Harmonize web servers used by StreamPipes services
* [STREAMPIPES-384](https://issues.apache.org/jira/browse/STREAMPIPES-384) - Show pipeline element icons in installation view
* [STREAMPIPES-386](https://issues.apache.org/jira/browse/STREAMPIPES-386) - Harmonize StreamPipes Connect Service
* [STREAMPIPES-387](https://issues.apache.org/jira/browse/STREAMPIPES-387) - Split StreamPipes Connect worker into two different services
* [STREAMPIPES-397](https://issues.apache.org/jira/browse/STREAMPIPES-397) - Remove obsolete module configs from UI
* [STREAMPIPES-399](https://issues.apache.org/jira/browse/STREAMPIPES-399) - Use new StreamPipes logo in UI
* [STREAMPIPES-401](https://issues.apache.org/jira/browse/STREAMPIPES-401) - Persist data explorer settings
* [STREAMPIPES-402](https://issues.apache.org/jira/browse/STREAMPIPES-402) - Improve data explorer widget config
* [STREAMPIPES-406](https://issues.apache.org/jira/browse/STREAMPIPES-406) - Bump Flink Version
* [STREAMPIPES-418](https://issues.apache.org/jira/browse/STREAMPIPES-418) - Datalake sink should use event schema to detect type of property
* [STREAMPIPES-422](https://issues.apache.org/jira/browse/STREAMPIPES-422) - Word cloud dashboard widget not updating to new events
* [STREAMPIPES-438](https://issues.apache.org/jira/browse/STREAMPIPES-438) - Harmonize Model Submitter
* [STREAMPIPES-443](https://issues.apache.org/jira/browse/STREAMPIPES-443) - Remove duplicate code from the adapter modules
* [STREAMPIPES-447](https://issues.apache.org/jira/browse/STREAMPIPES-447) - Processing Element: Detect Value Change
* [STREAMPIPES-448](https://issues.apache.org/jira/browse/STREAMPIPES-448) - Processing Element: Boolean Filter
* [STREAMPIPES-449](https://issues.apache.org/jira/browse/STREAMPIPES-449) - Update Processing Element API in module streampipes-processors-filters-jvm
* [STREAMPIPES-472](https://issues.apache.org/jira/browse/STREAMPIPES-472) - Reduce dependency overhead of extensions modules
* [STREAMPIPES-474](https://issues.apache.org/jira/browse/STREAMPIPES-474) - Update Maven archetypes
* [STREAMPIPES-477](https://issues.apache.org/jira/browse/STREAMPIPES-477) - CLI logs service name optional
* [STREAMPIPES-480](https://issues.apache.org/jira/browse/STREAMPIPES-480) - Update Spring version
* [STREAMPIPES-486](https://issues.apache.org/jira/browse/STREAMPIPES-486) - Update Flink and add log4j to dependency management
* [STREAMPIPES-487](https://issues.apache.org/jira/browse/STREAMPIPES-487) - Refactor Flink modules to use new service builder pattern
* [STREAMPIPES-489](https://issues.apache.org/jira/browse/STREAMPIPES-489) - Properly stop Flink jobs when running in debug mode
* [STREAMPIPES-490](https://issues.apache.org/jira/browse/STREAMPIPES-490) - Links on wiki page incorrect, they include .../docs/docs.. in url
* [STREAMPIPES-499](https://issues.apache.org/jira/browse/STREAMPIPES-499) - Update Jsplumb dependency
* [STREAMPIPES-502](https://issues.apache.org/jira/browse/STREAMPIPES-502) - Replace reserved keywords of events stored in data lake storage
* [STREAMPIPES-503](https://issues.apache.org/jira/browse/STREAMPIPES-503) - Add slide toggle static property
* [STREAMPIPES-505](https://issues.apache.org/jira/browse/STREAMPIPES-505) - Improve node discovery of OPC-UA adapter
* [STREAMPIPES-509](https://issues.apache.org/jira/browse/STREAMPIPES-509) - Use data lake APIs in live dashboard
* [STREAMPIPES-510](https://issues.apache.org/jira/browse/STREAMPIPES-510) - Provide better overview of pipeline elements in editor
* [STREAMPIPES-511](https://issues.apache.org/jira/browse/STREAMPIPES-511) - Support change of username and password in profile view

** Test
* [STREAMPIPES-476](https://issues.apache.org/jira/browse/STREAMPIPES-476) - Implementation of Pipeline Tests
* [STREAMPIPES-483](https://issues.apache.org/jira/browse/STREAMPIPES-483) - Provide E2E tests for preprocessing rules within the adapter


** Task
* [STREAMPIPES-442](https://issues.apache.org/jira/browse/STREAMPIPES-442) - Upgrade Angular to v12



# [0.68.0]

## Sub-task
* [STREAMPIPES-273](https://issues.apache.org/jira/browse/STREAMPIPES-273) - Remove DataSource concept
* [STREAMPIPES-274](https://issues.apache.org/jira/browse/STREAMPIPES-274) - Add new modules for streampipes-client and API
* [STREAMPIPES-275](https://issues.apache.org/jira/browse/STREAMPIPES-275) - Pipeline Element Templates

## New Features
* [STREAMPIPES-15](https://issues.apache.org/jira/browse/STREAMPIPES-15) - Let backend provide Kafka messaging properties
* [STREAMPIPES-74](https://issues.apache.org/jira/browse/STREAMPIPES-74) - Automatically restart pipelines on system startup
* [STREAMPIPES-245](https://issues.apache.org/jira/browse/STREAMPIPES-245) - Add pipeline monitoring feature
* [STREAMPIPES-248](https://issues.apache.org/jira/browse/STREAMPIPES-248) - Auto-restart pipelines that are stopped on system restart
* [STREAMPIPES-249](https://issues.apache.org/jira/browse/STREAMPIPES-249) - More compact data processor/sink definition
* [STREAMPIPES-250](https://issues.apache.org/jira/browse/STREAMPIPES-250) - Support lists in Siddhi wrapper
* [STREAMPIPES-251](https://issues.apache.org/jira/browse/STREAMPIPES-251) - Add stop feature to cli
* [STREAMPIPES-252](https://issues.apache.org/jira/browse/STREAMPIPES-252) - Support MQTT transport protocol
* [STREAMPIPES-288](https://issues.apache.org/jira/browse/STREAMPIPES-288) - Provide count aggregation in default version
* [STREAMPIPES-294](https://issues.apache.org/jira/browse/STREAMPIPES-294) - Wordcloud visualization in dashboard
* [STREAMPIPES-296](https://issues.apache.org/jira/browse/STREAMPIPES-296) - Status visualization in dashboard
* [STREAMPIPES-303](https://issues.apache.org/jira/browse/STREAMPIPES-303) - Add autocomplete feature for semantic type selection in Connect
* [STREAMPIPES-362](https://issues.apache.org/jira/browse/STREAMPIPES-362) - Live preview in pipeline editor
* [STREAMPIPES-372](https://issues.apache.org/jira/browse/STREAMPIPES-372) - Pipeline Health Check

## Improvements
* [STREAMPIPES-173](https://issues.apache.org/jira/browse/STREAMPIPES-173) - Backend should be run without any dependency on IntelliJ
* [STREAMPIPES-197](https://issues.apache.org/jira/browse/STREAMPIPES-197) - Improve Documentation
* [STREAMPIPES-200](https://issues.apache.org/jira/browse/STREAMPIPES-200) - Too many bold text parts in UI
* [STREAMPIPES-220](https://issues.apache.org/jira/browse/STREAMPIPES-220) - Add requiredFloatParameter builder method with ValueSpecification
* [STREAMPIPES-246](https://issues.apache.org/jira/browse/STREAMPIPES-246) - Remove module app-file-export
* [STREAMPIPES-254](https://issues.apache.org/jira/browse/STREAMPIPES-254) - Add correction value rule to preprocessing pipeline in connect adapters
* [STREAMPIPES-256](https://issues.apache.org/jira/browse/STREAMPIPES-256) - PLC4X S7 adapter should use PooledPlcDriverManager
* [STREAMPIPES-257](https://issues.apache.org/jira/browse/STREAMPIPES-257) - Add the connect master REST API to backend
* [STREAMPIPES-260](https://issues.apache.org/jira/browse/STREAMPIPES-260) - Add connection retry option for consul on startup
* [STREAMPIPES-269](https://issues.apache.org/jira/browse/STREAMPIPES-269) - Improve container-core serialization performance
* [STREAMPIPES-270](https://issues.apache.org/jira/browse/STREAMPIPES-270) - Update Angular version
* [STREAMPIPES-280](https://issues.apache.org/jira/browse/STREAMPIPES-280) - Refactor data explorer management
* [STREAMPIPES-287](https://issues.apache.org/jira/browse/STREAMPIPES-287) - Support GroupBy clause in Siddhi wrapper
* [STREAMPIPES-293](https://issues.apache.org/jira/browse/STREAMPIPES-293) - Extend Siddhi wrapper
* [STREAMPIPES-306](https://issues.apache.org/jira/browse/STREAMPIPES-306) - [OPC UA] Implement pull adapter
* [STREAMPIPES-307](https://issues.apache.org/jira/browse/STREAMPIPES-307) - Extend SDK to add default code block
* [STREAMPIPES-308](https://issues.apache.org/jira/browse/STREAMPIPES-308) - Add String mappings to BooleanToState Processor
* [STREAMPIPES-310](https://issues.apache.org/jira/browse/STREAMPIPES-310) - Improve export speed of data download
* [STREAMPIPES-334](https://issues.apache.org/jira/browse/STREAMPIPES-334) - Customize Label Property in NumberLabeler
* [STREAMPIPES-352](https://issues.apache.org/jira/browse/STREAMPIPES-352) - Add restart policy to compose files
* [STREAMPIPES-360](https://issues.apache.org/jira/browse/STREAMPIPES-360) - Improve Adapter Management
* [STREAMPIPES-369](https://issues.apache.org/jira/browse/STREAMPIPES-369) - Improve user guidance in live dashboard
* [STREAMPIPES-370](https://issues.apache.org/jira/browse/STREAMPIPES-370) - Change widget type in live dashboard
* [STREAMPIPES-374](https://issues.apache.org/jira/browse/STREAMPIPES-374) - Improve management of secret configs
* [STREAMPIPES-375](https://issues.apache.org/jira/browse/STREAMPIPES-375) - Restore pipeline element positions in pipeline canvas

## Bug Fixes
* [STREAMPIPES-126](https://issues.apache.org/jira/browse/STREAMPIPES-126) - [Postgres Sink] Tries to Creating Table for each incomming event
* [STREAMPIPES-127](https://issues.apache.org/jira/browse/STREAMPIPES-127) - [Postgres Sink] Using Wrong datatype while cerating table
* [STREAMPIPES-162](https://issues.apache.org/jira/browse/STREAMPIPES-162) - Postgres Column Names are case sentitiv and save leads to error
* [STREAMPIPES-163](https://issues.apache.org/jira/browse/STREAMPIPES-163) - Exception during save in MYSQl Sink with Timestamp value
* [STREAMPIPES-243](https://issues.apache.org/jira/browse/STREAMPIPES-243) - Configuration of And Processor is broken
* [STREAMPIPES-253](https://issues.apache.org/jira/browse/STREAMPIPES-253) - Missing timestamp property scope (header) in connect
* [STREAMPIPES-264](https://issues.apache.org/jira/browse/STREAMPIPES-264) - Cannot login after k8s restart
* [STREAMPIPES-267](https://issues.apache.org/jira/browse/STREAMPIPES-267) - NumberLabeler does not work with doubles
* [STREAMPIPES-271](https://issues.apache.org/jira/browse/STREAMPIPES-271) - Event property dialog breaks global dialog layout
* [STREAMPIPES-282](https://issues.apache.org/jira/browse/STREAMPIPES-282) - Fix empty route warning in backend
* [STREAMPIPES-284](https://issues.apache.org/jira/browse/STREAMPIPES-284) - NullPointerException at org.apache.streampipes.manager.execution.http.GraphSubmitter.detachGraphs(GraphSubmitter.java:94)
* [STREAMPIPES-285](https://issues.apache.org/jira/browse/STREAMPIPES-285) - Connect worker - Error after PLC turned off
* [STREAMPIPES-302](https://issues.apache.org/jira/browse/STREAMPIPES-302) - File Set Adapter does not start on pipeline invokation
* [STREAMPIPES-312](https://issues.apache.org/jira/browse/STREAMPIPES-312) - Duplicate activemq svc definition in helm chart
* [STREAMPIPES-313](https://issues.apache.org/jira/browse/STREAMPIPES-313) - org.apache.streampipes.connect.adapter.exception.AdapterException: Could not resolve runtime configurations from http://connect-worker-main:8098
* [STREAMPIPES-315](https://issues.apache.org/jira/browse/STREAMPIPES-315) - "==" operator does not work in Number Labeler
* [STREAMPIPES-317](https://issues.apache.org/jira/browse/STREAMPIPES-317) - [Postgres Sink] Connection via SSL not possible
* [STREAMPIPES-321](https://issues.apache.org/jira/browse/STREAMPIPES-321) - Fail to stop pipeline if one of the PE is stopped manually/not running
* [STREAMPIPES-336](https://issues.apache.org/jira/browse/STREAMPIPES-336) - Remove connect-master service build section from docker-compose.yml
* [STREAMPIPES-342](https://issues.apache.org/jira/browse/STREAMPIPES-342) - Maven build failure due to missing Apache license header
* [STREAMPIPES-358](https://issues.apache.org/jira/browse/STREAMPIPES-358) - pod restart freezes a pipeline (k8s)
* [STREAMPIPES-359](https://issues.apache.org/jira/browse/STREAMPIPES-359) - Documentation of pipeline elements does not provide accurate details
* [STREAMPIPES-361](https://issues.apache.org/jira/browse/STREAMPIPES-361) - Prevent running pipelines to be modified
* [STREAMPIPES-365](https://issues.apache.org/jira/browse/STREAMPIPES-365) - Pipeline verification not working
* [STREAMPIPES-366](https://issues.apache.org/jira/browse/STREAMPIPES-366) - Unexpected error when modifying pipelines
* [STREAMPIPES-367](https://issues.apache.org/jira/browse/STREAMPIPES-367) - Avoid orphaned pipelines
* [STREAMPIPES-368](https://issues.apache.org/jira/browse/STREAMPIPES-368) - Avoid duplicated visualizable pipelines
* [STREAMPIPES-371](https://issues.apache.org/jira/browse/STREAMPIPES-371) - Wrong pipeline is started after modification
* [STREAMPIPES-373](https://issues.apache.org/jira/browse/STREAMPIPES-373) - Updating a pipeline breaks running visualizations
* [STREAMPIPES-378](https://issues.apache.org/jira/browse/STREAMPIPES-378) - Mapping properties are not correctly extracted
* [STREAMPIPES-379](https://issues.apache.org/jira/browse/STREAMPIPES-379) - Runtime name is not shown in PLC4X S7 adapter

## Task
* [STREAMPIPES-268](https://issues.apache.org/jira/browse/STREAMPIPES-268) - Update Maven dependencies
* [STREAMPIPES-364](https://issues.apache.org/jira/browse/STREAMPIPES-364) - Improve robustness after pipeline updates
* [STREAMPIPES-377](https://issues.apache.org/jira/browse/STREAMPIPES-377) - Remove obsolete code from knowledge module

# [0.67.0]

## Sub-task
* [STREAMPIPES-133](https://issues.apache.org/jira/browse/STREAMPIPES-133) - Add StaticProperty for entering code
* [STREAMPIPES-134](https://issues.apache.org/jira/browse/STREAMPIPES-134) - Add UserDefinedOutputStrategy
* [STREAMPIPES-238](https://issues.apache.org/jira/browse/STREAMPIPES-238) - Harmonize file upload for Connect & pipeline elements

## Bug
* [STREAMPIPES-8](https://issues.apache.org/jira/browse/STREAMPIPES-8) - Static Property Group not working when only one child is present
* [STREAMPIPES-99](https://issues.apache.org/jira/browse/STREAMPIPES-99) - Upload Adapter Template
* [STREAMPIPES-119](https://issues.apache.org/jira/browse/STREAMPIPES-119) - Redirect to pipeline overview when modifying pipeline not working
* [STREAMPIPES-123](https://issues.apache.org/jira/browse/STREAMPIPES-123) - using specific runtame name leads to exception during extraction
* [STREAMPIPES-142](https://issues.apache.org/jira/browse/STREAMPIPES-142) - Handle non-primitive and primitive event data returned from Siddhi
* [STREAMPIPES-143](https://issues.apache.org/jira/browse/STREAMPIPES-143) - Numerical Filter in Siddhi not working
* [STREAMPIPES-152](https://issues.apache.org/jira/browse/STREAMPIPES-152) - Error when decoding secret properties
* [STREAMPIPES-153](https://issues.apache.org/jira/browse/STREAMPIPES-153) - DateTime is not supported in MySQL adapter
* [STREAMPIPES-154](https://issues.apache.org/jira/browse/STREAMPIPES-154) - Dashboard does not show data
* [STREAMPIPES-155](https://issues.apache.org/jira/browse/STREAMPIPES-155) - SplitArray controller produces wrong output schema
* [STREAMPIPES-158](https://issues.apache.org/jira/browse/STREAMPIPES-158) - AppId is empty in Connect adapters
* [STREAMPIPES-165](https://issues.apache.org/jira/browse/STREAMPIPES-165) - S7 adpater does not work
* [STREAMPIPES-170](https://issues.apache.org/jira/browse/STREAMPIPES-170) - NullPointer in JS Evaluator if no event is returned
* [STREAMPIPES-172](https://issues.apache.org/jira/browse/STREAMPIPES-172) - Default colors of line chart are not readable
* [STREAMPIPES-186](https://issues.apache.org/jira/browse/STREAMPIPES-186) - Remove environment variable to Kafka Rest
* [STREAMPIPES-188](https://issues.apache.org/jira/browse/STREAMPIPES-188) - Cannot import pre-exported pipeline description
* [STREAMPIPES-189](https://issues.apache.org/jira/browse/STREAMPIPES-189) - System error while guess the schema of the data
* [STREAMPIPES-195](https://issues.apache.org/jira/browse/STREAMPIPES-195) - "Do not show again" button in StreamPipes tour not working
* [STREAMPIPES-201](https://issues.apache.org/jira/browse/STREAMPIPES-201) - Initial Installation Setup renders elements besides each other
* [STREAMPIPES-202](https://issues.apache.org/jira/browse/STREAMPIPES-202) - Docs toggle and slide out in PE configuration is behaving oddly 
* [STREAMPIPES-205](https://issues.apache.org/jira/browse/STREAMPIPES-205) - Search function in pipeline editor not working properly
* [STREAMPIPES-207](https://issues.apache.org/jira/browse/STREAMPIPES-207) - Fix image widget in data explorer
* [STREAMPIPES-210](https://issues.apache.org/jira/browse/STREAMPIPES-210) - Map visualization in Dashboard is not working anymore
* [STREAMPIPES-214](https://issues.apache.org/jira/browse/STREAMPIPES-214) - CSV Metadata Enricher does not appear in lite version
* [STREAMPIPES-215](https://issues.apache.org/jira/browse/STREAMPIPES-215) - Data Set adapters are shown Data Stream Tab
* [STREAMPIPES-218](https://issues.apache.org/jira/browse/STREAMPIPES-218) - Pipeline element configuration dialog closes when clicking on editor canvas
* [STREAMPIPES-219](https://issues.apache.org/jira/browse/STREAMPIPES-219) - Refresh button in Data Explorer is not working
* [STREAMPIPES-227](https://issues.apache.org/jira/browse/STREAMPIPES-227) - Missing label in Boolean Counter configuration
* [STREAMPIPES-230](https://issues.apache.org/jira/browse/STREAMPIPES-230) - Large images are not transmitted
* [STREAMPIPES-231](https://issues.apache.org/jira/browse/STREAMPIPES-231) - Images are not shown in Data Explorer
* [STREAMPIPES-233](https://issues.apache.org/jira/browse/STREAMPIPES-233) - Modifying a pipeline breaks existing visualizations
* [STREAMPIPES-240](https://issues.apache.org/jira/browse/STREAMPIPES-240) - NullPointer Exception in processor image enricher
* [STREAMPIPES-241](https://issues.apache.org/jira/browse/STREAMPIPES-241) - CustomTransformOutputStrategy of Processor SplitArray not working
* [STREAMPIPES-242](https://issues.apache.org/jira/browse/STREAMPIPES-242) - Mqtt adapter next button can not be clicked when using authentication
* [STREAMPIPES-244](https://issues.apache.org/jira/browse/STREAMPIPES-244) - JS Evalutor is not working in docker

## New Feature
* [STREAMPIPES-114](https://issues.apache.org/jira/browse/STREAMPIPES-114) - New sink to  support writing data to MQTT.
* [STREAMPIPES-115](https://issues.apache.org/jira/browse/STREAMPIPES-115) - New sink to support writing data to MySQL
* [STREAMPIPES-132](https://issues.apache.org/jira/browse/STREAMPIPES-132) - Add data processor to evaluate JavaScript
* [STREAMPIPES-149](https://issues.apache.org/jira/browse/STREAMPIPES-149) - Processor: State Buffer
* [STREAMPIPES-159](https://issues.apache.org/jira/browse/STREAMPIPES-159) - Processor: Detect Signal Edge
* [STREAMPIPES-160](https://issues.apache.org/jira/browse/STREAMPIPES-160) - Adapter: Flic Button
* [STREAMPIPES-166](https://issues.apache.org/jira/browse/STREAMPIPES-166) - Add ColorPicker static property to SDK
* [STREAMPIPES-208](https://issues.apache.org/jira/browse/STREAMPIPES-208) - Use mapping properties in collections
* [STREAMPIPES-213](https://issues.apache.org/jira/browse/STREAMPIPES-213) - Enable user to delete data in data lake
* [STREAMPIPES-217](https://issues.apache.org/jira/browse/STREAMPIPES-217) - File Management View

## Improvement
* [STREAMPIPES-11](https://issues.apache.org/jira/browse/STREAMPIPES-11) - Cleanup POM files
* [STREAMPIPES-117](https://issues.apache.org/jira/browse/STREAMPIPES-117) - Share IntelliJ run configuration in version control
* [STREAMPIPES-118](https://issues.apache.org/jira/browse/STREAMPIPES-118) - Add configuration file to S7 adapter
* [STREAMPIPES-128](https://issues.apache.org/jira/browse/STREAMPIPES-128) - Upload Excel Option for PLC4x (S7) Adapter
* [STREAMPIPES-129](https://issues.apache.org/jira/browse/STREAMPIPES-129) - Upload Excel Option for PLC4x (S7) Adapter
* [STREAMPIPES-130](https://issues.apache.org/jira/browse/STREAMPIPES-130) - Image upload adapter
* [STREAMPIPES-131](https://issues.apache.org/jira/browse/STREAMPIPES-131) - Increase Accuracy of the Geo Distance Calculation 
* [STREAMPIPES-136](https://issues.apache.org/jira/browse/STREAMPIPES-136) - Limit container size in cli and installer
* [STREAMPIPES-141](https://issues.apache.org/jira/browse/STREAMPIPES-141) - Refactor all adapter implementations to use assets
* [STREAMPIPES-145](https://issues.apache.org/jira/browse/STREAMPIPES-145) - Migrate pipeline editor module from AngularJS to Angular
* [STREAMPIPES-146](https://issues.apache.org/jira/browse/STREAMPIPES-146) - Support CustomOutputStrategy in SiddhiEventEngine
* [STREAMPIPES-156](https://issues.apache.org/jira/browse/STREAMPIPES-156) - Processor: State Labeler
* [STREAMPIPES-167](https://issues.apache.org/jira/browse/STREAMPIPES-167) - IntelliJ Configurations for Extension as Project Files
* [STREAMPIPES-177](https://issues.apache.org/jira/browse/STREAMPIPES-177) - Migrate pipeline details view from AngularJS to Angular
* [STREAMPIPES-178](https://issues.apache.org/jira/browse/STREAMPIPES-178) - Migrate pipeline view to Angular
* [STREAMPIPES-190](https://issues.apache.org/jira/browse/STREAMPIPES-190) - Migrate pipeline element installation from AngularJS to Angular
* [STREAMPIPES-191](https://issues.apache.org/jira/browse/STREAMPIPES-191) - Remove MyElements view
* [STREAMPIPES-193](https://issues.apache.org/jira/browse/STREAMPIPES-193) - Remove AngularJS dependencies from core modules
* [STREAMPIPES-194](https://issues.apache.org/jira/browse/STREAMPIPES-194) - Remove icon description from widgets description in dashboard
* [STREAMPIPES-196](https://issues.apache.org/jira/browse/STREAMPIPES-196) - Refactor existing installer
* [STREAMPIPES-198](https://issues.apache.org/jira/browse/STREAMPIPES-198) - Fix progress bar background on startup
* [STREAMPIPES-199](https://issues.apache.org/jira/browse/STREAMPIPES-199) - Remove advanced settings from setup
* [STREAMPIPES-203](https://issues.apache.org/jira/browse/STREAMPIPES-203) - Add more options to install pipeline elements page
* [STREAMPIPES-206](https://issues.apache.org/jira/browse/STREAMPIPES-206) - Remove tson-ld and deprecated UI models
* [STREAMPIPES-211](https://issues.apache.org/jira/browse/STREAMPIPES-211) - Add Polling interval for PLC4X S7 adaptrer
* [STREAMPIPES-216](https://issues.apache.org/jira/browse/STREAMPIPES-216) - Move watertank simulator source to StreamPipes Connect
* [STREAMPIPES-221](https://issues.apache.org/jira/browse/STREAMPIPES-221) - Update maven archetypes for pipeline elements
* [STREAMPIPES-222](https://issues.apache.org/jira/browse/STREAMPIPES-222) - Upgrade Spring version
* [STREAMPIPES-225](https://issues.apache.org/jira/browse/STREAMPIPES-225) - Bundle all Flink pipeline elements
* [STREAMPIPES-232](https://issues.apache.org/jira/browse/STREAMPIPES-232) - Trigger Github Actions to push to Dockerhub on release preparation branches
* [STREAMPIPES-235](https://issues.apache.org/jira/browse/STREAMPIPES-235) - Add html font format only option as freetext static property
* [STREAMPIPES-236](https://issues.apache.org/jira/browse/STREAMPIPES-236) - Fix Telegram sink using html font format option in request
* [STREAMPIPES-239](https://issues.apache.org/jira/browse/STREAMPIPES-239) - Multi-arch docker image for Flink

## Task
* [STREAMPIPES-140](https://issues.apache.org/jira/browse/STREAMPIPES-140) - Add streampipes-maven-plugin to core


## [0.66.0]
## New Features
* New live dashboard
* New notification view
* Adapter icons canbe provided by container
* Multiple properties are supported in PLC4X adapter
* New data processor to merge data streams by timestamp
* New data processor to enrich streams
* Multi-arch docker images
* New adapter for NETIO power sockets
* New data sink for MQTT
* New data processor for numerical and text values

## Improvements
* Improvements to the Kafka Publisher Sink
* Improvements to the Notification Sink
* Upgrade to latest Angular version, improved UI build process for smaller files
* Consider domain property for live preview
* Support mapping properties in TSON-LD
* Update RDF4J and Empire dependencies
* Upgrade Siddhi version

## Bug fixes
* Password field in postgres sink is now marked as password
* Fix a bug in the REST pull adapter
* Fix broken links in UI
* Fix bug that caused pipelines not to be properly saved
* Many more minor bug fixes

## [0.65.0-pre-asf] - 2019-11-23
## New features

* Added a welcome screen to the UI during startup
* Added an umbrella pipeline element to reduce memory requirements of lite version
* Bumped Flink to latest version 1.9.1
* Added CSV enrichment processor
* Added event counter processor
* Support FileStaticProperty in Pipeline Editor

## Improvements

* Do not trigger cache refresh of pipeline elements during initial installation
* Websocket URL in live dashboard does not depend anymore on hostname
* Optimize Dockerfiles
* Installer now works without providing a hostname
* Optimize caching of pipeline element descriptions

## Bug fixes

* Fixed a bug in the OPC-UA Adapter
* Fixed a bug that prevented error messages to be shown in the pipeline view
* Fixed a bug that cause the pipeline modification to fail

## [0.64.0-pre-asf] - 2019-09-19
## New features

* Added a new StreamPipes Connect adapter for Apache PLC4X
* Added a new StreamPipes Connect adapter for Apache Pulsar
* Added a new data sink to send events to Apache Pulsar

## Improvements

* All StreamPipes services use a new Docker Image based on OpenJ9 which drastically reduces memory consumption


## [0.63.0-pre-asf] - 2019-09-05
## New features

* Added a new static property that handles secrets such as passwords
* Added a new static property to specify property groups
* Added a new external event processor in preparation for the upcoming python wrapper
* Added configuration options to FileStreamProtocol
* Pipeline Elements provide their own documentation and icons
* Added support for binary message formats: SMILE, FST and CBOR
* Added a new processor boolean inverter 
* Added an OPC-UA adapter to Connect
* Added a new random data generator to Connect
* Added a new IoTDB sink
* Added a new OPC-UA sink
* Added a new settings page to select preferred message formats
* Added support for runtime-resolvable static properties in Connect
* Added a new static property StaticPropertyAlternatives that handles alternatives
* Extracted Connect adapters from Backend to new worker-based architecture
* Added support for multiple dashboards within one pipeline 
* Extracted RDF4J HTTP repository to own service
* Added a feature to gracefully stop pipelines when containers are stopped
* Added support for Alternatives/Group static properties to Connect
* Added a feedback button 
* Added authentication to MQTT adapter
* Added improved asset support for data streams


## Bug Fixes

* Uninstallation of data sinks not working
* Duplicated events in pipelines with two dashboard sinks
* Trend detection fires too often 
* Rules in ROS adapter are not working
* MQTT Protocol NullPointer in Guess Schema
* Unit conversion is not stored in connect
* Error when reading domain properties
* Shared usage of transport protocols and formats breaks pipeline element installation 
* Modifying links between pipeline elements
* Validation of alternative configuration at MQTT adapter not working as expected
* Dashboard does not work after editing pipeline
* Dots in keys within StreamPipes Connect are currently not working
* Error in Dashboard with ROS messages with header field
* CSV Format does not guess numbers correctly 
* AppendOutputStrategy: Renaming does not work correctly
* Wrong extractor for Runtime-resolvable selections in documentation
* ProtocolMatch adds wrong error message
* Uninstalling pipeline elements not working

## Improvements

* Customize event output in Siddhi wrapper 
* Connect not showing Error Messages
* Improve edit schema dialog in Connect 
* Directly mark event property as timestamp 
* Avoid using function calls in Connect UI 
* Make UI more browser-independent
* Let DeclarersSingleton declare supported protocols 
* Improve support for runtime resolvable static properties 
* Package StreamPipes backend as jar instead of war file  
* Migrate pipeline element containers to Spring Boot
* Pipeline Tutorial still has Field Hasher in last step
* Migrate Connect containers to Spring Boot  
* Enable gzip compression in UI
* In Installation Dialogue Enter not working as expected 
* Extended and improved documentation

## Minors / Library updates

* Update Kafka
* Update Flink
* Update Maven plugin dependencies
* Update Powermock Version
* Update jetty version
* Update Checkstyle version 
* Add method to SDK to get type of Mapping Property
* Update Jackson libraries
* Update documentation
* Update Maven archetypes

## [0.62.0-pre-asf] - 2019-05-22
### Added
* Always show consistency check messages of pipeline elements
* StreamPipes Connect: Sort adapters by name
* Add categories to StreamPipes Connect
* Add Wikipedia adapter
* Add Coindesk adapter
* Add IEXCloud adapter
* Extract labels and descriptions to external resources file
* Add extractor for Static Properties in all Connect adapters
* Implement Interfaces for historic data access
* Add description for Connect configuration properties

### Changed / Bug Fixes
* StreamPipes Connect: Ensure correct ordering of static properties
* Adapter Event-Property Order
* Fix bug in data set support of Connect 
* Upgrade UI to latest Angular version 7
* Refactor authentication/configuration checks in UI
* Ensure correct ordering of stream requirements
* Fix link to documentation in StreamPipes UI
* Improve behaviour of customizing window

### Removed


## [0.61.0-pre-asf] - 2019-03-20
### Added
- Improved validation of pipelines in the pipeline editor
- Case-insensitive pipeline element search
- Customize dialog is hidden if no static properties are present
- Export and import of Adapter descriptions
- Migration guide for SIP-08
- New map visualization
- Improved support for complex data types
- Introduction of new event model
- New Maven archetype: pe-source
- SSL support

### Changed / Bug Fixes
- Bug Fix: Tutorial not shown
- Bug Fix: Transform Output Strategy not working
- Refactor Maven archetypes to support new event model and wrapper classes
- Upgrade to latest AngularJS version
- Ensure correct ordering of static properties
- Bug Fix: AnyStaticProperty not working
- Bug Fix: Pipeline can be stored without a Sink
- Bug Fix: Pipeline modelling: duplicated runtimeNames
- Use of the UI without Internet
- Bug Fix: CollectionStaticProperty not working

### Removed
- OpenSenseMap adapter due to API changes


## [0.60.1-pre-asf] - 2018-11-28
### Added
* Maven archetypes to easily create new pipeline elements using the SDK
### Changed
* UI improvements
* Several bug fixes


## [0.60.0-pre-asf] - 2018-11-14
### Added
- Beta release of StreamPipes Connect Library
- Tutorials for better user guidance
- New Wrapper for the Siddhi CEP engine
- New Project streampipes-pipeline-elements contains more than 40 new pipeline elements

### Changed
- Various bug fixes and stability improvements
- Many UX improvements (e.g., harmonized styles)
- Dashboard does not reload after new visualization type has been created
- Improved test coverage

### Removed

## [0.55.2-pre-asf] - 2018-05-08
### Added
- The [installer](https://www.github.com/streampipes/streampipes-installer) makes it easy to install StreamPipes on Linux, MacOS and Windows
- Live data preview for data streams in the pipeline editor
- Initial support for data sets
- Default for configurations can now be provided as environment variable, with the same name

### Changed
- Pipeline elements can be directly installed at installation time
- Extended the SDK to create new pipeline elements
- Several UI improvements to make the definition of pipelines more intuitive
- Several bug fixes and code improvements

### Removed
