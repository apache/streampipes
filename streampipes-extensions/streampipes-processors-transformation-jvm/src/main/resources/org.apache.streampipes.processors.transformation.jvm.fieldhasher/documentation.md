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

## Field Hasher

<p align="center">
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

The Field Hasher processor is a data transformation component that applies cryptographic hash functions to string values in a data stream. It can be used to encode sensitive information, generate unique identifiers, or transform data for privacy and security purposes.

The processor supports three widely-used hash algorithms:
* **MD5** - A 128-bit hash function
* **SHA1** - A 160-bit hash function
* **SHA2** - A 256-bit hash function (SHA-256 implementation)

***

## Required input

This processor requires an event stream that contains at least one field of type string. The string field will be used as input for the hash function.

***

## Configuration

### Field
Specifies the string field that will be encoded. This field must exist in the input event stream and must contain string values.

### Hash Algorithm
Select the hash algorithm to use for encoding the string field. Available options are:
* **SHA1** - Produces a 40-character hexadecimal hash
* **SHA2** - Produces a 64-character hexadecimal hash
* **MD5** - Produces a 32-character hexadecimal hash

## Output

The processor modifies the input event by replacing the value of the selected field with its hashed version. All other fields in the event remain unchanged.

### Example

#### Input Event
```json
{
  "timestamp": 1617183834000,
  "sensorId": "sensor123",
  "value": 42.5,
  "user": "john.doe@example.com"
}
```

#### Configuration
* Field: user
* Hash Algorithm: MD5

#### Output Event
```json
{
  "timestamp": 1617183834000,
  "sensorId": "sensor123",
  "value": 42.5,
  "user": "e87f955d3b3499b8b13e901fd61b6b64"
}
```

## Use Cases

1. **Privacy Protection**: Hash personally identifiable information (PII) before storage or transmission
2. **Data Anonymization**: Create anonymous identifiers from user data
3. **Checksum Generation**: Generate checksums for data validation
4. **Unique ID Creation**: Create unique identifiers from combinations of fields

## Notes

* The hash functions are one-way transformations - the original value cannot be recovered from the hash
* The same input will always produce the same hash value
* Different hash algorithms provide different levels of collision resistance and output lengths
* For security-critical applications, consider using SHA2 as it provides stronger cryptographic properties