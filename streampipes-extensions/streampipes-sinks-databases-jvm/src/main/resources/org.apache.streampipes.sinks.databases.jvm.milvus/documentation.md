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

## Milvus
<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description
stores events in a Milvus database.

***

## Required input
This sink does not have any requirements and works with any incoming event type.

***

## Configuration

### URI
The URI of the Milvus instance.

### Token
A valid access token to access the specified Milvus instance.(default: "root:milvus")

### DBName
The name of the database to which the target Milvus instance belongs.

### DatabaseReplicasNumber
The number of replicas of the database to create.

### CollectionName
The name of the collection to create.

### Vector
The name of the vector field to create.

## Output

(not applicable for data sinks)



