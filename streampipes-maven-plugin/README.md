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

# Apache StreamPipes Maven Plugin

## Description

The Apache StreamPipes is an internal developer plugin which eases the generation of documentation for
adapters and pipeline elements.

The plugin generates documentation files which can be integrated into the UI.

## Usage

The streampipes-maven-plugin can either be started from the command line or embedded into a project's pom.

### Prerequisites

The plugin must be started from a module which contains an `Init` class which inherits `ExtensionsModelSubmitter`.
By default, the goal runs in the `package` phase.

### Command line

```bash
# Switch to a directory containing StreamPipes extensions and an Init class, e.g., streampipes-extensions-all-jvm

mvn streampipes:extract-docs -DinitClass=org.apache.streampipes.extensions.all.jvm.AllExtensionsInit
```

### Inclusion in pom file

```xml

<plugin>
    <groupId>org.apache.streampipes</groupId>
    <artifactId>streampipes-maven-plugin</artifactId>
    <version>0.93.0</version>
    <dependencies>
        <dependency>
            <groupId>org.apache.rocketmq</groupId>
            <artifactId>rocketmq-client-java</artifactId>
            <version>5.0.2</version>
        </dependency>
    </dependencies>
    <configuration>
        <initClass>org.apache.streampipes.extensions.all.jvm.AllExtensionsInit</initClass>
    </configuration>
    <executions>
        <execution>
            <phase>package</phase>
            <goals>
                <goal>extract-docs</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

* Replace the `version` with the current development version.
* Replace the `initClass` with the module's init class.
* The dependency to RocketMQ is only needed for modules containing the RocketMQ sink and can be omitted otherwise.

## Output

The plugin creates a new folder `docs` in the module's `target` directory.

The folder includes:

* An `img` folder which has a subdirectory for each extension (named by the `appId`) containing the icon.
* A `pe` folder which has a subdirectory for each extension (named by the `appId`) containing the `documentation.md`
  file, which has been rewritten to match the requirements of the Docusaurus Markdown parser.
* An updated `sidebar.json` file containing the sidebar, which is downloaded from the `streampipes-website` repo on
  branch `dev` and updated with the current set of extensions.
