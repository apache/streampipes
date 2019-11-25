# StreamPipes Helm Chart
StreamPipes helm chart to deploy StreamPipes on Kubernetes.

Currently: **StreamPipes v0.65.0**

## Prerequisite

Requires Helm (https://helm.sh/) and an active connection to a kubernetes cluster with a running tiller server.

Tested with:
* Kubernetes v1.14.8
* Helm v2.16.1

## Start StreamPipes

Run command:

```sh
helm install ./ --name streampipes --set deployment=lite
```

```sh
helm install ./ --name streampipes --set deployment=full
```

## Delete StreamPipes

```sh
helm del --purge streampipes
```
