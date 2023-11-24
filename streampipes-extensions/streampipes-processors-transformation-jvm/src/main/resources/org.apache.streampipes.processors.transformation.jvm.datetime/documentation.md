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

## Datetime From String

<p align="center">
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Overview

The "Datetime From String" processor is a handy tool that helps convert human-readable datetime information into a
format that machines can understand. This is particularly useful when dealing with data that includes dates and times.

### Why Use This Processor?

In the context of event streams, you may encounter dates and times formatted for human readability but not necessarily
optimized for computer processing. The "Datetime From String" processor addresses this by facilitating the conversion
of human-readable datetime information within your continuous stream of events.

***

## How It Works

When you input a data stream into this processor containing a datetime in a specific format (such as "2023-11-24 15:30:
00"), it
undergoes a transformation. The processor converts it into a computer-friendly format called a ZonedDateTime object.

### Example

Let's say you have an event stream with a property containing values like "2023-11-24 15:30:00" and you want to make
sure your computer understands it. You can use
this processor to convert it into a format that's machine-friendly.

***

## Getting Started

To use this processor, you need one thing in your data:

1. **Datetime String**: This is the name of the event property that contains the human-readable datetime string, like "2023-11-24 15:30:00".


### Configuration

The only thing you need to configure is the time zone.
1. **Time Zone**: Specify the time zone that applies to your datetime if it doesn't already have this information.This ensures that the processor understands the context of your
datetime.

## Output

After the conversion happens, the processor adds a new piece of information to your data stream:

* **dateTime**: This is the transformed datetime in a format that computers can easily work with.