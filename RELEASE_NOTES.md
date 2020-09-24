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


## [Unreleased]
### New Features

### Improvements

### Bug Fixes

## [0.67.0]
This list only incldues changes to pipeline elements provided in the repository. The full release notes are available in our Jira:
[Release Notes](https://issues.apache.org/jira/browse/STREAMPIPES-154?jql=project%20%3D%20STREAMPIPES%20AND%20fixVersion%20%3D%200.67.0%20ORDER%20BY%20priority%20DESC%2C%20updated%20DESC)

## Bug
* [STREAMPIPES-143](https://issues.apache.org/jira/browse/STREAMPIPES-143) - Numerical Filter in Siddhi not working
* [STREAMPIPES-153](https://issues.apache.org/jira/browse/STREAMPIPES-153) - DateTime is not supported in MySQL adapter
* [STREAMPIPES-155](https://issues.apache.org/jira/browse/STREAMPIPES-155) - SplitArray controller produces wrong output schema
* [STREAMPIPES-165](https://issues.apache.org/jira/browse/STREAMPIPES-165) - S7 adpater does not work
* [STREAMPIPES-170](https://issues.apache.org/jira/browse/STREAMPIPES-170) - NullPointer in JS Evaluator if no event is returned
* [STREAMPIPES-214](https://issues.apache.org/jira/browse/STREAMPIPES-214) - CSV Metadata Enricher does not appear in lite version
* [STREAMPIPES-227](https://issues.apache.org/jira/browse/STREAMPIPES-227) - Missing label in Boolean Counter configuration
* [STREAMPIPES-240](https://issues.apache.org/jira/browse/STREAMPIPES-240) - NullPointer Exception in processor image enricher
* [STREAMPIPES-241](https://issues.apache.org/jira/browse/STREAMPIPES-241) - CustomTransformOutputStrategy of Processor SplitArray not working
* [STREAMPIPES-244](https://issues.apache.org/jira/browse/STREAMPIPES-244) - JS Evalutor is not working in docker

## New Feature
* [STREAMPIPES-114](https://issues.apache.org/jira/browse/STREAMPIPES-114) - New sink to  support writing data to MQTT.
* [STREAMPIPES-115](https://issues.apache.org/jira/browse/STREAMPIPES-115) - New sink to support writing data to MySQL
* [STREAMPIPES-132](https://issues.apache.org/jira/browse/STREAMPIPES-132) - Add data processor to evaluate JavaScript
* [STREAMPIPES-149](https://issues.apache.org/jira/browse/STREAMPIPES-149) - Processor: State Buffer
* [STREAMPIPES-159](https://issues.apache.org/jira/browse/STREAMPIPES-159) - Processor: Detect Signal Edge

## Improvement
* [STREAMPIPES-118](https://issues.apache.org/jira/browse/STREAMPIPES-118) - Add configuration file to S7 adapter
* [STREAMPIPES-128](https://issues.apache.org/jira/browse/STREAMPIPES-128) - Upload Excel Option for PLC4x (S7) Adapter
* [STREAMPIPES-129](https://issues.apache.org/jira/browse/STREAMPIPES-129) - Upload Excel Option for PLC4x (S7) Adapter
* [STREAMPIPES-130](https://issues.apache.org/jira/browse/STREAMPIPES-130) - Image upload adapter
* [STREAMPIPES-131](https://issues.apache.org/jira/browse/STREAMPIPES-131) - Increase Accuracy of the Geo Distance Calculation 
* [STREAMPIPES-141](https://issues.apache.org/jira/browse/STREAMPIPES-141) - Refactor all adapter implementations to use assets
* [STREAMPIPES-146](https://issues.apache.org/jira/browse/STREAMPIPES-146) - Support CustomOutputStrategy in SiddhiEventEngine
* [STREAMPIPES-156](https://issues.apache.org/jira/browse/STREAMPIPES-156) - Processor: State Labeler
* [STREAMPIPES-211](https://issues.apache.org/jira/browse/STREAMPIPES-211) - Add Polling interval for PLC4X S7 adaptrer
* [STREAMPIPES-216](https://issues.apache.org/jira/browse/STREAMPIPES-216) - Move watertank simulator source to StreamPipes Connect
* [STREAMPIPES-225](https://issues.apache.org/jira/browse/STREAMPIPES-225) - Bundle all Flink pipeline elements
* [STREAMPIPES-236](https://issues.apache.org/jira/browse/STREAMPIPES-236) - Fix Telegram sink using html font format option in request


## [0.66.0]
## New Features
* Multiple properties are supported in PLC4X adapter
* New data processor to merge data streams by timestamp
* New data processor to enrich streams
* New data sink for Eclipse Ditto
* Multi-arch docker images
* New adapter for NETIO power sockets
* New data sink for MQTT
* New data processor for numerical and text values

## Improvements
* Improvements to the Kafka Publisher Sink
* Improvements to the Notification Sink

## Bug fixes
* Password field in postgres sink is now marked as password
* Fix a bug in the REST pull adapter
