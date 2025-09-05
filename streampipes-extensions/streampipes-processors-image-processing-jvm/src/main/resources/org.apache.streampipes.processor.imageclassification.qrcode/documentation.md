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

## QR Code Reader

<p align="center">
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

QR Code Reader: Detects a QR Code in an image

***

## Required input

Input events require to have an image field.

***

## Configuration

### Image

Image of the QR code

### Send placeholder value if no qr code is detected

It is a boolean selection.

### Placeholder value

Place holder value

## Output

Outputs a similar event like below.

```
{
  'qrvalue': 'http://githhub.com/',
  'timestamp': 1621244783151
}
```