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

## Slack Notification

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

Slack bot to send notifications directly into your slack

Before you use this sink, the Slack token needs to be configured.
After you've installed the element, navigate to ``Settings``, open the panel ``Sinks Notifications JVM`` and add your
Slack API token.
***

## Required input

This sink does not have any requirements and works with any incoming event type.

***

## Configuration

Describe the configuration parameters here

### Receiver

The receiver of the Slack message.

### Channel Type

The channel type, should be "User" or "Channel"

### Content

The message that should be sent.

### Silent Period

The *Silent Period* is the duration, expressed in minutes, during which notifications are temporarily disabled after one
has been sent. This feature is implemented to prevent overwhelming the target with frequent notifications, avoiding
potential spam behavior.

## Output

(not applicable for data sinks)