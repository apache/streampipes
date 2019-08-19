# Streampipes Helm Chart

## Installation

Requires Helm (https://helm.sh/) and an active connection to a kubernetes cluster with a running tiller server

```sh
$ brew install helm kubernetes-cli
```

## Start Streampipes

Run command in chart directory

```sh
helm install ./ --name streampipes
```

## Known problems
kafka must be kafka-service