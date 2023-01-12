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

# Activate EPSG Database Service

## CLI
If using the CLI add 'epsg' in StreamPipes environment file (.spenv)
Running the `pipeline-element` environment, your environment file should look like this

```
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

[environment:pipeline-element]
backend
consul
extensions-all-jvm
couchdb
kafka
ui
sources-watertank-simulator
zookeeper
influxdb
epsg

```


## Docker installer

Coming soon


## K8s Helm

Coming soon



# Prepare EPSG Database

The database, in which the epsg data will be imported, is already included as a service (Postgres DB). The Only step the user has to do is to fill the database with the required scripts.

Therefore, the following empty scripts must be replaced in the `incubator-streampipes/installer/scripts/epsg` folder

* PostgreSQL_Table_Script.sql
* PostgreSQL_Data_Script.sql
* PostgreSQL_FKey_Script.sql
* EPSG_FINISH.sql

Due to license agreement, you have to create an <a href="https://epsg.org/user/register/" target="_blank">account</a>


to accept the <a href="https://epsg.org/terms-of-use.html" target="_blank">term of use</a> of the 'EPSG Dataset'.

With an account, you can download the EPSG Dataset <a href="https://epsg.org/archives.html" target="_blank">here</a>

.
Make sure you download the EPSG-v9_9_1-PostgreSQL.zip, which supports the 2007 data model release. Higher versions are not 
yet supported by the <a href="https://sis.apache.org/" target="_blank">Apache SIS </a>, which is used in StreamPipes to handle Geometry reprojections.

Unzip the folder and replace files
* PostgreSQL_Table_Script.sql
* PostgreSQL_Data_Script.sql
* PostgreSQL_FKey_Script.sql

in the `incubator-streampipes/installer/scripts/epsg` folder.

For indexing the imported data and get better performance, go to 
<a href="https://github.com/apache/sis/blob/master/core/sis-referencing/src/main/resources/org/apache/sis/referencing/factory/sql/EPSG_Finish.sql" target="_blank">this file</a>
and replace it with the
* EPSG_FINISH.sql

in the `incubator-streampipes/installer/scripts/epsg` folder.

# Import into Database

## CLI

1) Restart StreamPipes with `streampipes up -d`

2) Now check if the database scripts were executed with
   `streampipes logs --follow epsg`

If you already started StreamPipes before, the import was done with the empty scripts! You have to delete the corresponding docker volume. The scripts are only imported during the first start!

Therefore, make sure that StreamPipes is not running with `streampipes down`.

Now execute  `docker volume rm streampipes_epsg`
Then you can repeat Step 1) and 2)


## Docker

Coming soon


## k8s Helm

Coming soon


# Check Data Import










