# Apache StreamPipes Wrapper for Python

**NOTE**: 

THIS IS WIP. IT CURRENTLY ONLY WORKS IN INTERPLAY WITH A JAVA PROCESSOR IMPLEMENTING THE MODEL.

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
