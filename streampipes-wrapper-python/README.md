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

[![Github Actions](https://img.shields.io/github/workflow/status/apache/streampipes/build-and-deploy-docker-dev)](https://github.com/apache/streampipes/actions/)
[![Docker pulls](https://img.shields.io/docker/pulls/apachestreampipes/backend.svg)](https://hub.docker.com/r/apachestreampipes/backend/)
[![Maven central](https://img.shields.io/maven-central/v/org.apache.streampipes/streampipes-backend.svg)](https://img.shields.io/maven-central/v/org.apache.streampipes/streampipes-backend.svg)
[![License](https://img.shields.io/github/license/apache/streampipes.svg)](http://www.apache.org/licenses/LICENSE-2.0)
[![Last commit](https://img.shields.io/github/last-commit/apache/streampipes.svg)]()
[![Twitter](https://img.shields.io/twitter/follow/StreamPipes.svg?label=Follow&style=social)](https://twitter.com/StreamPipes)

## Apache StreamPipes Wrapper for Python [WIP]

**NOTE**:

> The StreamPipes wrapper for python is currently under development. Thus, the processor model description still needs
> to be implemented externally in Java.

## Apache StreamPipes

Apache StreamPipes enables flexible modeling of stream processing pipelines by providing a graphical
modeling editor on top of existing stream processing frameworks.

It leverages non-technical users to quickly define and execute processing pipelines based on an easily extensible
toolbox of data sources, data processors and data sinks. StreamPipes has an exchangeable runtime execution layer and
executes pipelines using one of the provided wrappers, e.g., for Apache Flink or Apache Kafka Streams.

Pipeline elements in StreamPipes can be installed at runtime - the built-in SDK allows to easily implement new
pipeline elements according to your needs. Pipeline elements are standalone microservices that can run anywhere -
centrally on your server, in a large-scale cluster or close at the edge.

## A Speudocode Example

**NOTE**:
Only works in combination with Java!

````
from streampipes.core import StandaloneModelSubmitter
from streampipes.manager import Declarer
from streampipes.model.pipeline_element_config import Config
from streampipes.core import EventProcessor


class HelloWorldProcessor(EventProcessor):

    def on_invocation(self):
        pass

    def on_event(self, event):
        event['greeting'] = 'hello world'
        return event

    def on_detach(self):
        pass


def main():
    # Configurations to be stored in key-value store (consul)
    config = Config(app_id='pe/org.apache.streampipes.processor.python')

    config.register(type='host',
                    env_key='SP_HOST',
                    default='processor-python',
                    description='processor hostname')

    config.register(type='port',
                    env_key='SP_PORT',
                    default=8090,
                    description='processor port')

    config.register(type='service',
                    env_key='SP_SERVICE_NAME',
                    default='Python Processor',
                    description='processor service name')

    processors = {
        'org.apache.streampipes.processors.python.helloworld': HelloWorldProcessor,
    }

    # Declarer
    # add the dict of processors to the Declarer
    # This is an abstract class that holds the specified processors
    Declarer.add(processors=processors)

    # StandaloneModelSubmitter
    # Initializes the REST api
    StandaloneModelSubmitter.init(config=config)


if __name__ == '__main__':
    main()
````
