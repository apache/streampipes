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
**Current version:** 0.92.0-SNAPSHOT
<!-- END do not edit -->

We provide two helm chart templates to get you going:

- **default**: a lightweight template with few pipeline elements, needs less memory
- **full**:  contains more pipeline elements, requires **>16 GB RAM** (recommended)

## Prerequisite
Requires Helm (https://helm.sh/) and an active connection to a kubernetes cluster with a running tiller server.

Tested with:
* K8s v1.19.3
* Helm v3.1.2
* Minikube v1.15.1 (recommended for local testing)

> **NOTE**: We experienced some problems with using host path volumes in Docker Desktop environments for persistent storage. Therefore, we suggest to use minikube for local testing.

## Local testing

We recommend using [minikube](https://minikube.sigs.k8s.io/docs/) for local testing. Follow instructions in their docs to setup test environment

Once installed, start local minikube node with a mapped host volume:
```bash
minikube start --mount-string ${HOME}/streampipes-k8s:/streampipes-k8s --mount --memory=4g --cpus=4
```

**Start** the a helm chart template by running the following command from the root of this folder:
> **NOTE**: Starting might take a while since we also initially pull all Docker images from Dockerhub.

```bash
helm install streampipes ./

# full template only recommend if you have sufficient resources
# helm install streampipes ./ --set deployment=full
```
After a while, all containers should successfully started, indicated by the `Running` status. 
```bash
kubectl get pods
NAME                                           READY   STATUS    RESTARTS   AGE
backend-76ddc486c8-nswpc                       1/1     Running   0          3m27s
consul-55965f966b-gwb7l                        1/1     Running   0          3m27s
couchdb-77db98cf7b-xnnvb                       1/1     Running   0          3m27s
influxdb-b95b6479-r8wh8                        1/1     Running   0          3m27s
extensions-all-jvm-79c445dbd9-m8xcs     1/1     Running   0          3m27s
ui-b94bd9766-rm6zb                             2/2     Running   0          3m27s
```

For **minikube users**:
> **NOTE**: If you're running Docker Desktop or Minikube with a local k8s cluster, the above step to use your host IP might not work. Luckily, you can port-forward a service port to your localhost using the following command to be able to access the UI either via `http://localhost` or `http://<HOST_IP>` (you require sudo to run this command in order to bind to a privileged port).
```bash
kubectl port-forward svc/ui --address=0.0.0.0 80:80
```

**Deleting** the current helm chart deployment:
```bash
helm del streampipes
```

We retain the created persistent volume. You need to manually delete it:
```bash
rm -rf ${HOME}/streampipes-k8s
```

## Bugs and Feature Requests

If you've found a bug or have a feature that you'd love to see in StreamPipes, feel free to create an issue on [GitHub](https://github.com/apache/streampipes/issues).

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
* Find an issue  on [GitHub](https://github.com/apache/streampipes/issues) which is tagged with a _good first issue_ tag
* Have a look at our developer wiki at [https://cwiki.apache.org/confluence/display/STREAMPIPES](https://cwiki.apache.org/confluence/display/STREAMPIPES) to learn more about StreamPipes development.

Have fun!

## Feedback
We'd love to hear your feedback! Subscribe to [users@streampipes.apache.org](mailto:users@streampipes.apache.org)

## License
[Apache License 2.0](../LICENSE)
