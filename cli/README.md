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

# CLI for StreamPipes
The StreamPipes CLI is focused on developer, either planning to write new extensions such as **connect adapters, processors, sinks**  or working on the core of StreamPipes, namely **backend** or **ui**. To provide more flexibility what services need to be running we provide the CLI to easily set up a suitable dev environment locally.

#### Structure & Description
The CLI is written in [argbash](https://github.com/matejak/argbash) and basically is a wrapper around several docker-compose commands plus some additional sugar.

```bash
.
├── README.md
├── services/   # holds all docker-compose service descriptions
├── sp          # CLI
├── sp.m4       # argbash file    
├── templates/  # holds all dev templates used to generate `system` file
└── tmpl_env    # template env file used to generate `.env` file
```

<!-- ## TL;DR
```bash
# start StreamPipes standalone lite version including current Java processors
./sp start
``` -->

## Prequisite
* Docker
* Docker-Compose
* Free ports (see mapped ports of services under `services/<service>/docker-compose.yml`)

> **NOTE**: By using the CLI, we explicitely map ports of services to the host system. Thus, it might happy that you run into port conflicts if any other service already binds to that address.

## Template structure

We provide necessary `service templates` for various development scenarios:

```bash
templates
├── backend-dev # for core development
├── full        # for developing distributed extensions (Flink processors)
├── lite        # for developing standalone extensions (Java processors)
└── ui-dev      # for ui development
```

## Usage along Dev cycle: set-template, start, stop, (clean), update

**First**, we select a template - this copies the content of the template and creates a new `system` file in the root dir:
```bash
./sp set-template <TEMPLATE>

# ./sp set-template lite
# [INFO]  Set configuration for template: lite
```

**Second**, we start the setup - this parsed the system file and iterates through the services directory, and appends each docker-compose.yml file.
> **Note**: Issuing this command also pull the necessary images

```bash
./sp start


# ...
#
# INFO: StreamPipes 0.67.0-SNAPSHOT is now ready to be used on your system
#       Check https://streampipes.apache.org/ for information on StreamPipes
#
#       Go to the UI and follow the instructions to get started: http://localhost/
```

**Third**, stopping the setup is as easy as follows:
```bash
./sp stop
```

**Fourth (Optional)**, stopping the service does not remove the created Docker volumes. In case you also want to clean this up use the following command.
```bash
./sp clean
```

Additionally, if you want to update the services specified in your current `system` file, you can run:

```bash
./sp update
```

## Update changes to CLI
We leverage [argbash](https://github.com/matejak/argbash) to build this CLI. To include changes to `sp.m4` file we can generate a new version of `sp` using the following command.

```bash
${PATH_TO_ARGBASH}/bin/argbash sp.m4 -o sp
```

<!-- All active services are defined in the system file.
All available services are in the services folder.

## Features Suggestion
* start (service-name) (--hostname "valueHostName") (--defaultip)
  * Starts StreamPipes or service
* stop (service-name)
  * Stops StreamPipes and deletes containers
* restart (service-name)
  * Restarts containers
* update (service-name) (--renew)
  * Downloads new docker images
  * --renew restart containers after download
* set-template (template-name)
  * Replaces the systems file with file mode-name
* log (service-name)
  * Prints the logs of the service

* list-available
* list-active
* list-templates

* activate (service-name) (--all)
  * Adds service to system and starts
* add (service-name) (--all)
  * Adds service to system
* deactivate {remove} (service-name)  (--all)
  * Stops container and removes from system file
* clean
  * Stops and cleans SP installation, remove networks
* remove-settings:
  * Stops StreamPipes and deletes .env file
* set-version:
  * Change the StreamPipes version in the tmpl_env file

* generate-compose-file


## Flags

* ARG_OPTIONAL_SINGLE([hostname], , [The default hostname of your server], )
* ARG_OPTIONAL_BOOLEAN([defaultip],d, [When set the first ip is used as default])
* ARG_OPTIONAL_BOOLEAN([all],a, [Select all available StreamPipes services])





## Naming Files / Folders
* active-services
* services/
* system-configurations -> templates/
* tmpl_env -->
