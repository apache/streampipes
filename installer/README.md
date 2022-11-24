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

# Apache StreamPipes installer
This project contains a variety of StreamPipes installation and operation options, namely:

* **[StreamPipes Compose](./compose)** - The User's Choice
* **[StreamPipes CLI](./cli)** - The Developer's Favorite
* **[StreamPipes k8s](./k8s)** - The Operator's Dream

> **NOTE**: StreamPipes CLI & k8s are highly recommended for developers or operators. Standard users should stick to StreamPipes Compose.

This is useful in order to easily spin up StreamPipes' microservice environment consisting of:

* **backend**, **connect master** and **ui** container (see [streampipes](https://github.com/apache/streampipes))
* **extensions**, i.e. connect adapter, custom sources and pipeline elements (see [streampipes-extensions](https://github.com/apache/streampipes-extensions)), as well as
* mandatory **third-party services** such as databases, message broker etc.

## How to get started?
Clone this project
```bash
git clone https://github.com/apache/streampipes-installer
```
or download the ZIP of the installer. Click on the green button `Code` on the top right. Then click on `Download ZIP`.

## Upgrade version for all options
To upgrade the StreamPipes version all at once for all options and README files you can use the little helper script.
> **IMPORTANT**: This script does not check for valid version input.

```bash
./upgrade_versions.sh <version>
```

## Bugs and Feature Requests

If you've found a bug or have a feature that you'd love to see in StreamPipes, feel free to create an issue in our Jira:
[https://issues.apache.org/jira/projects/STREAMPIPES](https://issues.apache.org/jira/projects/STREAMPIPES)

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
* Ask for a mentor who helps you understanding the code base and guides you through the first setup steps
* Find an issue in our [Jira](https://issues.apache.org/jira/projects/STREAMPIPES) which is tagged with a _newbie_ tag
* Have a look at our developer wiki at [https://cwiki.apache.org/confluence/display/STREAMPIPES](https://cwiki.apache.org/confluence/display/STREAMPIPES) to learn more about StreamPipes development.

Have fun!

## Feedback
We'd love to hear your feedback! Subscribe to [users@streampipes.apache.org](mailto:users@streampipes.apache.org)

## License
[Apache License 2.0](LICENSE)
