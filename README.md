# Streampipes Helm Chart

## Installation

Requires Helm (https://helm.sh/) and an active connection to a Kubernetes Cluster with a running Tiller-Server

```sh
$ brew install helm kubernetes-cli
```

## Start Streampipes

Run command in chart directory

```sh
helm install ./ --name streampipes-core
```