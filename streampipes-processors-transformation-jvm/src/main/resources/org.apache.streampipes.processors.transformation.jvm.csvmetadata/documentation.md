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

## CSV Metadata Enrichment
Enrich a datastream with information provided in a CSV file.
The data of the CSV file is matched by an id column with a property value of a String in the data stream.

***

## Description
Upload a CSV file with static meta information that will be appended to each event.
The file can contain different information for different keys in the stream.


### Structure of CSV file
The first row containes the runtime names for the properties to insert.
Once the file is uploaded the user can select which column to use for the matching property and which values should be appended.
Delimiter: ';'


***

## Example
Add the location of a production line to the event

### Input  event
```
{
  'line_id': 'line1', 
  'timestamp': 1586378041
}
```

### CSV File
```
production_line;location
line1;germany
line2;uk
line3;usa
```

### Configuration
* The field that is used for the lookup (Example: line_id)
* The CSV file (Example: Upload the csv file)
* Field to match (Example: production_line)
* Fields to append (Example: location)

### Output event 
```
{
  'line_id': 'line1', 
  'timestamp': 1586378041, 
  'location': 'germany'
}
```
