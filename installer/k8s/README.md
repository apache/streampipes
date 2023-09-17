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
**Current version:** 0.93.0-SNAPSHOT
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
##Parameters

###Common parameters
| Parameter Name                                   | Description                                             | Value                                   |
|--------------------------------------------------|---------------------------------------------------------|-----------------------------------------|
| deployment                                       | Deployment type (lite or full)                          | lite                                    |
| preferredBroker                                  | Preferred broker for deployment                         | "nats"                                  |
| pullPolicy                                       | Image pull policy                                       | "Always"                                |
| restartPolicy                                    | Restart policy for the container                        | Always                                  |
| persistentVolumeReclaimPolicy                    | Reclaim policy for persistent volumes                   | "Delete"                                |
| persistentVolumeAccessModes                      | Access mode for persistent volumes                      | "ReadWriteOnce"                         |
| initialDelaySeconds                              | Initial delay for liveness and readiness probes         | 60                                      |
| periodSeconds                                    | Interval between liveness and readiness probes          | 30                                      |
| failureThreshold                                 | Number of consecutive failures for readiness probes     | 30                                      |
| hostPath                                         | Host path for the application                           | ""                                      |

###StreamPipes common parameters
| Parameter Name                                  | Description                                             | Value                                    |
|-------------------------------------------------|---------------------------------------------------------|------------------------------------------|
| streampipes.version                             | StreamPipes version                                     | "0.93.0-SNAPSHOT"                        |
| streampipes.registry                            | StreamPipes registry URL                                | "apachestreampipes"                      |
| streampipes.auth.secretName                     | The secret name for storing secrets                     | "sp-secrets"                             |
| streampipes.auth.users.admin.user               | The initial admin user                                  | "admin@streampipes.apache.org"           |
| streampipes.auth.users.admin.password           | The initial admin password (leave empty for autogen)    | "admin"                                  |
| streampipes.auth.users.service.user             | The initial service account user                        | "sp-service-client"                      |
| streampipes.auth.users.service.secret           | The initial service account secret                      | empty (auto-generated)                   |
| streampipes.auth.encryption.passcode            | Passcode for value encryption                           | empty (auto-generated)                   |
| streampipes.core.appName                        | StreamPipes backend application name                    | "backend"                                |
| streampipes.core.port                           | StreamPipes backend port                                | 8030                                     |
| streampipes.core.persistence.storageClassName   | Storage class name for backend PVs                      | "hostpath"                               |
| streampipes.core.persistence.storageSize        | Size of the backend PV                                  | "1Gi"                                    |
| streampipes.core.persistence.claimName          | Name of the backend PersistentVolumeClaim               | "backend-pvc"                            |
| streampipes.core.persistence.pvName             | Name of the backend PersistentVolume                    | "backend-pv"                             |
| streampipes.core.service.name                   | Name of the backend service                             | "backend"                                |
| streampipes.core.service.port                   | TargetPort of the StreamPipes backend service           | 8030                                     |
| streampipes.ui.appName                          | StreamPipes UI application name                         | "ui"                                     |
| streampipes.ui.resolverActive                   | Flag for enabling DNS resolver for Nginx proxy          | true                                     |
| streampipes.ui.port                             | StreamPipes UI port                                     | 8088                                     |
| streampipes.ui.resolver                         | DNS resolver for Nginx proxy                            | "kube-dns.kube-system.svc.cluster.local" |
| streampipes.ui.service.name                     | Name of the UI service                                  | "ui"                                     |
| streampipes.ui.service.type                     | Type of the UI service                                  | "ClusterIP"                              |
| streampipes.ui.service.nodePort                 | Node port for the UI service                            | 8088                                     |
| streampipes.ui.service.port                     | TargetPort of the StreamPipes UI service                | 8088                                     |
| streampipes.ingress.active                      | Flag for enabling Ingress for StreamPipes               | false                                    |
| streampipes.ingress.annotations                 | Annotations for Ingress                                 | {}                                       |
| streampipes.ingress.host                        | Hostname for Ingress                                    | ""                                       |
| streampipes.ingressroute.active                 | Flag for enabling IngressRoute for StreamPipes          | true                                     |
| streampipes.ingressroute.annotations            | Annotations for IngressRoute                            | {}                                       |
| streampipes.ingressroute.entryPoints            | Entry points for IngressRoute                           | ["web", "websecure"]                     |
| streampipes.ingressroute.host                   | Hostname for IngressRoute                               | ""                                       |
| streampipes.ingressroute.certResolverActive     | Flag for enabling certificate resolver for IngressRoute | true                                     |
| streampipes.ingressroute.certResolver           | Certificate resolver for IngressRoute                   | ""                                       |


###Extensions common parameters
| Parameter Name                                  | Description                                             | Value                                    |
|-------------------------------------------------|---------------------------------------------------------|------------------------------------------|
| extensions.iiot.appName                         | IIoT extensions application name                        | extensions-all-iiot                      |
| extensions.iiot.port                            | Port for the IIoT extensions application                | 8090                                     |
| extensions.iiot.service.name                    | Name of the IIoT extensions service                     | extensions-all-iiot                      |
| extensions.iiot.service.port                    | TargetPort of the IIoT extensions service               | 8090                                     |


###External common parameters

####Consul common parameters
| Parameter Name                                  | Description                                              | Value                                    |
|-------------------------------------------------|----------------------------------------------------------|------------------------------------------|
| external.consul.appName                         | Consul application name                                  | "consul"                                 |
| external.consul.version                         | Consul version                                           | 1.14.3                                   |
| external.consul.webPort                         | Port number for the Consul web interface                 | 8500                                     |
| external.consul.dnsPort                         | Port number for the Consul DNS interface                 | 8600                                     |
| external.consul.persistence.storageClassName    | Storage class name for Consul PVs                        | "hostpath"                               |
| external.consul.persistence.storageSize         | Size of the Consul PV                                    | "1Gi"                                    |
| external.consul.persistence.claimName           | Name of the Consul PersistentVolumeClaim                 | "consul-pvc"                             |
| external.consul.persistence.pvName              | Name of the Consul PersistentVolume                      | "consul-pv"                              |
| external.consul.service.name                    | Name of the Consul service                               | "consul"                                 |
| external.consul.service.webPort                 | TargetPort of the Consul service for web interface       | 8500                                     |
| external.consul.service.dnsPort                 | TargetPort of the Consul service for DNS interface       | 8600                                     |

####Couchdb common parameters
| Parameter Name                                  | Description                                              | Value                                    |
|-------------------------------------------------|----------------------------------------------------------|------------------------------------------|
| external.couchdb.appName                        | CouchDB application name                                 | "couchdb"                                |
| external.couchdb.version                        | CouchDB version                                          | 3.3.1                                    |
| external.couchdb.user                           | CouchDB admin username                                   | "admin"                                  |
| external.couchdb.password                       | CouchDB admin password                                   | empty (auto-generated)                   |
| external.couchdb.port                           | Port for the CouchDB service                             | 5984                                     |
| external.couchdb.service.name                   | Name of the CouchDB service                              | "couchdb"                                |
| external.couchdb.service.port                   | TargetPort of the CouchDB service                        | 5984                                     |
| external.couchdb.persistence.storageClassName   | Storage class name for CouchDB PVs                       | "hostpath"                               |
| external.couchdb.persistence.storageSize        | Size of the CouchDB PV                                   | "1Gi"                                    |
| external.couchdb.persistence.claimName          | Name of the CouchDB PersistentVolumeClaim                | "couchdb-pvc"                            |
| external.couchdb.persistence.pvName             | Name of the CouchDB PersistentVolume                     | "couchdb-pv"                             |

####Influxdb common parameters
| Parameter Name                                  | Description                                              | Value                                    |
|-------------------------------------------------|----------------------------------------------------------|------------------------------------------|
| external.influxdb.appName                       | InfluxDB application name                                | "influxdb"                               |
| external.influxdb.version                       | InfluxDB version                                         | 2.6                                      |
| external.influxdb.username                      | InfluxDB admin username                                  | "admin"                                  |
| external.influxdb.password                      | InfluxDB admin password                                  | empty (auto-generated)                   |
| external.influxdb.adminToken                    | InfluxDB admin token                                     | empty (auto-generated)                   |
| external.influxdb.initOrg                       | InfluxDB initial organization                            | "sp"                                     |
| external.influxdb.initBucket                    | InfluxDB initial bucket                                  | "sp"                                     |
| external.influxdb.initMode                      | InfluxDB initialization mode                             | "setup"                                  |
| external.influxdb.apiPort                       | Port number for the InfluxDB service (API)               | 8083                                     |
| external.influxdb.httpPort                      | Port number for the InfluxDB service (HTTP)              | 8086                                     |
| external.influxdb.grpcPort                      | Port number for the InfluxDB service (gRPC)              | 8090                                     |
| external.influxdb.service.name                  | Name of the InfluxDB service                             | "influxdb"                               |
| external.influxdb.service.apiPort               | TargetPort of the InfluxDB service for API               | 8083                                     |
| external.influxdb.service.httpPort              | TargetPort of the InfluxDB service for HTTP              | 8086                                     |
| external.influxdb.service.grpcPort              | TargetPort of the InfluxDB service for gRPC              | 8090                                     |
| external.influxdb.persistence.storageClassName  | Storage class name for InfluxDB PVs                      | "hostpath"                               |
| external.influxdb.persistence.storageSize       | Size of the InfluxDB PV                                  | "1Gi"                                    |
| external.influxdb.persistence.storageSizeV1     | Size of the InfluxDB PV for v1 databases                 | "1Gi"                                    |
| external.influxdb.persistence.claimName         | Name of the InfluxDBv2 PersistentVolumeClaim             | "influxdb2-pvc"                          |
| external.influxdb.persistence.claimNameV1       | Name of the InfluxDBv1 PersistentVolumeClaim             | "influxdb-pvc"                           |
| external.influxdb.persistence.pvName            | Name of the InfluxDBv2 PersistentVolume                  | "influxdb2-pv"                           |
| external.influxdb.persistence.pvNameV1          | Name of the InfluxDBv1 PersistentVolume                  | "influxdb-pv"                            |


####Nats common parameters
| Parameter Name                                  | Description                                              | Value                                    |
|-------------------------------------------------|----------------------------------------------------------|------------------------------------------|
| external.nats.appName                           | NATS application name                                    | "nats"                                   |
| external.nats.port                              | Port for the NATS service                                | 4222                                     |
| external.nats.version                           | NATS version                                             |                                          |
| external.nats.service.type                      | Type of the NATS service                                 | "NodePort"                               |
| external.nats.service.externalTrafficPolicy     | External traffic policy for the NATS service             | "Local"                                  |
| external.nats.service.name                      | Name of the NATS service                                 | "nats"                                   |
| external.nats.service.port                      | TargetPort of the NATS service                           | 4222                                     |


####Kafka common parameters
| Parameter Name                                  | Description                                              | Value                                    |
|-------------------------------------------------|----------------------------------------------------------|------------------------------------------|
| external.kafka.appName                          | Kafka application name                                   | "kafka"                                  |
| external.kafka.version                          | Kafka version                                            | 2.2.0                                    |
| external.kafka.port                             | Port for the Kafka service                               | 9092                                     |
| external.kafka.service.name                     | Name of the Kafka service                                | "kafka"                                  |
| external.kafka.service.port                     | TargetPort of the Kafka service                          | 9092                                     |
| external.kafka.persistence.storageClassName     | Storage class name for Kafka PVs                         | "hostpath"                               |
| external.kafka.persistence.storageSize          | Size of the Kafka PV                                     | "1Gi"                                    |
| external.kafka.persistence.claimName            | Name of the Kafka PersistentVolumeClaim                  | "kafka-pvc"                              |
| external.kafka.persistence.pvName               | Name of the Kafka PersistentVolume                       | "kafka-pv"                               |


####Zookeeper common parameters
| Parameter Name                                  | Description                                              | Value                                    |
|-------------------------------------------------|----------------------------------------------------------|------------------------------------------|
| external.zookeeper.appName                      | ZooKeeper application name                               | "zookeeper"                              |
| external.zookeeper.version                      | ZooKeeper version                                        | 3.4.13                                   |
| external.zookeeper.port                         | Port for the ZooKeeper service                           | 2181                                     |
| external.zookeeper.service.name                 | Name of the ZooKeeper service                            | "zookeeper"                              |
| external.zookeeper.service.port                 | TargetPort of the ZooKeeper service                      | 2181                                     |
| external.zookeeper.persistence.storageClassName | Storage class name for ZooKeeper PVs                     | "hostpath"                               |
| external.zookeeper.persistence.storageSize      | Size of the ZooKeeper PV                                 | "1Gi"                                    |
| external.zookeeper.persistence.claimName        | Name of the ZooKeeper PersistentVolumeClaim              | "zookeeper-pvc"                          |
| external.zookeeper.persistence.pvName           | Name of the ZooKeeper PersistentVolume                   | "zookeeper-pv"                           |


####Pulsar common parameters
| Parameter Name                                  | Description                                              | Value                                    |
|-------------------------------------------------|----------------------------------------------------------|------------------------------------------|
| external.pulsar.appName                         | pulsar application name                                  | "pulsar"                                 |
| external.pulsar.version                         | pulsar version                                           | 3.0.0                                    |
| external.pulsar.port                            | Port for the pulsar service                              | 6650                                     |
| external.pulsar.service.name                    | Name of the pulsar service                               | "pulsar"                                 |
| external.pulsar.service.port                    | TargetPort of the pulsar service                         | 6650                                     |
| external.pulsar.persistence.storageClassName    | Storage class name for pulsar PVs                        | "hostpath"                               |
| external.pulsar.persistence.storageSize         | Size of the pulsar PV                                    | "1Gi"                                    |
| external.pulsar.persistence.claimName           | Name of the pulsar PersistentVolumeClaim                 | "pulsar-pvc"                             |
| external.pulsar.persistence.pvName              | Name of the pulsar PersistentVolume                      | "pulsar-pv"                              |

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
