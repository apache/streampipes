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

## Qdrant

<p align="center">
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

Stores events in a Qdrant vector database. This sink connector allows you to store vector data along with associated metadata in a Qdrant collection.

***

## Required input

This sink requires an input stream that contains a vector field. The vector field should be a list of float values.

***

## Configuration

### Host

The host address of the Qdrant instance (e.g., "localhost" or "xyz-example.cloud-region.cloud-provider.cloud.qdrant.io").

### Port

The port number of the Qdrant instance (default is 6334 for gRPC).

### API Key

The API key for authentication with Qdrant. This is required for secure access to the Qdrant instance.

### Collection Name

The name of the collection where the data will be stored. If the collection doesn't exist, it will be created automatically.

### ID Field

The field name that will be used as the unique identifier for each point in the collection. This should be a UUID string.

### Vector Field

The name of the field containing the vector data. This field should contain a list of float values.

### Vector Dimension

The dimension of the vectors to be stored (default is 384). This must match the dimension of your input vectors.

### Distance Metric

The distance metric to use for vector similarity search. Available options are:

- Cosine
- Euclid
- Dot
- Manhattan

## Output

(not applicable for data sinks)

## Notes

- The sink automatically creates the collection if it doesn't exist
- All non-vector fields from the input event are stored as payload
- The sink uses [gRPC for communication](https://qdrant.tech/documentation/interfaces/#grpc-interface) with Qdrant
- Vector data must be provided as a list of float values
- The ID fmust be a valid UUID string
