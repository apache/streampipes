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
**Current version:** 0.67.0
<!-- END do not edit -->

## Prerequisite
Requires Helm (https://helm.sh/) and an active connection to a kubernetes cluster with a running tiller server.

Tested with:
* K3s v1.18.8+k3s1 (6b595318) with K8s v1.18.8
* Helm v3.1.2

## Usage
We provide two helm chart options to get you going:

- **default**: a light-weight option with few pipeline elements, needs less memory
- **full**:  contains more pipeline elements, requires **>16 GB RAM** (recommended)

**Starting** the **default** helm chart option is as easy as simply running the following command from the root of this folder:
> **NOTE**: Starting might take a while since we also initially pull all Docker images from Dockerhub.

```bash
helm install streampipes ./
```
After a while, all containers should successfully started, indicated by the `Running` status. 
```bash
kubectl get pods
NAME                                           READY   STATUS    RESTARTS   AGE
activemq-66d58f47cf-2r2nb                      1/1     Running   0          3m27s
backend-76ddc486c8-nswpc                       1/1     Running   0          3m27s
connect-master-7b477f9b79-8dfvr                1/1     Running   0          3m26s
connect-worker-78d89c989c-9v8zs                1/1     Running   0          3m27s
consul-55965f966b-gwb7l                        1/1     Running   0          3m27s
couchdb-77db98cf7b-xnnvb                       1/1     Running   0          3m27s
influxdb-b95b6479-r8wh8                        1/1     Running   0          3m27s
kafka-657b5fb77-dp2d6                          1/1     Running   0          3m27s
pipeline-elements-all-jvm-79c445dbd9-m8xcs     1/1     Running   0          3m27s
sources-watertank-simulator-6c6b8844f6-6b4d7   1/1     Running   0          3m27s
ui-b94bd9766-rm6zb                             2/2     Running   0          3m27s
zookeeper-5d9947686f-6nzgs                     1/1     Running   0          3m26s
```

After all containers are successfully started just got to your browser and visit any of the k8s cluster nodes on
`http://<NODE_IP>` to finish the installation.

> **NOTE**: If you're running Docker for Mac or Docker for Windows with a local k8s cluster, the above step to use your host IP might not work. Luckily, you can port-forward a service port to your localhost using the following command to be able to access the UI either via `http://localhost` or `http://<HOST_IP>` (you require sudo to run this command in order to bind to a privileged port).
```bash
kubectl port-forward svc/ui --address=0.0.0.0 80:80
```

Starting the **full** helm chart option is almost the same:
```bash
helm install streampipes ./ --set deployment=full
```

**Deleting** the current helm chart deployment:
```bash
helm del streampipes
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
[Apache License 2.0](../LICENSE)
