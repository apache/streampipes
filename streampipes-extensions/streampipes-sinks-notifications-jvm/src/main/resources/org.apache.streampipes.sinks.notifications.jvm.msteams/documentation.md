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

# MS Teams Sink

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

---

## Description

The MS Teams Sink is a StreamPipes data sink that facilitates the sending of messages to a Microsoft Teams channel
through a Webhook URL. Whether you need to convey simple text messages or employ more advanced formatting with [Adaptive
Cards](https://adaptivecards.io/), this sink provides a versatile solution for integrating StreamPipes with Microsoft Teams.

---

## Required input

The MS Teams Sink does not have any specific requirements for incoming event types. It is designed to work seamlessly
with any type of incoming event, making it a versatile choice for various use cases.

---

## Configuration

#### Webhook URL

To configure the MS Teams Sink, you need to provide the Webhook URL that enables the sink to send messages to a specific
MS Teams channel. If you don't have a Webhook URL, you can learn how to create
one [here](https://learn.microsoft.com/en-us/microsoftteams/platform/webhooks-and-connectors/how-to/add-incoming-webhook?tabs=dotnet#create-incoming-webhooks-1).

#### Message Content Options

You can choose between two message content formats:

- **Simple Message Content:** Supports plain text and basic markdown formatting.
- **Advanced Message Content:** Expects JSON input directly forwarded to Teams without modification. This format is
  highly customizable and can be used for Adaptive Cards.

Choose the format that best suits your messaging needs.

---

## Usage

#### Simple Message Format

In the simple message format, you can send plain text messages or utilize basic markdown formatting to convey
information. This is ideal for straightforward communication needs.

#### Advanced Message Format

For more sophisticated messaging requirements, the advanced message format allows you to send JSON content directly to
Microsoft Teams without modification. This feature is especially powerful when used
with [Adaptive Cards](https://learn.microsoft.com/en-us/adaptive-cards/), enabling interactive and dynamic content in
your Teams messages.
