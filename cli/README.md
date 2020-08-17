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

**Current version:** 0.67.0-SNAPSHOT

```bash
StreamPipes CLI - Manage your StreamPipes environment with ease

Usage: streampipes COMMAND [OPTIONS]

Options:
  --help, -h      show help
  --version, -v   show version

Commands:
  clean       Clean all configs/docker data volumes from system
  info        Get information
  logs        Get container logs for specific container
  ps          List all StreamPipes container for running environment
  pull        Download latest images from Dockerhub
  restart     Restart StreamPipes environment
  start       Start StreamPipes environment
  stop        Stop and Remove StreamPipes environment
  template    Select StreamPipes environment template

Run 'streampipes COMMAND --help' for more info on a command.
```

## Prerequisite
The CLI is basically a wrapper around multiple `docker` and `docker-compose` commands plus some additional sugar.

* Docker >= 17.06.0
* Docker-Compose >= 1.17.0 (Compose file format: 3.4)
* On Windows hosts: GitBash


Tested on: **macOS**, **Linux**, **Windows***)

> **NOTE**: *) If you're using Windows the CLI only works in combination with GitBash - PowerShell won't work.


## Usage: Along dev life-cycle
**List** available environment templates
```bash
streampipes template --list
```

**Set** environment template, e.g. `pipeline-element` if you want to write a new pipeline element
```bash
streampipes template --set pipeline-element
```

**Start** environment ( default: `dev` mode).
> **NOTE**: `dev` mode is enabled by default since we rely on open ports to core service such as `consul`, `couchdb`, `kafka` etc. to reach from the IDE when developing.

```bash
streampipes start
# start in regular mode with unmapped ports
# streampipes start --user
```
Now you're good to go to write your new pipeline element :tada: :tada: :tada:

> **HINT for extensions**: Use our [Maven archetypes](https://streampipes.apache.org/docs/docs/dev-guide-archetype/) to setup a project skeleton and use your IDE of choice for development. However, we do recommend using IntelliJ.

> **HINT for core**: To work on `backend` or `ui` features you need to set the template to `backend` and clone the core repository [incubator-streampipes](https://github.com/apache/incubator-streampipes) - check the prerequisites there for more information.

**Stop** environment and remove docker container
```bash
streampipes stop
```

## Additionally, useful commands

**Get logs** of specific service
```bash
streampipes logs --follow backend
```

**Update** all services of current environment
```bash
streampipes pull
```

**Restart** all services of current environment or specific services
```bash
streampipes restart
# restart backend & consul
# streampipes restart backend consul
```

**Clean** your system and remove created StreamPipes Docker volumes
```bash
streampipes clean
```

## Run `streampipes` from anywhere? No problem
Simply add the path the cli directory to your `$PATH` variable, e.g. in your `.bashrc` or `.zshrc`
> **NOTE**: So far, only tested on **macOS** and **Linux**.

```bash
export PATH="/path/to/incubator-streampipes-installer/cli:$PATH"
```

## Upgrade to new version
To upgrade to a new version, simply edit the version tag in `VERSION`.

## Get help
If you have any problems during the installation or questions around StreamPipes, you'll get help through one of our community channels:

- [Slack](https://slack.streampipes.org)
- [Mailing Lists](https://streampipes.apache.org/mailinglists.html)

And don't forget to follow us on [Twitter](https://twitter.com/streampipes)!

## License
[Apache License 2.0](../LICENSE)
