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

"""Case contract and extensible registration."""

from abc import ABC, abstractmethod

from streampipes_performance.config import CaseDocumentation, Model
from streampipes_performance.plugins import Registry

REGISTRY = Registry("case")


def register(name):
    def decorate(cls):
        REGISTRY.add(name, cls)
        return cls

    return decorate


class Case(ABC):
    # Targets this case runs on; targets that extend one of them inherit the case.
    targets = ("streampipes",)
    # Services the readiness check waits for: extensions, nats, influxdb_metrics (only when configured).
    services = ()
    Parameters = Model
    documentation = CaseDocumentation()

    def __init__(self, parameters, runtime_settings=None):
        self.parameters = self.Parameters.model_validate(parameters)
        self.runtime_settings = runtime_settings

    @abstractmethod
    def setup(self, context):
        """Create resources and establish readiness."""

    @abstractmethod
    def run(self, context):
        """Execute the workload and record observations."""

    def prepare_results(self, context):
        return {}

    def cleanup(self, context):
        return context.cleanup()
