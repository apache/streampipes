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

# [0.97.0]

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


### Bug  🧰

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

### Breakingchange 💣

* [[#1040](https://github.com/apache/streampipes/issues/1040)]: Change Event Runtime Name of geometry field


### Deprecation ⚠️
The following features will be removed in the next version:

- **Method:** `IParameterExtractor.selectedTreeNodesInternalNames`
  - Use `selectedTreeNodesInternalNames(String, Class)` instead.

- **Method:** `SpServiceDefinitionBuilder.registerMessagingFormat`
- **Method:** `SpServiceDefinitionBuilder.registerMessagingFormats`
- **Method:** `AbstractProcessingElementBuilder.supportedFormats`
- **Class:** `SupportedFormats`

- **Method:** `IStreamPipesClient.registerDataFormat`
  - Manual registration of data formats is no longer required.

- **Class:** `CreateNestedTransformationRule`
- **Class:** `CreateNestedRuleDescription`
  - Adding nested properties to events in the adapter is no longer supported due to increased event complexity.
  - To migrate, all adapters must remove references to this rule.

- **Concept:** `List<URI> domainProperties` in `EventProperty` has been replaced with `String semanticType`.
  - All APIs using this model must be updated accordingly.

- **Component:** The Live Dashboard has been replaced with an updated version of the Data Explorer.
  - To migrate, existing dashboards must be manually transitioned to the new Data Explorer Dashboards and Data View.


### Dependencies  📦

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


# [0.95.1]

## What's Changed

### Bug fixes 🧰

* [[#2944](https://github.com/apache/streampipes/issues/2944)]: Fix S7 PLC connection issue

# [0.95.0]

## What's Changed

### Enhancement 🌟

* [[#2659](https://github.com/apache/streampipes/issues/2659)]: Show adapter status light in adapter overview
* [[#2657](https://github.com/apache/streampipes/issues/2657)]: No icon tooltips are shown in dashboard
* [[#2511](https://github.com/apache/streampipes/pull/2511)]: feat: Create adapter for Open Industry 4.0 devices
* [[#2379](https://github.com/apache/streampipes/issues/2379)]: Inconsistent behavior in `EditorService` API calls to Pipelines REST API
* [[#2312](https://github.com/apache/streampipes/issues/2312)]: Extend Notification sinks by silent period
* [[#2311](https://github.com/apache/streampipes/pull/2311)]: Extend MS Teams sink by a silent period
* [[#2283](https://github.com/apache/streampipes/pull/2283)]: extend monitoring by adding metrics for adapters
* [[#2269](https://github.com/apache/streampipes/issues/2269)]: Support basic auth for Prometheus monitoring endpoint
* [[#2252](https://github.com/apache/streampipes/issues/2252)]: Enhance Data Lake Sink Schema Management Options
* [[#2248](https://github.com/apache/streampipes/pull/2248)]: introduce sink for MS Teams
* [[#2199](https://github.com/apache/streampipes/issues/2199)]: Nginx configuration parameters as environment variables
* [[#2192](https://github.com/apache/streampipes/issues/2192)]: Add download button for assets in StreamPipes
* [[#2144](https://github.com/apache/streampipes/issues/2144)]: Enhance Static Properties by UI validation
* [[#2107](https://github.com/apache/streampipes/issues/2107)]: Display File Types in different colors
* [[#2096](https://github.com/apache/streampipes/issues/2096)]: Assets need to be updated as well (only for adapters)
* [[#2074](https://github.com/apache/streampipes/issues/2074)]: Add download button for files in StreamPipes
* [[#1936](https://github.com/apache/streampipes/issues/1936)]: StreamPipes Connect: Ease deletion of adapters
* [[#1865](https://github.com/apache/streampipes/issues/1865)]: New Processor: Datetime from String
* [[#1119](https://github.com/apache/streampipes/issues/1119)]: Remove dependency angular-datatables and datatables.net
* [[#782](https://github.com/apache/streampipes/issues/782)]: Migrate test setup to JUnit 5 (Jupiter)
* [[#690](https://github.com/apache/streampipes/issues/690)]: Remove lodash dependency from ui
* [[#686](https://github.com/apache/streampipes/issues/686)]: Enable connect adapter service development when core runs in Docker
* [[#422](https://github.com/apache/streampipes/issues/422)]: Update Consul key value configuration for pipeline elements when reconnecting
* [[#376](https://github.com/apache/streampipes/issues/376)]: Add adapter documentation
* [[#232](https://github.com/apache/streampipes/issues/232)]: Required/optional static properties

### Bug fixes 🧰

* [[#2894](https://github.com/apache/streampipes/issues/2894)]: deleteTransformationRule.spec.ts test is flaky
* [[#2890](https://github.com/apache/streampipes/issues/2890)]: Websocket Server stops sending message when the pipeline instantiating it is stopped and restarted even without any modification
* [[#2886](https://github.com/apache/streampipes/issues/2886)]: Bug when deleting nested properties in Adapter
* [[#2862](https://github.com/apache/streampipes/issues/2862)]: OPCUA Adapter "Add All Direct Children" "+" button not working
* [[#2856](https://github.com/apache/streampipes/issues/2856)]: File Stream Adapter ignores speed up factor
* [[#2855](https://github.com/apache/streampipes/issues/2855)]: Issue with `DeleteTransformationRule` in Adapter
* [[#2853](https://github.com/apache/streampipes/pull/2853)]: fix timestampTranfsformationRuleDescription metadata map
* [[#2850](https://github.com/apache/streampipes/pull/2850)]: fix ToTransformedSchemaConverter set HEADER_PROPERTY
* [[#2848](https://github.com/apache/streampipes/pull/2848)]: fix: Add error handling to OI4 and MQTT adapters
* [[#2842](https://github.com/apache/streampipes/pull/2842)]: fix PipelineElementTemplateVisitor SecretStaticProperty value
* [[#2841](https://github.com/apache/streampipes/pull/2841)]: guessSchema the encrypted secretValue should be decrypted when used
* [[#2836](https://github.com/apache/streampipes/issues/2836)]: Cannot deploy a pipeline with Trend data processor
* [[#2830](https://github.com/apache/streampipes/issues/2830)]: Data Adapter losing Dimension selection
* [[#2820](https://github.com/apache/streampipes/issues/2820)]: Asset Dashboard loosing picture when going to Edit mode
* [[#2815](https://github.com/apache/streampipes/issues/2815)]: Pipeline validation in pipeline editor
* [[#2756](https://github.com/apache/streampipes/pull/2756)]: fix: OI4 adapter can only read one sensor measurement per event
* [[#2751](https://github.com/apache/streampipes/pull/2751)]: fix: Add MatTableModule to profile module
* [[#2661](https://github.com/apache/streampipes/issues/2661)]: No user feedback when self registration fails
* [[#2650](https://github.com/apache/streampipes/issues/2650)]: Registration configuration does not work
* [[#2648](https://github.com/apache/streampipes/issues/2648)]: Email configuration view is broken
* [[#2646](https://github.com/apache/streampipes/pull/2646)]: fix: Disable live preview subscription when closing dialog
* [[#2597](https://github.com/apache/streampipes/issues/2597)]: Data Explorer time series chart gets broken for high-frequency data
* [[#2591](https://github.com/apache/streampipes/issues/2591)]: Time series charts in data explorer do not work with high data frequency
* [[#2577](https://github.com/apache/streampipes/issues/2577)]: Pipeline Element Templates do not work
* [[#2556](https://github.com/apache/streampipes/issues/2556)]: When Extensions are used, starting multiple "service" can result in multiple identical elements in the ui
* [[#2553](https://github.com/apache/streampipes/issues/2553)]: StreamPipes python functions error for not started adapter
* [[#2551](https://github.com/apache/streampipes/issues/2551)]: StreamPipes function does always create a new data stream
* [[#2550](https://github.com/apache/streampipes/issues/2550)]: Data Stream created by Python function is not entirely correct
* [[#2549](https://github.com/apache/streampipes/issues/2549)]: Property renaming in the FileStream Adapter does not work
* [[#2537](https://github.com/apache/streampipes/pull/2537)]: fix: restart multiple adapter instances per worker
* [[#2520](https://github.com/apache/streampipes/issues/2520)]: Issue with File Cache Persistence in Extensions Service
* [[#2493](https://github.com/apache/streampipes/issues/2493)]: Permission dialog doesn't read users when deselecting them
* [[#2491](https://github.com/apache/streampipes/issues/2491)]: OPC-UA adapter resets values when editing adapter
* [[#2473](https://github.com/apache/streampipes/issues/2473)]: Nginx configuration error during a migration from version 0.93.0 to 0.95.0.
* [[#2468](https://github.com/apache/streampipes/issues/2468)]: Adapter are not always restarted on system restart
* [[#2465](https://github.com/apache/streampipes/issues/2465)]: Date selection in data explorer download dialog broken
* [[#2461](https://github.com/apache/streampipes/issues/2461)]: 400 error with using email notification
* [[#2458](https://github.com/apache/streampipes/issues/2458)]: Add timestamp rule malfunction after adapter edit
* [[#2396](https://github.com/apache/streampipes/issues/2396)]: Should Prevent Multiple Clicks on Tutorial Button
* [[#2372](https://github.com/apache/streampipes/issues/2372)]: Resolve Nightly E2E Test Failures
* [[#2361](https://github.com/apache/streampipes/issues/2361)]: Pipeline element template endpoint missing RequestBody
* [[#2353](https://github.com/apache/streampipes/issues/2353)]: NullPointer Exception when misconfiguration resource files for processing element
* [[#2337](https://github.com/apache/streampipes/issues/2337)]: Can't access "Connect" with User Role "Connect Admin"
* [[#2336](https://github.com/apache/streampipes/issues/2336)]: Live dashboard widgets do not display data when option `same frequency` is used
* [[#2335](https://github.com/apache/streampipes/pull/2335)]: fix: orchestrate adapter health check such that monitoring of adapters works as expected
* [[#2329](https://github.com/apache/streampipes/issues/2329)]: Python client throws exception on connection
* [[#2328](https://github.com/apache/streampipes/pull/2328)]: fix: update of metrics in AdapterHealthCheck
* [[#2326](https://github.com/apache/streampipes/issues/2326)]: When uploading large files response status 413 is returned
* [[#2323](https://github.com/apache/streampipes/pull/2323)]: fix: Modify schema update label
* [[#2322](https://github.com/apache/streampipes/pull/2322)]: fix: ensure Spring uses the correct serializer in extensions service
* [[#2292](https://github.com/apache/streampipes/issues/2292)]: User Groups broken
* [[#2275](https://github.com/apache/streampipes/pull/2275)]: fix: filter for running adapter instances in adapter health check
* [[#2257](https://github.com/apache/streampipes/pull/2257)]: fix: emit pipeline metrics even if no pipeline is running
* [[#2235](https://github.com/apache/streampipes/issues/2235)]: No pipeline connections when editing a pipeline
* [[#2221](https://github.com/apache/streampipes/issues/2221)]: FIle upload does not allow for multiple files
* [[#2183](https://github.com/apache/streampipes/issues/2183)]: Streamline module names in installer and docs
* [[#2106](https://github.com/apache/streampipes/issues/2106)]: Influx Sink can only handle primitive properties


### Breaking Change 💣

* [[#2548](https://github.com/apache/streampipes/pull/2548)]: fix: do not overwrite timestamp in output event of a function
* [[#2241](https://github.com/apache/streampipes/issues/2241)]: Remove activeMQ from installer cli
* [[#2133](https://github.com/apache/streampipes/issues/2133)]: remove Consul and all related assets



### Deprecation ⚠️

* [[#2261](https://github.com/apache/streampipes/pull/2261)]: refactor: remove deprecated method `fromResources`
* [[#2205](https://github.com/apache/streampipes/pull/2205)]: refactor: remove deprecated elements from `NamedStreamPipesEntity`
* [[#2156](https://github.com/apache/streampipes/pull/2156)]: refactor: remove deprecated methods from parameter extractor


### Documentation & Website 📚

* [[#2882](https://github.com/apache/streampipes/pull/2882)]: docs: document machine data simulator properly
* [[#2851](https://github.com/apache/streampipes/pull/2851)]: fix: Fix client python docs site build warning
* [[#2849](https://github.com/apache/streampipes/pull/2849)]: fix: Fix streampipes client python docs config
* [[#2837](https://github.com/apache/streampipes/pull/2837)]: docs: fix OPC-UA sink documentation
* [[#2687](https://github.com/apache/streampipes/pull/2687)]: docs: add python tutorial about ML with ONNX
* [[#2511](https://github.com/apache/streampipes/pull/2511)]: feat: Create adapter for Open Industry 4.0 devices
* [[#2343](https://github.com/apache/streampipes/pull/2343)]: Update HTTP Server adapter documentation.md
* [[#2311](https://github.com/apache/streampipes/pull/2311)]: Extend MS Teams sink by a silent period
* [[#2248](https://github.com/apache/streampipes/pull/2248)]: introduce sink for MS Teams
* [[#2239](https://github.com/apache/streampipes/issues/2239)]: Remove consul references from the website and documentation
* [[#733](https://github.com/apache/streampipes/issues/733)]: Restructure documentantion
* [[#376](https://github.com/apache/streampipes/issues/376)]: Add adapter documentation


### Dependency Updates 📦

* [[#2733](https://github.com/apache/streampipes/pull/2733)]: chore(deps-dev): bump black from 24.3.0 to 24.4.0 in /streampipes-client-python
* [[#2732](https://github.com/apache/streampipes/pull/2732)]: chore(deps): bump org.apache.pulsar:pulsar-client from 3.1.1 to 3.2.2
* [[#2731](https://github.com/apache/streampipes/pull/2731)]: chore(deps): bump konva from 9.2.0 to 9.3.6 in /ui
* [[#2726](https://github.com/apache/streampipes/pull/2726)]: chore(deps): bump pydantic from 2.6.4 to 2.7.0 in /streampipes-client-python
* [[#2716](https://github.com/apache/streampipes/pull/2716)]: chore(deps): bump org.apache.maven.plugins:maven-plugin-plugin from 3.11.0 to 3.12.0
* [[#2710](https://github.com/apache/streampipes/pull/2710)]: chore(deps): bump org.apache.maven.plugin-tools:maven-plugin-annotations from 3.11.0 to 3.12.0
* [[#2691](https://github.com/apache/streampipes/pull/2691)]: feat: Support custom service tags and service selection for adapters
* [[#2656](https://github.com/apache/streampipes/pull/2656)]: chore(deps-dev): bump @typescript-eslint/eslint-plugin from 5.57.1 to 5.62.0 in /ui
* [[#2655](https://github.com/apache/streampipes/pull/2655)]: chore(deps): bump org.apache.inlong:tubemq-client from 1.10.0 to 1.11.0
* [[#2643](https://github.com/apache/streampipes/pull/2643)]: chore(deps): bump jakarta.activation:jakarta.activation-api from 2.0.1 to 2.1.3
* [[#2642](https://github.com/apache/streampipes/pull/2642)]: chore(deps): bump org.mockito:mockito-core from 5.10.0 to 5.11.0
* [[#2640](https://github.com/apache/streampipes/pull/2640)]: chore(deps-dev): bump express from 4.18.2 to 4.19.2 in /ui
* [[#2636](https://github.com/apache/streampipes/pull/2636)]: chore(deps): bump katex from 0.16.9 to 0.16.10 in /ui
* [[#2621](https://github.com/apache/streampipes/pull/2621)]: chore(deps): bump org.simplejavamail:simple-java-mail from 8.5.1 to 8.8.0
* [[#2620](https://github.com/apache/streampipes/pull/2620)]: chore(deps): bump @swimlane/ngx-charts from 20.4.1 to 20.5.0 in /ui
* [[#2611](https://github.com/apache/streampipes/pull/2611)]: chore(deps-dev): bump webpack-dev-middleware from 5.3.3 to 5.3.4 in /ui
* [[#2564](https://github.com/apache/streampipes/pull/2564)]: chore(deps): bump follow-redirects from 1.15.4 to 1.15.6 in /ui
* [[#2563](https://github.com/apache/streampipes/pull/2563)]: chore(deps): bump com.google.guava:guava from 33.0.0-jre to 33.1.0-jre
* [[#2555](https://github.com/apache/streampipes/pull/2555)]: chore(deps): bump org.codehaus.plexus:plexus-component-annotations from 2.1.1 to 2.2.0
* [[#2543](https://github.com/apache/streampipes/pull/2543)]: chore(deps-dev): bump @types/showdown from 1.9.4 to 2.0.6 in /ui
* [[#2533](https://github.com/apache/streampipes/pull/2533)]: chore(deps): bump org.codehaus.mojo:extra-enforcer-rules from 1.7.0 to 1.8.0
* [[#2500](https://github.com/apache/streampipes/pull/2500)]: chore(deps-dev): bump ruff from 0.1.0 to 0.3.0 in /streampipes-client-python
* [[#2482](https://github.com/apache/streampipes/pull/2482)]: chore(deps): bump org.springframework.security:spring-security-core from 6.2.0 to 6.2.2
* [[#2471](https://github.com/apache/streampipes/pull/2471)]: chore(deps): bump undici from 5.27.2 to 5.28.3 in /ui
* [[#2359](https://github.com/apache/streampipes/pull/2359)]: chore(deps-dev): bump lint-staged from 15.1.0 to 15.2.0 in /ui
* [[#2352](https://github.com/apache/streampipes/pull/2352)]: chore(deps): bump org.apache.opennlp:opennlp-tools from 2.1.0 to 2.3.1
* [[#2348](https://github.com/apache/streampipes/pull/2348)]: chore(deps): bump org.apache.commons:commons-pool2 from 2.11.1 to 2.12.0
* [[#2344](https://github.com/apache/streampipes/pull/2344)]: chore(deps): bump org.influxdb:influxdb-java from 2.23 to 2.24
* * [[#2341](https://github.com/apache/streampipes/pull/2341)]: chore(deps-dev): bump @types/node from 20.9.4 to 20.10.4 in /ui
* [[#2340](https://github.com/apache/streampipes/pull/2340)]: chore(deps): bump org.apache.maven.plugins:maven-invoker-plugin from 3.4.0 to 3.6.0
* [[#2332](https://github.com/apache/streampipes/pull/2332)]: chore(deps): bump org.apache.maven.plugins:maven-checkstyle-plugin from 3.2.1 to 3.3.1
* [[#2319](https://github.com/apache/streampipes/pull/2319)]: chore(deps-dev): bump black from 23.11.0 to 23.12.0 in /streampipes-client-python
* [[#2308](https://github.com/apache/streampipes/pull/2308)]: chore(deps): bump org.apache.commons:commons-text from 1.10.0 to 1.11.0
* [[#2303](https://github.com/apache/streampipes/pull/2303)]: chore(deps): bump org.apache.maven.plugins:maven-plugin-plugin from 3.8.1 to 3.10.2
* [[#2288](https://github.com/apache/streampipes/pull/2288)]: chore(deps): bump org.apache.maven.plugins:maven-clean-plugin from 3.2.0 to 3.3.2
* [[#2287](https://github.com/apache/streampipes/pull/2287)]: chore(deps): bump commons-codec:commons-codec from 1.15 to 1.16.0
* [[#2274](https://github.com/apache/streampipes/pull/2274)]: chore(deps): bump org.checkerframework:checker-qual from 3.39.0 to 3.41.0
* [[#2273](https://github.com/apache/streampipes/pull/2273)]: chore(deps): bump actions/labeler from 4 to 5
* * [[#2265](https://github.com/apache/streampipes/pull/2265)]: chore(deps): bump org.mockito:mockito-core from 5.7.0 to 5.8.0
* [[#2258](https://github.com/apache/streampipes/pull/2258)]: chore(deps-dev): bump eslint from 8.53.0 to 8.54.0 in /ui
* [[#2255](https://github.com/apache/streampipes/pull/2255)]: deps: update commons-compress due to CVE
* [[#2248](https://github.com/apache/streampipes/pull/2248)]: introduce sink for MS Teams
* [[#2236](https://github.com/apache/streampipes/pull/2236)]: chore(deps-dev): bump cryptography from 41.0.4 to 41.0.6 in /streampipes-client-python
* [[#2220](https://github.com/apache/streampipes/pull/2220)]: chore(deps): bump com.opencsv:opencsv from 5.8 to 5.9
* [[#2218](https://github.com/apache/streampipes/pull/2218)]: chore(deps-dev): bump @types/node from 20.8.10 to 20.9.4 in /ui
* [[#2213](https://github.com/apache/streampipes/pull/2213)]: chore(deps): bump org.postgresql:postgresql from 42.6.0 to 42.7.0
* [[#2211](https://github.com/apache/streampipes/pull/2211)]: chore(deps-dev): bump prettier from 3.0.3 to 3.1.0 in /ui
* [[#2194](https://github.com/apache/streampipes/pull/2194)]: chore(deps): bump swagger-ui from 5.9.1 to 5.10.0 in /ui
* [[#2187](https://github.com/apache/streampipes/pull/2187)]: chore(deps): bump com.fasterxml.jackson.core:jackson-databind from 2.15.0 to 2.16.0
* [[#2186](https://github.com/apache/streampipes/pull/2186)]: chore(deps): bump jackson.version from 2.15.0 to 2.16.0
* [[#2171](https://github.com/apache/streampipes/pull/2171)]: chore(deps-dev): bump mypy from 1.6.0 to 1.7.0 in /streampipes-client-python
* [[#2164](https://github.com/apache/streampipes/pull/2164)]: chore(deps): remove org.eclipse.jetty:jetty-server
* [[#2159](https://github.com/apache/streampipes/pull/2159)]: chore(deps-dev): bump black from 23.10.0 to 23.11.0 in /streampipes-client-python
* [[#2157](https://github.com/apache/streampipes/issues/2157)]: Remove outdated dependencies from dependency management
* [[#2150](https://github.com/apache/streampipes/pull/2150)]: chore(deps): bump swagger-ui from 4.18.0 to 5.9.1 in /ui
* [[#2147](https://github.com/apache/streampipes/pull/2147)]: chore(deps-dev): bump @types/node from 20.4.6 to 20.8.10 in /ui
* [[#2145](https://github.com/apache/streampipes/pull/2145)]: chore: Maven housekeeping


# [0.93.0]

## What's Changed

### Enhancement 🌟

* [[#2092](https://github.com/apache/streampipes/issues/2092)]: Remove magic HTTP numbers in StreamPipes
* [[#2056](https://github.com/apache/streampipes/issues/2056)]: Make email templates configurable
* [[#2032](https://github.com/apache/streampipes/issues/2032)]: Provide endpoint to get measurement counts from core
* [[#2031](https://github.com/apache/streampipes/pull/2031)]: style: Add last message info to adapter overview
* [[#1992](https://github.com/apache/streampipes/issues/1992)]: Migration of Kafka source configuration.
* [[#1980](https://github.com/apache/streampipes/issues/1980)]: Arrays are not supported for S7 PLCs
* [[#1906](https://github.com/apache/streampipes/issues/1906)]: Revive streampipes-maven-plugin to auto-generate
  pipeline element documentation
* [[#1875](https://github.com/apache/streampipes/issues/1875)]: Connect: Order measurement units by name
* [[#1814](https://github.com/apache/streampipes/issues/1814)]: Integrate extensions service discovery & configuration
  management into core
* [[#1716](https://github.com/apache/streampipes/pull/1716)]: Enable creating CouchDB attachments for images
* [[#1688](https://github.com/apache/streampipes/issues/1688)]: New Processor: Round Numeric Values
* [[#1662](https://github.com/apache/streampipes/issues/1662)]: Support asynchronous browsing in OPC-UA adapter
* [[#1592](https://github.com/apache/streampipes/issues/1592)]: Connect IO-Link Sensor Data into StreamPipes
* [[#1374](https://github.com/apache/streampipes/issues/1374)]: Convenient `columns` query parameter for data lake
  measure
* [[#1103](https://github.com/apache/streampipes/issues/1103)]: Support Python 3.11 in python client

### Bug fixes 🧰
* [[#2191](https://github.com/apache/streampipes/pull/2191)]: fix: tooltip in asset overview
* [[#2146](https://github.com/apache/streampipes/pull/2146)]: fix(#2002) Retry service registration in case services are
  removed be…
* [[#2166](https://github.com/apache/streampipes/issues/2166)]: Protected names are not sanitized correctly in Data Lake
  Sink / Influx sink
* [[#2165](https://github.com/apache/streampipes/issues/2165)]: Update `0.92.0` -> `0.93.0` of `Machine Data Simulator`
  not working
* [[#2112](https://github.com/apache/streampipes/issues/2112)]: Changes on messaging layer configuration on UI not
  persisted.
* [[#2044](https://github.com/apache/streampipes/issues/2044)]: Docker compose build error
* [[#2024](https://github.com/apache/streampipes/pull/2024)]: fix: make data retrieval of IOLink sensor more robust
* [[#1992](https://github.com/apache/streampipes/issues/1992)]: Migration of Kafka source configuration.
* [[#1983](https://github.com/apache/streampipes/issues/1983)]: Logo image broken in Footer
* [[#1956](https://github.com/apache/streampipes/issues/1956)]: NPE in ConsulConfigMigration
* [[#1938](https://github.com/apache/streampipes/issues/1938)]: Datetime selector in Data Explorer has issues with 12 am
* [[#1934](https://github.com/apache/streampipes/issues/1934)]: Improve adapter started dialog in StreamPipes connect
* [[#1876](https://github.com/apache/streampipes/issues/1876)]: Connect: Form validation in schema editor
* [[#1834](https://github.com/apache/streampipes/pull/1834)]: [hotfix] Fix MDC layout issue in permission dialog
* [[#1829](https://github.com/apache/streampipes/pull/1829)]: [hotfix] Fix layout issues and validation in data explorer
* [[#1794](https://github.com/apache/streampipes/issues/1794)]: Aggregation field in data explorer widget is broken
* [[#1770](https://github.com/apache/streampipes/issues/1770)]: Wrong base image in Maven archetypes
* [[#1769](https://github.com/apache/streampipes/issues/1769)]: The dashboard fails to load the element whose name
  contiains '/'
* [[#1741](https://github.com/apache/streampipes/issues/1741)]: The status light widget in the live dashboard is broken
* [[#1713](https://github.com/apache/streampipes/issues/1713)]: OPC UA NullPointer Exception when Node Description is
  Missing
* [[#1642](https://github.com/apache/streampipes/issues/1642)]: Data Lake default export period does not work
* [[#1637](https://github.com/apache/streampipes/issues/1637)]: Schema guessing from file is currently not implemented
  in HTTP Server source
* [[#1629](https://github.com/apache/streampipes/pull/1629)]: fix: kafka consumer data loss promble
* [[#1597](https://github.com/apache/streampipes/issues/1597)]: apachestreampipes/sources-vehicle-simulator:
  0.92.0-SNAPSHOT not found
* [[#1546](https://github.com/apache/streampipes/issues/1546)]: Failed to upgrade the helm chart
* [[#1533](https://github.com/apache/streampipes/issues/1533)]: Notification counter is not reset
* [[#1481](https://github.com/apache/streampipes/issues/1481)]: URL Dereferencing Processor NotSerializableException

### Breaking Change 💣

* [[#2143](https://github.com/apache/streampipes/pull/2143)]: refactor(#2128): deprecate legacy adapters
* [[#2088](https://github.com/apache/streampipes/issues/2088)]: Remove module `streampipes-logging`
* [[#2066](https://github.com/apache/streampipes/pull/2066)]: refactor: remove legacy demo resources
* [[#1912](https://github.com/apache/streampipes/pull/1912)]: Remove python wrapper
* [[#1583](https://github.com/apache/streampipes/issues/1583)]: Remove CumSum Pipeline Element
* [[#1289](https://github.com/apache/streampipes/issues/1289)]: Harmonize data set and data stream API

### Deprecation ⚠️

* [[#2143](https://github.com/apache/streampipes/pull/2143)]: refactor(#2128): deprecate legacy adapters
* [[#1640](https://github.com/apache/streampipes/pull/1640)]: feature: retrieve credentials from SP environment
  variables

### Documentation & Website 📚

* [[#2143](https://github.com/apache/streampipes/pull/2143)]: refactor(#2128): deprecate legacy adapters
* [[#2138](https://github.com/apache/streampipes/pull/2138)]: Use os.environ dictionary to set environment variables.
* [[#2069](https://github.com/apache/streampipes/pull/2069)]: refactor: Add individual connector modules for adapters
  and sinks
* [[#2067](https://github.com/apache/streampipes/issues/2067)]: Check references for watertank simulator and vehicle
  simulator on the website
* [[#2066](https://github.com/apache/streampipes/pull/2066)]: refactor: remove legacy demo resources
* [[#1983](https://github.com/apache/streampipes/issues/1983)]: Logo image broken in Footer
* [[#1978](https://github.com/apache/streampipes/pull/1978)]: Add deployment of Prometheus and Grafana to K8s
* [[#1955](https://github.com/apache/streampipes/pull/1955)]: Introduce Quickstart deployment mode
* [[#1945](https://github.com/apache/streampipes/pull/1945)]: Add configuration hint for Kafka users.
* [[#1912](https://github.com/apache/streampipes/pull/1912)]: Remove python wrapper
* [[#1906](https://github.com/apache/streampipes/issues/1906)]: Revive streampipes-maven-plugin to auto-generate
  pipeline element documentation
* [[#1844](https://github.com/apache/streampipes/pull/1844)]: chore: add Poetry badge to our README
* [[#1820](https://github.com/apache/streampipes/pull/1820)]: Add Pulsar's Messaging Layer to a Helm Deployment
* [[#1817](https://github.com/apache/streampipes/pull/1817)]: chore: introduce poetry as dependency management tool
* [[#1733](https://github.com/apache/streampipes/pull/1733)]: feature: introduce admonitions to warn about dependency
  issue in docs
* [[#1694](https://github.com/apache/streampipes/pull/1694)]: chore: improve metadata for repository
* [[#1640](https://github.com/apache/streampipes/pull/1640)]: feature: retrieve credentials from SP environment
  variables

### Dependency Updates 📦

* [[#2177](https://github.com/apache/streampipes/pull/2177)]: deps: update Active MQ due to CVE
* [[#2140](https://github.com/apache/streampipes/pull/2140)]: chore(deps-dev): bump eslint from 8.37.0 to 8.53.0 in /ui
* [[#2127](https://github.com/apache/streampipes/pull/2127)]: chore(deps-dev): bump
  @angular-eslint/eslint-plugin-template from 15.2.1 to 16.2.0 in /ui
* [[#2126](https://github.com/apache/streampipes/pull/2126)]: chore(deps): bump org.mockito:mockito-core from 5.6.0 to
  5.7.0
* [[#2125](https://github.com/apache/streampipes/pull/2125)]: chore(deps): remove org.wildfly.common:wildfly-common
* [[#2119](https://github.com/apache/streampipes/pull/2119)]: chore(deps): bump com.google.protobuf:protobuf-java from
  3.24.0 to 3.25.0
* [[#2113](https://github.com/apache/streampipes/pull/2113)]: chore(deps-dev): bump webpack from 5.88.2 to 5.89.0 in /ui
* [[#2091](https://github.com/apache/streampipes/pull/2091)]: chore(deps-dev): bump browserify-sign from 4.2.1 to 4.2.2
  in /ui
* [[#2087](https://github.com/apache/streampipes/pull/2087)]: chore(deps): bump com.rabbitmq:amqp-client from 5.19.0 to
  5.20.0
* [[#2085](https://github.com/apache/streampipes/pull/2085)]: chore(deps-dev): bump jasmine-core from 4.6.0 to 5.1.1 in
  /ui
* [[#2069](https://github.com/apache/streampipes/pull/2069)]: refactor: Add individual connector modules for adapters
  and sinks
* [[#2066](https://github.com/apache/streampipes/pull/2066)]: refactor: remove legacy demo resources
* [[#2048](https://github.com/apache/streampipes/pull/2048)]: chore(deps-dev): bump assert from 2.0.0 to 2.1.0 in /ui
* [[#2038](https://github.com/apache/streampipes/pull/2038)]: chore(deps-dev): bump @babel/traverse from 7.22.5 to
  7.23.2 in /ui
* [[#2036](https://github.com/apache/streampipes/pull/2036)]: chore(deps): bump plotly.js from 2.22.0 to 2.26.2 in /ui
* [[#2035](https://github.com/apache/streampipes/pull/2035)]: chore(deps): bump org.apache.inlong:tubemq-client from
  1.7.0 to 1.9.0
* [[#2027](https://github.com/apache/streampipes/pull/2027)]: chore(deps-dev): bump @types/jasmine from 4.3.1 to 5.1.0
  in /ui
* [[#2019](https://github.com/apache/streampipes/pull/2019)]: chore(deps): bump shepherd.js from 11.1.1 to 11.2.0 in /ui
* [[#2015](https://github.com/apache/streampipes/pull/2015)]: chore(deps): remove org.immutables
* [[#2011](https://github.com/apache/streampipes/pull/2011)]: chore(deps): bump org.simplejavamail:simple-java-mail from
  8.2.0 to 8.3.1
* [[#2009](https://github.com/apache/streampipes/pull/2009)]: chore(deps-dev): bump webpack from 5.76.1 to 5.88.2 in /ui
* [[#1999](https://github.com/apache/streampipes/pull/1999)]: chore(deps): bump io.nats:jnats from 2.16.1 to 2.17.0
* [[#1996](https://github.com/apache/streampipes/pull/1996)]: chore(deps): bump org.checkerframework:checker-qual from
  3.38.0 to 3.39.0
* [[#1988](https://github.com/apache/streampipes/pull/1988)]: chore(deps): bump org.simplejavamail:simple-java-mail from
  8.1.3 to 8.2.0
* [[#1984](https://github.com/apache/streampipes/pull/1984)]: chore(deps): bump org.yaml:snakeyaml from 2.1 to 2.2
* [[#1977](https://github.com/apache/streampipes/pull/1977)]: chore(deps): bump com.rabbitmq:amqp-client from 5.18.0 to
  5.19.0
* [[#1972](https://github.com/apache/streampipes/pull/1972)]: chore(deps-dev): bump org.testcontainers:testcontainers
  from 1.18.3 to 1.19.0
* [[#1970](https://github.com/apache/streampipes/pull/1970)]: Bump org.mockito:mockito-core from 5.4.0 to 5.5.0
* [[#1964](https://github.com/apache/streampipes/pull/1964)]: Bump org.xerial.snappy:snappy-java from 1.1.10.1 to
  1.1.10.4
* [[#1963](https://github.com/apache/streampipes/pull/1963)]: Bump tslib from 2.5.0 to 2.6.2 in /ui
* [[#1962](https://github.com/apache/streampipes/pull/1962)]: Bump com.google.guava:guava from 32.0.1-jre to 32.1.2-jre
* [[#1949](https://github.com/apache/streampipes/pull/1949)]: Bump com.nimbusds:nimbus-jose-jwt from 9.31 to 9.35
* [[#1946](https://github.com/apache/streampipes/pull/1946)]: Bump typing-extensions from 4.5.0 to 4.8.0 in
  /streampipes-client-python
* [[#1942](https://github.com/apache/streampipes/pull/1942)]: Bump org.boofcv:boofcv-core from 0.44 to 1.1.0
* [[#1939](https://github.com/apache/streampipes/pull/1939)]: refactor: replace random password generation logic
* [[#1931](https://github.com/apache/streampipes/pull/1931)]: Bump org.eclipse.jetty:jetty-http from 10.0.14 to 10.0.16
* [[#1930](https://github.com/apache/streampipes/pull/1930)]: Bump org.eclipse.jetty:jetty-servlets from 10.0.14 to
  10.0.16
* [[#1919](https://github.com/apache/streampipes/pull/1919)]: Bump karma-jasmine-html-reporter from 2.0.0 to 2.1.0 in
  /ui
* [[#1916](https://github.com/apache/streampipes/pull/1916)]: Bump net.minidev:json-smart from 2.4.9 to 2.5.0
* [[#1912](https://github.com/apache/streampipes/pull/1912)]: Remove python wrapper
* [[#1881](https://github.com/apache/streampipes/pull/1881)]: Bump cz.habarta.typescript-generator:
  typescript-generator-maven-plugin from 3.1.1185 to 3.2.1263
* [[#1861](https://github.com/apache/streampipes/pull/1861)]: Bump roaster.version from 2.28.0.Final to 2.29.0.Final
* [[#1860](https://github.com/apache/streampipes/pull/1860)]: Bump @ctrl/ngx-codemirror from 5.1.1 to 6.1.0 in /ui
* [[#1850](https://github.com/apache/streampipes/pull/1850)]: Bump org.antlr:antlr4-runtime from 4.11.1 to 4.13.0
* [[#1848](https://github.com/apache/streampipes/pull/1848)]: Bump @angular-eslint/builder from 15.2.1 to 16.1.1 in /ui
* [[#1837](https://github.com/apache/streampipes/pull/1837)]: Bump redis.clients:jedis from 4.3.1 to 4.4.3
* [[#1836](https://github.com/apache/streampipes/pull/1836)]: Bump lint-staged from 13.2.0 to 14.0.0 in /ui
* [[#1831](https://github.com/apache/streampipes/pull/1831)]: Bump blacken-docs from 1.15.0 to 1.16.0 in
  /streampipes-client-python
* [[#1830](https://github.com/apache/streampipes/pull/1830)]: Bump org.jetbrains.kotlin:kotlin-stdlib from 1.8.0 to
  1.9.0
* [[#1825](https://github.com/apache/streampipes/pull/1825)]: Bump com.google.protobuf:protobuf-java from 3.21.12 to
  3.24.0
* [[#1821](https://github.com/apache/streampipes/pull/1821)]: Bump org.boofcv:boofcv-core from 0.43.1 to 0.44
* [[#1817](https://github.com/apache/streampipes/pull/1817)]: chore: introduce poetry as dependency management tool
* [[#1816](https://github.com/apache/streampipes/pull/1816)]: Bump eslint-config-prettier from 8.8.0 to 9.0.0 in /ui
* [[#1812](https://github.com/apache/streampipes/pull/1812)]: Bump konva from 8.4.0 to 9.2.0 in /ui
* [[#1810](https://github.com/apache/streampipes/pull/1810)]: Bump @types/node from 18.14.0 to 20.4.6 in /ui
* [[#1805](https://github.com/apache/streampipes/pull/1805)]: Bump pyupgrade from 3.9.0 to 3.10.1 in
  /streampipes-client-python
* [[#1804](https://github.com/apache/streampipes/pull/1804)]: Bump flake8 from 6.0.0 to 6.1.0 in
  /streampipes-client-python
* [[#1802](https://github.com/apache/streampipes/pull/1802)]: Bump org.boofcv:boofcv-core from 0.42 to 0.43.1
* [[#1801](https://github.com/apache/streampipes/pull/1801)]: Bump mkdocs from 1.4.2 to 1.5.1 in
  /streampipes-client-python
* [[#1790](https://github.com/apache/streampipes/pull/1790)]: Bump @jsplumb/browser-ui from 6.1.1 to 6.2.10 in /ui
* [[#1789](https://github.com/apache/streampipes/pull/1789)]: Bump com.opencsv:opencsv from 5.7.1 to 5.8
* [[#1784](https://github.com/apache/streampipes/pull/1784)]: Bump @typescript-eslint/parser from 5.59.11 to 5.62.0 in
  /ui
* [[#1780](https://github.com/apache/streampipes/pull/1780)]: Bump word-wrap from 1.2.3 to 1.2.4 in /ui
* [[#1767](https://github.com/apache/streampipes/pull/1767)]: remove dependency scala-xml_2.11
* [[#1766](https://github.com/apache/streampipes/pull/1766)]: Bump semver from 5.7.1 to 5.7.2 in /ui
* [[#1765](https://github.com/apache/streampipes/pull/1765)]: Bump checkstyle from 10.6.0 to 10.12.1
* [[#1764](https://github.com/apache/streampipes/pull/1764)]: Bump black from 23.3.0 to 23.7.0 in
  /streampipes-client-python
* [[#1763](https://github.com/apache/streampipes/pull/1763)]: Bump pyupgrade from 3.8.0 to 3.9.0 in
  /streampipes-client-python
* [[#1761](https://github.com/apache/streampipes/pull/1761)]: Bump cypress from 12.8.1 to 12.17.0 in /ui
* [[#1759](https://github.com/apache/streampipes/pull/1759)]: Bump amqp-client from 5.17.0 to 5.18.0
* [[#1749](https://github.com/apache/streampipes/pull/1749)]: Bump blacken-docs from 1.14.0 to 1.15.0 in
  /streampipes-client-python
* [[#1748](https://github.com/apache/streampipes/pull/1748)]: Bump extra-enforcer-rules from 1.6.1 to 1.7.0
* [[#1746](https://github.com/apache/streampipes/pull/1746)]: Bump jquery from 3.6.3 to 3.7.0 in /ui
* [[#1739](https://github.com/apache/streampipes/pull/1739)]: Bump graalvm.js.version from 22.3.1 to 23.0.0
* [[#1735](https://github.com/apache/streampipes/pull/1735)]: Bump jakarta.activation-api from 1.2.2 to 2.1.2
* [[#1734](https://github.com/apache/streampipes/pull/1734)]: Bump shepherd.js from 11.0.1 to 11.1.1 in /ui
* [[#1733](https://github.com/apache/streampipes/pull/1733)]: feature: introduce admonitions to warn about dependency
  issue in docs
* [[#1730](https://github.com/apache/streampipes/pull/1730)]: Bump javassist from 3.25.0-GA to 3.29.2-GA
* [[#1728](https://github.com/apache/streampipes/pull/1728)]: Bump pyupgrade from 3.7.0 to 3.8.0 in
  /streampipes-client-python
* [[#1723](https://github.com/apache/streampipes/pull/1723)]: Bump jboss-logging from 3.4.0.Final to 3.5.2.Final
* [[#1721](https://github.com/apache/streampipes/pull/1721)]: Bump tubemq-client from 1.6.0 to 1.7.0
* [[#1715](https://github.com/apache/streampipes/pull/1715)]: Bump okio from 1.16.0 to 3.3.0
* [[#1712](https://github.com/apache/streampipes/pull/1712)]: Bump autoflake from 2.1.0 to 2.2.0 in
  /streampipes-client-python
* [[#1711](https://github.com/apache/streampipes/pull/1711)]: Bump pytest from 7.3.0 to 7.4.0 in
  /streampipes-client-python
* [[#1710](https://github.com/apache/streampipes/pull/1710)]: Bump formatter-maven-plugin from 2.21.0 to 2.23.0
* [[#1707](https://github.com/apache/streampipes/pull/1707)]: Bump mypy from 1.3.0 to 1.4.0 in
  /streampipes-client-python
* [[#1704](https://github.com/apache/streampipes/pull/1704)]: Bump spring-security-core from 6.0.3 to 6.1.1 & spring to
  6.0.10
* [[#1702](https://github.com/apache/streampipes/pull/1702)]: Bump angular-plotly.js from 4.0.4 to 5.0.0 in /ui
* [[#1699](https://github.com/apache/streampipes/pull/1699)]: Bump @typescript-eslint/parser from 5.56.0 to 5.59.11 in
  /ui
* [[#1698](https://github.com/apache/streampipes/pull/1698)]: Bump mockito-core from 5.3.1 to 5.4.0
* [[#1697](https://github.com/apache/streampipes/pull/1697)]: Bump pyupgrade from 3.6.0 to 3.7.0 in
  /streampipes-client-python
* [[#1692](https://github.com/apache/streampipes/pull/1692)]: Bump dependency-check-maven from 6.5.1 to 8.3.1
* [[#1689](https://github.com/apache/streampipes/pull/1689)]: Bump snappy-java from 1.1.7.7 to 1.1.10.1
* [[#1687](https://github.com/apache/streampipes/pull/1687)]: Bump guava from 31.1-jre to 32.0.1-jre
* [[#1686](https://github.com/apache/streampipes/pull/1686)]: Bump @swimlane/ngx-charts from 20.1.2 to 20.4.1 in /ui
* [[#1681](https://github.com/apache/streampipes/pull/1681)]: Bump testcontainers from 1.17.4 to 1.18.3
* [[#1679](https://github.com/apache/streampipes/pull/1679)]: Bump nimbus-jose-jwt from 9.30.1 to 9.31
* [[#1678](https://github.com/apache/streampipes/pull/1678)]: Bump blacken-docs from 1.13.0 to 1.14.0 in
  /streampipes-client-python
* [[#1674](https://github.com/apache/streampipes/pull/1674)]: Bump pyupgrade from 3.4.0 to 3.6.0 in
  /streampipes-client-python
* [[#1640](https://github.com/apache/streampipes/pull/1640)]: feature: retrieve credentials from SP environment
  variables
* [[#1636](https://github.com/apache/streampipes/pull/1636)]: Bump mockito-core from 4.11.0 to 5.3.1
* [[#1631](https://github.com/apache/streampipes/pull/1631)]: Bump mkdocstrings[python] from 0.21.1 to 0.22.0 in
  /streampipes-client-python
* [[#1595](https://github.com/apache/streampipes/pull/1595)]: Bump spring-boot.version from 3.0.6 to 3.1.0
* [[#1591](https://github.com/apache/streampipes/pull/1591)]: Bump pytest-cov from 4.0.0 to 4.1.0 in
  /streampipes-client-python
* [[#1588](https://github.com/apache/streampipes/pull/1588)]: Bump types-requests from 2.30.0.0 to 2.31.0.0 in
  /streampipes-client-python
* [[#1587](https://github.com/apache/streampipes/pull/1587)]: Bump socket.io-parser from 4.2.1 to 4.2.3 in /ui
* [[#1578](https://github.com/apache/streampipes/pull/1578)]: Bump postgresql from 42.4.3 to 42.6.0
* [[#1576](https://github.com/apache/streampipes/pull/1576)]: Support pulsar messasging layer
* [[#1335](https://github.com/apache/streampipes/issues/1335)]: Replace `@angular/flex-layout` dependency
  with `@ngbracket/ngx-layout` dependency

### Uncategorized ❓

* [[#2209](https://github.com/apache/streampipes/pull/2209)]: build: change `outputHashing` in Angular to avoid caching
  issue after new release
* [[#2190](https://github.com/apache/streampipes/pull/2190)]: ui: add link to LinkedIn in Support section
* [[#2135](https://github.com/apache/streampipes/issues/2135)]: Max health check intervals configurable
* [[#2130](https://github.com/apache/streampipes/issues/2130)]: Add E2E-Test for pipeline export and import
* [[#2129](https://github.com/apache/streampipes/pull/2129)]: feat: Use alpine-based Docker image for UI
* [[#2122](https://github.com/apache/streampipes/issues/2122)]: Cleanup extension bundles
* [[#2104](https://github.com/apache/streampipes/issues/2104)]: Improve lifecycle for managing core and extension
  initialization actions
* [[#2098](https://github.com/apache/streampipes/issues/2098)]: Implement first migration for S7 adapter
* [[#2076](https://github.com/apache/streampipes/pull/2076)]: improve archetypes for adapter tutorial
* [[#2071](https://github.com/apache/streampipes/pull/2071)]: refactor: minor adaption & improvement
* [[#2068](https://github.com/apache/streampipes/pull/2068)]: refactor: Make interactive tutorial work again
* [[#2064](https://github.com/apache/streampipes/pull/2064)]: refactor: introduce convenience method for service url
* [[#2061](https://github.com/apache/streampipes/issues/2061)]: Create zip file during build phase with installer files
  only
* [[#2041](https://github.com/apache/streampipes/pull/2041)]: refactor: remove references and artifacts for data sets
* [[#2018](https://github.com/apache/streampipes/pull/2018)]: test(#2017): Add cypress test for configuration
* [[#2017](https://github.com/apache/streampipes/issues/2017)]: Add more e2e tests to configuration view
* [[#2002](https://github.com/apache/streampipes/issues/2002)]: Harmonize registration of adapters and pipeline elements
* [[#1926](https://github.com/apache/streampipes/issues/1926)]: Improve handling of secrets in K8s
* [[#1852](https://github.com/apache/streampipes/pull/1852)]: Remove sources-vehicle-simulator from cli-installer full
  env.
* [[#1843](https://github.com/apache/streampipes/pull/1843)]: chore: refine dependency constraints
* [[#1787](https://github.com/apache/streampipes/issues/1787)]: Improve logging of extensions services
* [[#1786](https://github.com/apache/streampipes/pull/1786)]: add probes to Streampipes' kubernetes deployment ( #1781 )
* [[#1777](https://github.com/apache/streampipes/issues/1777)]: Add API endpoint to get available users
* [[#1771](https://github.com/apache/streampipes/issues/1771)]: Remove dependencies to specific protocols from the
  StreamPipes core service
* [[#1726](https://github.com/apache/streampipes/issues/1726)]: Update Maven archetypes
* [[#1717](https://github.com/apache/streampipes/issues/1717)]: Support other protocols besides Kafka in Streampipes
  Client for gathering live data
* [[#1683](https://github.com/apache/streampipes/pull/1683)]: Support migration of adapters in data import
* [[#1682](https://github.com/apache/streampipes/pull/1682)]: Harmonize OPC-UA adapter and sink, add timestamp to
  metadata (#899)
* [[#1676](https://github.com/apache/streampipes/issues/1676)]: About Kafka consumer data loss problem
* [[#1673](https://github.com/apache/streampipes/pull/1673)]: Make ChangedValueDetectionProcessor dimension sensitive
* [[#1664](https://github.com/apache/streampipes/issues/1664)]: Unify the labels for OPC UA adapter & sink
* [[#1660](https://github.com/apache/streampipes/pull/1660)]: Improve CSS assets to ease configuration of custom layouts
* [[#1651](https://github.com/apache/streampipes/issues/1651)]: Integrate all experimental Flink pipeline elements into
  a single module
* [[#1648](https://github.com/apache/streampipes/issues/1648)]: Move OPC-UA processor and sink into a single module
* [[#1632](https://github.com/apache/streampipes/issues/1632)]: Cleanup API to define data processors and sinks
* [[#1628](https://github.com/apache/streampipes/pull/1628)]: chore: add missing support of NATS as messaging protocol
* [[#1616](https://github.com/apache/streampipes/issues/1616)]: Modify .asf.yaml to better organize Github discussions
  on mailing list
* [[#1590](https://github.com/apache/streampipes/issues/1590)]: Rename the interface `AdapterInterface` to `IAdapter` in
  the `remove-set-adapter` branch
* [[#1589](https://github.com/apache/streampipes/pull/1589)]: add sample configuration of pulsar subscription-name
* [[#1581](https://github.com/apache/streampipes/issues/1581)]: HTTP Stream Adapter Stops Emitting Events When Running
  Multiple Instances
* [[#1580](https://github.com/apache/streampipes/issues/1580)]:  Include Set Adapters in CouchDB Backup During Migration
  Script
* [[#1260](https://github.com/apache/streampipes/issues/1260)]: StreamPipes functions Python: `required_streams `
  vs `consumed_streams `

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
