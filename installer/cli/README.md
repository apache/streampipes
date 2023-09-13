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
# StreamPipes CLI - The Developer's Favorite

The StreamPipes command-line interface (CLI) is focused on developers in order to provide an easy entrypoint to set up a suitable dev environment, either planning on developing

* new extensions such as **connect adapters, processors, sinks** or,
* new core features for **backend** and **ui**.

<!-- BEGIN do not edit: set via ../upgrade_versions.sh -->
**Current version:** 0.93.0-SNAPSHOT
<!-- END do not edit -->

## TL;DR

```bash
streampipes env --list
[INFO] Available StreamPipes environment templates:
pipeline-element
...
streampipes env --set pipeline-element
streampipes up -d
```
> **NOTE**: use `./installer/cli/streampipes` if you haven't add it to the PATH and sourced it (see section "Run `streampipes` from anywhere?").

## Prerequisite
The CLI is basically a wrapper around multiple `docker` and `docker-compose` commands plus some additional sugar.

* Docker >= 17.06.0
* Docker-Compose >= 1.26.0 (Compose file format: 3.4)
* Google Chrome (recommended), Mozilla Firefox, Microsoft Edge
* For Windows Developer: GitBash only


Tested on: (***macOS**, **Linux**, **Windows***)

> **NOTE**: If you're using Windows the CLI only works in combination with GitBash - CMD, PowerShell won't work.


## CLI commands overview

```
StreamPipes CLI - Manage your StreamPipes environment with ease

Usage: streampipes COMMAND [OPTIONS]

Options:
  --help, -h      show help
  --version, -v   show version

Commands:
  add         Add new StreamPipes service (e.g., adapter, processor, sink)
  clean       Remove StreamPipes data volumes, dangling images and network
  down        Stop and remove StreamPipes containers
  env         Inspect and select StreamPipes environments
  info        Get information
  logs        Get container logs for specific container
  ps          List all StreamPipes container for running environment
  pull        Download latest images from Dockerhub
  restart     Restart StreamPipes container
  start       Start StreamPipes container
  stop        Stop StreamPipes container
  up          Create and start StreamPipes container environment

Run 'streampipes COMMAND --help' for more info on a command.
```

## Usage: Along dev life-cycle

**List** available environment templates.
```bash
streampipes env --list
```

**Inspect** services in an available environment to know what kind of services it is composed of.
```bash
streampipes env --inspect pipeline-element
```

**Set** environment, e.g. `pipeline-element`, if you want to write a new pipeline element.
```bash
streampipes env --set pipeline-element
```

**Start** environment ( default: `dev` mode). Here the service definition in the selected environment is used to start the multi-container landscape.
> **NOTE**: `dev` mode is enabled by default since we rely on open ports to core service such as `consul`, `couchdb`, `kafka` etc. to reach from the IDE when developing. If you don't want to map ports (except the UI port), then use the `--no-ports` flag.

```bash
streampipes up -d
# start in production mode with unmapped ports
# streampipes up -d --no-ports
```
Now you're good to go to write your new pipeline element :tada: :tada: :tada:

> **HINT for extensions**: Use our [Maven archetypes](https://streampipes.apache.org/docs/docs/dev-guide-archetype/) to setup a project skeleton and use your IDE of choice for development. However, we do recommend using IntelliJ.

> **HINT for core**: To work on `backend` or `ui` features you need to set the template to `backend` and clone the core repository [streampipes](https://github.com/apache/streampipes) - check the prerequisites there for more information.

**Stop** environment and remove docker container
```bash
streampipes down
# want to also clean docker data volumes when stopping the environment?
# streampipes down -v
```

## Additionally, useful commands

**Start individual services only?** We got you! You chose a template that suits your needs and now you only want to start individual services from it, e.g. only Kafka and Consul.

> **NOTE**: the service names need to be present and match your current `.spenv` environment.

```bash
streampipes up -d kafka consul
```

**Get current environment** (if previously set using `streampipes env --set <environment>`).
```bash
streampipes env
```

**List containers** of environment.
```bash
streampipes ps
# include also stopped container
streampieps ps --all
```

**Get logs** of specific service and use optional `--follow` flag to stay attached to the logs.
```bash
streampipes logs --follow backend
```

**Update** all services of current environment
```bash
streampipes pull
```

**Stop** existing StreamPipes containers
```bash
streampipes stop pipeline-elements-all-jvm
```

**Start** existing StreamPipes containers
```bash
streampipes start pipeline-elements-all-jvm
```

**Restart** existing services
```bash
# restart backend consul container
streampipes restart backend consul
# restart existing services by removing and recreating container instance
streampipes restart --force-create pipeline-elements-all-jvm
```

**Clean** your system and remove created StreamPipes Docker volumes, StreamPipes docker network and dangling StreamPipes images of old image layers.
```bash
streampipes clean
# remove volumes, network and dangling images
# streampipes clean --volumes
```

## Add newly developed StreamPipes Pipeline Elements to service catalog
As you develop new pipeline elements, e.g., adapters, processors or sinks, you might want to add them to your existing environment. Adding these components requires minimal effort.
We can simply use `streampipes add` command to populate relevant Docker Compose files under the StreamPipes working directory (`deploy/standalone/`).

> **NOTE**: This requires your own pipeline elements to be already containerized and accessible to pull, e.g., from a public Docker repository such as DockerHub. If you use a private
> repository make sure your local Docker Daemon has access to it.

```bash
# check out streampipes add --help for more options
# add new pipeline element to catalog, here my-processor
streampipes add my-processor

# add new pipeline element with custom image and ports
streampipes add my-processor --image myrepo/myprocessor:0.68.0 --ports 8090:8090 --ports 8091:8091

# [Optional] add new pipeline element and persistently store service in environment (here: lite) and activate service in current environment (.spenv)
streampipes add my-processor --store lite --activate
```

In general, this creates necessary Docker Compose files in the local service catalog:
```bash
cli/deploy/standalone/my-processor
├── docker-compose.dev.yml
└── docker-compose.yml
```

If you activated the pipeline element while adding via the flag `--activate`, you can directly start you new pipeline element using the following command. Otherwise, you will have to manually add it at a later point.
> **NOTE**: The service name **must** match the directory name under `deploy/standalone/<service_name>`
```bash
streampipes up -d my-processor
```

## Modify/Create an environment template
As of now, this step has to be done **manually**. All environments are located in `environments/`.

```bash
├── adapter               # developing a new connect adapter
├── backend               # developing core backend features
├── basic                 # wanna run core, UI, connect etc from the IDE?
├── full                  # full version containing more pipeline elements
├── lite                  # few pipeline elements, less memory  
├── pipeline-element      # developing new pipeline-elements
└── ui                    # developing UI features
```
**Modifying an existing environment template**. To modify an existing template, you can simply add a `<YOUR_NEW_SERVICE>` to the template.
> **NOTE**: You need to make sure, that the service your are adding exists in `deploy/standalone/service/<YOUR_NEW_SERVICE>`. If your're adding a completely new service take a look at existing ones, create a new service directory and include a `docker-compose.yml` and `docker-compose.dev.yml` file.

```
[environment:backend]
activemq
kafka
...
<YOUR_NEW_SERVICE>
```

**Creating a new** environment template. To create a new environment template, place a new file `environments/<YOUR_NEW_ENVIRONMENT>` in the template directory. Open the file and use the following schema.
> **IMPORTANT**: Please make sure to have `[environment:<YOUR_NEW_ENVIRONMENT>]` header in the first line of your new template matching the name of the file. Make sure to use small caps letters (lowercase) only.

```
[environment:<YOUR_NEW_ENVIRONMENT>]
<SERVICE_1>
<SERVICE_2>
...
```

## Run `streampipes` from anywhere? No problem
Simply add the path to this cli directory to your `$PATH` (on macOS, Linux) variable, e.g. in your `.bashrc` or `.zshrc`, or `%PATH%` (on Windows).

For **macOS**, or **Linux**:

```bash
export PATH="/path/to/streampipes-installer/cli:$PATH"
```

For **Windows 10** add `installer\cli` to environment variables, e.g. check this [documentation](https://helpdeskgeek.com/windows-10/add-windows-path-environment-variable/).


## Upgrade to new version
To upgrade to a new version, simply edit the version tag `SP_VERSION` in the `.env` file.

## Bugs and Feature Requests

If you've found a bug or have a feature that you'd love to see in StreamPipes, feel free to create an issue Find an issue on [GitHub](https://github.com/apache/streampipes/issues).

## Get help
If you have any problems during the installation or questions around StreamPipes, you'll get help through one of our community channels:

- [Slack](https://slack.streampipes.org)
- [Mailing Lists](https://streampipes.apache.org/mailinglists.html)

And don't forget to follow us on [Twitter](https://twitter.com/streampipes)!

## Contribute
We welcome contributions to StreamPipes. If you are interested in contributing to StreamPipes, let us know! You'll
 get to know an open-minded and motivated team working together to build the next IIoT analytics toolbox.

Here are some first steps in case you want to contribute:
* Subscribe to our dev mailing list [dev-subscribe@streampipes.apache.org](dev-subscribe@streampipes.apache.org)
* Send an email, tell us about your interests and which parts of StreamPipes you'd like to contribute (e.g., core or UI)!
* Ask for a mentor who helps you to understand the code base and guides you through the first setup steps
* Find an issue on [GitHub](https://github.com/apache/streampipes/issues) which is tagged with a _good first issue_ tag
* Have a look at our developer wiki at [https://cwiki.apache.org/confluence/display/STREAMPIPES](https://cwiki.apache.org/confluence/display/STREAMPIPES) to learn more about StreamPipes development.

Have fun!

## Feedback
We'd love to hear your feedback! Subscribe to [users@streampipes.apache.org](mailto:users@streampipes.apache.org)

## License
[Apache License 2.0](../LICENSE)
