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
# StreamPipes Helm Chart

This Helm chart deploys Apache StreamPipes, a self-service Industrial IoT toolbox designed for stream processing and analytics.

## Architecture

The StreamPipes deployment consists of three main components:

- **Backend**: Core processing engine and API server
- **UI**: Web-based frontend for user interaction
- **Extensions**: Adapters and pipeline elements for stream processing

## Prerequisites

- Kubernetes 1.19+
- Helm 3.2.0+
- PV provisioner support in the underlying infrastructure (for persistence)

## Dependencies

The chart has optional dependencies on:
- CouchDB: Document database for metadata storage
- InfluxDB: Time series database
- Kafka: Message broker for data streaming

## Installation

```bash
# Add the StreamPipes Helm repository
helm repo add streampipes https://apache.github.io/streampipes-helm

# Install the chart with the release name "my-release"
helm install my-release streampipes/streampipes
```

## Configuration Reference

| Parameter | Description | Default |
|-----------|-------------|---------|
| `image.registry` | Image registry for StreamPipes images | `apache/streampipes` |
| `image.pullPolicy` | Pull policy for images | `IfNotPresent` |
| `imagePullSecrets` | Image pull secrets | `[]` |
| `nameOverride` | Override the name of the chart | `""` |
| `fullnameOverride` | Override the full name of the chart | `""` |
| `serviceAccount.create` | Create service account | `true` |
| `serviceAccount.annotations` | Service account annotations | `{}` |
| `serviceAccount.name` | Service account name, if not set will be auto-generated | `""` |
| `serviceAccount.automount` | Automount service account token | `true` |

### Backend Configuration

| Parameter | Description | Default |
|-----------|-------------|---------|
| `backend.image` | Backend image name | `"backend"` |
| `backend.tag` | Backend image tag (defaults to Chart.AppVersion) | `""` |
| `backend.replicaCount` | Number of backend replicas | `1` |
| `backend.containerPort` | Backend container port | `8030` |
| `backend.service.type` | Service type for backend | `ClusterIP` |
| `backend.service.port` | Service port for backend | `8030` |
| `backend.persistence.existingClaim` | Use existing PVC | `""` |
| `backend.persistence.storageClass` | Storage class for PVC | `""` |
| `backend.persistence.accessModes` | Access modes for PVC | `["ReadWriteOnce"]` |
| `backend.persistence.size` | Storage size for PVC | `1Gi` |
| `backend.initialDelaySeconds` | Initial delay for probes | `60` |
| `backend.periodSeconds` | Period for probes | `10` |
| `backend.failureThreshold` | Failure threshold for probes | `12` |
| `backend.restartPolicy` | Restart policy | `Always` |
| `backend.securityContext` | Security context for backend pods | `{}` |
| `backend.podAnnotations` | Annotations for backend pods | `{}` |
| `backend.podLabels` | Additional labels for backend pods | `{}` |
| `backend.updateStrategy` | Update strategy for backend deployment | `{}` |

### UI Configuration

| Parameter | Description | Default |
|-----------|-------------|---------|
| `ui.image` | UI image name | `"ui"` |
| `ui.tag` | UI image tag (defaults to Chart.AppVersion) | `""` |
| `ui.replicaCount` | Number of UI replicas | `1` |
| `ui.containerPort` | UI container port | `8080` |
| `ui.service.type` | Service type for UI | `ClusterIP` |
| `ui.service.port` | Service port for UI | `8080` |
| `ui.service.resolverActive` | Enable DNS resolver in nginx | `false` |
| `ui.service.resolver` | DNS resolver for nginx | `kube-dns.kube-system.svc.cluster.local` |
| `ui.initialDelaySeconds` | Initial delay for probes | `60` |
| `ui.periodSeconds` | Period for probes | `10` |
| `ui.failureThreshold` | Failure threshold for probes | `12` |
| `ui.restartPolicy` | Restart policy | `Always` |
| `ui.securityContext` | Security context for UI pods | `{}` |
| `ui.podAnnotations` | Annotations for UI pods | `{}` |
| `ui.podLabels` | Additional labels for UI pods | `{}` |

### Extensions Configuration

| Parameter | Description | Default |
|-----------|-------------|---------|
| `extensions.image` | Extensions image name | `"extensions-all-jvm"` |
| `extensions.tag` | Extensions image tag | `""` |
| `extensions.replicaCount` | Number of extensions replicas | `1` |
| `extensions.containerPort` | Extensions container port | `8090` |
| `extensions.service.type` | Service type for extensions | `ClusterIP` |
| `extensions.service.port` | Service port for extensions | `8090` |
| `extensions.initialDelaySeconds` | Initial delay for probes | `60` |
| `extensions.periodSeconds` | Period for probes | `10` |
| `extensions.failureThreshold` | Failure threshold for probes | `12` |
| `extensions.restartPolicy` | Restart policy | `Always` |
| `extensions.securityContext` | Security context for extensions pods | `{}` |
| `extensions.podAnnotations` | Annotations for extensions pods | `{}` |
| `extensions.podLabels` | Additional labels for extensions pods | `{}` |
| `extensions.updateStrategy` | Update strategy for extensions deployment | `{}` |

### StreamPipes Application Configuration

| Parameter | Description | Default |
|-----------|-------------|---------|
| `streampipes.secrets.existingSecret` | Existing secret for StreamPipes credentials | `""` |
| `streampipes.secrets.adminPasswordSecretKey` | Key in existing secret for admin password | `""` |
| `streampipes.secrets.clientSecretKey` | Key in existing secret for client secret | `""` |
| `streampipes.secrets.encryptionPasscodeSecretKey` | Key in existing secret for encryption passcode | `""` |
| `streampipes.admin.email` | Admin email | `"admin@streampipes.apache.org"` |
| `streampipes.admin.password` | Admin password (auto-generated if empty) | `"admin"` |
| `streampipes.serviceUser.user` | Service user name | `"service-user"` |
| `streampipes.serviceUser.secret` | Service user secret (auto-generated if empty) | `""` |
| `streampipes.encryption.passcode` | Encryption passcode (auto-generated if empty) | `""` |
| `streampipes.broker.type` | Message broker type | `"kafka"` |

### External Services Configuration

| Parameter | Description | Default |
|-----------|-------------|---------|
| `externalCouchdb.host` | External CouchDB host | `""` |
| `externalCouchdb.port` | External CouchDB port | `5984` |
| `externalCouchdb.adminUsername` | External CouchDB admin username | `""` |
| `externalCouchdb.adminPassword` | External CouchDB admin password | `""` |
| `externalCouchdb.existingSecret` | Existing secret for CouchDB credentials | `""` |
| `externalCouchdb.adminUsernameKey` | Key in existing secret for admin username | `"adminUsername"` |
| `externalCouchdb.adminPasswordKey` | Key in existing secret for admin password | `"adminPassword"` |
| `externalInfluxdb.host` | External InfluxDB host | `""` |
| `externalInfluxdb.port` | External InfluxDB port | `8086` |
| `externalInfluxdb.admin.token` | External InfluxDB admin token | `""` |
| `externalInfluxdb.existingSecret` | Existing secret for InfluxDB credentials | `""` |
| `externalInfluxdb.adminTokenSecretKey` | Key in existing secret for admin token | `"sp-influxdb-admin-token"` |
| `externalKafka.host` | External Kafka host | `""` |
| `externalKafka.port` | External Kafka port | `9092` |

### Ingress Configuration

| Parameter | Description | Default |
|-----------|-------------|---------|
| `ingress.enabled` | Enable ingress | `false` |
| `ingress.className` | Ingress class name | `""` |
| `ingress.annotations` | Ingress annotations | `{}` |
| `ingress.host` | Hostname for ingress | `""` |

### Monitoring Configuration

| Parameter | Description | Default |
|-----------|-------------|---------|
| `monitoring.enabled` | Enable ServiceMonitor for Prometheus | `false` |
| `monitoring.interval` | Scrape interval | `30s` |
| `monitoring.labels` | Additional labels for ServiceMonitor | `{}` |

### Proxy Configuration

| Parameter | Description | Default |
|-----------|-------------|---------|
| `proxy` | HTTP/HTTPS proxy URL | `""` |
| `noProxy` | Comma-separated list of hosts to exclude from proxy | `""` |

## Examples

### Minimal Installation

```yaml
# values.yaml
backend:
  persistence:
    storageClass: "standard"
```

### Production Deployment

```yaml
# values.yaml
backend:
  replicaCount: 2
  persistence:
    size: 10Gi

influxdb:
  enabled: false

externalInfluxdb:
  host: "influxdb.example.com"
  existingSecret: "influxdb-secret"

ingress:
  enabled: true
  className: "nginx"
  host: "streampipes.example.com"
  annotations:
    cert-manager.io/cluster-issuer: "letsencrypt-prod"
```

## Upgrading

When upgrading, ensure that you maintain compatibility with your existing data and configuration. Any changes to secrets or external services should be carefully managed.

## Security Considerations

- By default, the chart generates random passwords if not provided
- For production deployments, use custom secrets and consider externalized database services
- Enable TLS via ingress annotations for secure communications