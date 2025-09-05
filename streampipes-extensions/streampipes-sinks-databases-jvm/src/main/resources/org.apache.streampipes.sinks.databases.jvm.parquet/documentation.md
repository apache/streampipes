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

## Parquet
<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

stores events in Parquet

***

## Required input

This sink does not have any requirements and works with any incoming event type.

***

## Configuration

### SchemaName
The name of the schema to be used for the Parquet file.

### SchemaNamespace
The namespace of the schema to be used for the Parquet file.

### ParquetFileName
The name of the Parquet file to be created.

### ParquetGenerationDirectory
The directory where the Parquet file will be generated.

### RowGroupSize
The size of the row group in bytes. The default value is 134217728, which means that the row group size will be determined by the Parquet library.

### PageSize
The size of the page in bytes. The default value is 1048576, which means that the page size will be determined by the Parquet library.

### CompressionCodecName
The compression codec to be used for the Parquet file. The default value is "UNCOMPRESSED", which means that no compression will be applied. Other options are "SNAPPY", "GZIP", "LZO", "BROTLI", "LZ4", and "ZSTD".