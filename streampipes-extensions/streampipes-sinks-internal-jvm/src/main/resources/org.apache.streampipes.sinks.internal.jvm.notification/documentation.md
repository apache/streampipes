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

## Notification

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

Displays a notification in the UI panel of StreamPipes.

***

## Required input

This sink does not have any requirements and works with any incoming event type.

***

## Configuration

### Notification Title

The title of the notification.

### Content

The notification message.

### Silent Period

The *Silent Period* is the duration, expressed in minutes, during which notifications are temporarily disabled after one
has been sent. This feature is implemented to prevent overwhelming the target with frequent notifications, avoiding
potential spam behavior.

## Output

(not applicable for data sinks)