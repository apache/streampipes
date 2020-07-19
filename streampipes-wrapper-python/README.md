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
from streampipes.core import StandaloneSubmitter
from streampipes.manager import Declarer


def main():
    processors = {
        'org.apache.streampipes.processors.python.helloworld': HelloWorldProcessor,
    }

    Declarer.add(processors=processors)
    StandaloneSubmitter.init()


if __name__ == '__main__':
    main()
````
Add the newly defined `HelloWorldProcessor` to the processors dictionary including a unique id `org.apache.streampipes.processors.python.helloworld`.
