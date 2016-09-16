all: flink
push: push-flink
.PHONY: push push-flink flink

CONTAINER_NAME = melentye/flink
# To bump the flink version, bump the flink_ver in Dockerfile, bump
# this tag and reset to v1. You should also double check the native
# Hadoop libs at that point (we grab the 2.7.0 libs).
TAG = 1.0.3_v1

flink:
	docker build -t $(CONTAINER_NAME) .
	docker tag $(CONTAINER_NAME) $(CONTAINER_NAME):$(TAG)

push-flink: flink
	gcloud docker push $(CONTAINER_NAME)
	gcloud docker push $(CONTAINER_NAME):$(TAG)

clean:
	docker rmi $(CONTAINER_NAME):$(TAG) || :
	docker rmi $(CONTAINER_NAME) || :
