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

## [Unreleased]
### New Features

### Improvements

### Bug Fixes

## [0.67.0]

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