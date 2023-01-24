from typing import Any, Dict, List, Optional

from streampipes_client.client.client import StreamPipesClient
from streampipes_client.functions.function_handler import FunctionHandler
from streampipes_client.functions.registration import Registration
from streampipes_client.functions.streampipes_function import StreamPipesFunction
from streampipes_client.functions.utils.function_config import (
    FunctionConfig,
    FunctionId,
)
from streampipes_client.functions.utils.function_context import FunctionContext


class RiverFunction(StreamPipesFunction):
    def __init__(
        self,
        function_id: FunctionId,
        stream_ids: List[str],
        model,
        supervised: bool,
        target_label,
        on_start,
        on_event,
        on_stop,
    ) -> None:
        super().__init__()
        self.function_id = function_id
        self.stream_ids = stream_ids
        self.model = model
        self.supervised = supervised
        self.target_label = target_label
        self.on_start = on_start
        self.on_event = on_event
        self.on_stop = on_stop

        self.learning = True

    def getFunctionConfig(self) -> FunctionConfig:
        return FunctionConfig(self.function_id)

    def requiredStreamIds(self) -> List[str]:
        return self.stream_ids

    def onServiceStarted(self, context: FunctionContext):
        self.on_start(self, context)

    def onEvent(self, event: Dict[str, Any], streamId: str):
        self.on_event(self, event, streamId)
        if self.learning:
            if self.supervised:
                y = event.pop(self.target_label)
                self.model.learn_one(event, y)
            else:
                self.model.learn_one(event)
            # print("learning")
        # else:
        #    if self.supervised:
        #        print(event.pop(self.target_label))
        #    print(event, self.model.predict_one(event), )

    def onServiceStopped(self):
        self.on_stop(self)


class OnlineMachineLearning:
    def __init__(
        self,
        client: StreamPipesClient,
        function_id: FunctionId,
        stream_ids: List[str],
        model,
        supervised: bool = False,
        target_label: Optional[str] = None,
        on_start=lambda self, context: None,
        on_event=lambda self, event, streamId: None,
        on_stop=lambda self: None,
    ):
        self.client = client
        self.sp_function = RiverFunction(
            function_id, stream_ids, model, supervised, target_label, on_start, on_event, on_stop
        )

    def start(self):
        registration = Registration()
        registration.register(self.sp_function)
        self.function_handler = FunctionHandler(registration, self.client)
        self.function_handler.initializeFunctions()

    def set_learning(self, learning):
        self.sp_function.learning = learning

    def stop(self):
        self.function_handler.disconnect()
