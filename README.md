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

# Apache StreamPipes (incubating) installer
This project contains a variety of StreamPipes installation and operation options, namely:

* **[StreamPipes Compose](./compose)** - The User's Choice
* **[StreamPipes CLI](./cli)** - The Developer's Favorite
* **[StreamPipes k8s](./k8s)** - The Operator's Dream

> **NOTE**: StreamPipes CLI & k8s are highly recommended for developers or operators. Standard users should stick to StreamPipes Compose.

## How to get started?
Clone this project
```bash
git clone https://github.com/apache/incubator-streampipes-installer
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

## License
[Apache License 2.0](LICENSE)
