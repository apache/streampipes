# Flink in Docker

This is a Docker image appropriate for running Flink in Kuberenetes. You can also run it locally with docker-compose in which case you get two containers by default: 
* `flink-master` - runs a Flink JobManager in cluster mode and exposes a port for Flink and a port for the WebUI.
* `flink-worker` - runs a Flink TaskManager and connects to the Flink JobManager via static DNS name `flink-master`.

The structure of the repo is heavily influenced by [Spark in Kubernetes](https://github.com/kubernetes/application-images/tree/master/spark)

# Usage

## Build

You only need to build an image if you have changed it, otherwise skip to the next step. Get the code from Github and build an image by running `make`

## Run locally

Get the code from Github and run `docker-compose up` to start with a single TaskManager. 
`docker-compose scale flink-worker=5` - scale to 5 workers.

## Run in Kubernetes

Use [flink-kubernetes](https://github.com/melentye/flink-kubernetes)
