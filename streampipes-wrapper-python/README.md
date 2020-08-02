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
# [WIP] Apache StreamPipes Wrapper for Python

**NOTE**: 

> The python wrapper is currently under development. Thus, it only works in combination when having Java processor for describing, registration and talking to the backend.

### A minimal example
#### Define a processor
```
from streampipes.core import EventProcessor


class HelloWorldProcessor(EventProcessor):

    def on_invocation(self):
        pass

    def on_event(self, event):
        event['greeting'] = 'hello world'
        return event

    def on_detach(self):
        pass
```
This processor received an event `dict` and adds a new `greeting` field including the message `hello world` to it.
#### Add to processor dict
````
from streampipes.core import StandaloneModelSubmitter
from streampipes.manager import Declarer
from streampipes.model.pipeline_element_config import Config

from helloworld import HelloWorldProcessor


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
Add the newly defined `HelloWorldProcessor` to the processors dictionary including a unique id `org.apache.streampipes.processors.python.helloworld`.
