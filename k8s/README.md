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
# StreamPipes k8s - The Operator's Dream
StreamPipes k8s is a helm chart to deploy StreamPipes on Kubernetes.

<!-- BEGIN do not edit: set via ../upgrade_versions.sh -->
**Current version:** 0.67.0-SNAPSHOT
<!-- END do not edit -->

## Prerequisite
Requires Helm (https://helm.sh/) and an active connection to a kubernetes cluster with a running tiller server.

Tested with:
* K3s v1.18.6+k3s1 (6f56fa1d) with K8s v1.18.6
* Helm v3.1.2

## Start StreamPipes
Run command:

```sh
helm install streampipes ./ --set deployment=lite
```

```sh
helm install streampipes ./ --set deployment=full
```

## Delete StreamPipes

```sh
helm del streampipes
```

## Get help
If you have any problems during the installation or questions around StreamPipes, you'll get help through one of our community channels:

- [Slack](https://slack.streampipes.org)
- [Mailing Lists](https://streampipes.apache.org/mailinglists.html)

And don't forget to follow us on [Twitter](https://twitter.com/streampipes)!

## License
[Apache License 2.0](../LICENSE)
