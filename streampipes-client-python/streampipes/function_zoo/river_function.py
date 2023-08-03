#
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
from typing import Any, Callable, Dict, List, Optional

from streampipes.client.client import StreamPipesClient
from streampipes.functions.broker.broker_handler import get_broker_description
from streampipes.functions.function_handler import FunctionHandler
from streampipes.functions.registration import Registration
from streampipes.functions.streampipes_function import StreamPipesFunction
from streampipes.functions.utils.data_stream_generator import (
    RuntimeType,
    create_data_stream,
)
from streampipes.functions.utils.function_context import FunctionContext
from streampipes.model.resource.function_definition import FunctionDefinition


class RiverFunction(StreamPipesFunction):
    """Implementation of a StreamPipesFunction to enable an easy usage
    for Online Machine Learning models of the [River library](https://riverml.xyz/).

    The function trains the model with the incoming events and publishes the prediction to an output data stream.

    Parameters
    ----------
    function_definition: FunctionDefinition
        The function definition which contains the output stream.
    model: Any
        The model to train. It meant to be a River model/pipeline,
        but can be every model with a 'learn_one' and 'predict_one' method.
    supervised: bool
        Define if the model is supervised or unsupervised.
    target_label: str
        Define the name of the target attribute if the model is supervised.
    on_start: Callable[[Any, FunctionContext], None]
        A function to be called when this StreamPipesFunction gets started.
    on_event: Callable[[Any, Dict[str, Any], str], None]
        A function to be called when this StreamPipesFunction receives an event.
    on_stop: Callable[[Any], None]
        A function to be called when this StreamPipesFunction gets stopped.
    """

    def __init__(
        self,
        function_definition: FunctionDefinition,
        model: Any,
        supervised: bool,
        target_label: Optional[str],
        on_start: Callable[[Any, FunctionContext], None],
        on_event: Callable[[Any, Dict[str, Any], str], None],
        on_stop: Callable[[Any], None],
    ) -> None:
        super().__init__(function_definition)
        self.model = model
        self.supervised = supervised
        self.target_label = target_label
        self.on_start = on_start
        self.on_event = on_event
        self.on_stop = on_stop

        self.learning = True

    def onServiceStarted(self, context: FunctionContext):
        """Executes the `on_start` method of the function.

        Parameters
        ----------
        context: FunctionContext
            The functions' context

        Returns
        -------
        None

        """
        self.on_start(self, context)

    def onEvent(self, event: Dict[str, Any], streamId: str):
        """Trains the model with the incoming events and sends the prediction back to StreamPipes.

        Parameters
        ----------
        event: Dict[str, Any]
            The incoming event that serves as input for the function
        streamId: str
            Identifier of the corresponding data stream

        Returns
        -------
        None

        """
        self.on_event(self, event, streamId)
        output_event = {}
        if self.supervised:
            y = event.pop(self.target_label)  # type: ignore
            output_event["truth"] = y
        output_event["learning"] = self.learning
        output_event["prediction"] = self.model.predict_one(event)
        if self.learning:
            if self.supervised:
                self.model.learn_one(event, y)
            else:
                self.model.learn_one(event)

        self.add_output(self.function_definition.get_output_stream_ids()[0], output_event)

    def onServiceStopped(self):
        """Executes the `on_stop` function."""
        self.on_stop(self)


class OnlineML:
    """Wrapper class to enable an easy usage for Online Machine Learning models of the River library.

    It creates a StreamPipesFunction to train a model with the incoming events of a data stream and
    creates an output data stream that publishes the prediction to StreamPipes.

    Parameters
    ----------
    client: StreamPipesClient
        The client for the StreamPipes API.
    stream_ids: List[str]
        The ids of the data stream to train the model.
    model: Any
        The model to train. It meant to be a River model/pipeline,
        but can be every model with a 'learn_one' and 'predict_one' methode.
    prediction_type: str
        The data type of the prediction.
        Is only needed when you continue to work with the prediction in StreamPipes.
    supervised: bool
        Define if the model is supervised or unsupervised.
    target_label: Optional[str]
        Define the name of the target attribute if the model is supervised.
    on_start: Callable[[Any, FunctionContext], None]
        A function to be called when this StreamPipesFunction gets started.
    on_event: Callable[[Any, Dict[str, Any], str], None]
        A function to be called when this StreamPipesFunction receives an event.
    on_stop: Callable[[Any], None]
        A function to be called when this StreamPipesFunction gets stopped.
    """

    def __init__(
        self,
        client: StreamPipesClient,
        stream_ids: List[str],
        model: Any,
        prediction_type: str = RuntimeType.STRING.value,
        supervised: bool = False,
        target_label: Optional[str] = None,
        on_start: Callable[[Any, FunctionContext], None] = lambda self, context: None,
        on_event: Callable[[Any, Dict[str, Any], str], None] = lambda self, event, streamId: None,
        on_stop: Callable[[Any], None] = lambda self: None,
    ):
        self.client = client

        attributes = {"learning": RuntimeType.BOOLEAN.value, "prediction": prediction_type}
        if supervised:
            attributes["truth"] = prediction_type
            if target_label is None:
                raise ValueError("You must define a target attribute for a supervised model.")

        output_stream = create_data_stream(
            name="prediction",
            attributes=attributes,
            broker=get_broker_description(client.dataStreamApi.get(stream_ids[0])),  # type: ignore
        )
        function_definition = FunctionDefinition(consumed_streams=stream_ids).add_output_data_stream(output_stream)
        self.sp_function = RiverFunction(
            function_definition, model, supervised, target_label, on_start, on_event, on_stop
        )

    def start(self):
        """Registers the function and starts the training."""
        registration = Registration()
        registration.register(self.sp_function)
        self.function_handler = FunctionHandler(registration, self.client)
        self.function_handler.initializeFunctions()

    def set_learning(self, learning: bool):
        """Start or stop the training of the model.

        Parameters
        ----------
        learning: bool
            Defines if the training should be continued
        """
        self.sp_function.learning = learning

    def stop(self):
        """Stops the function and ends the training forever."""
        self.function_handler.disconnect()
