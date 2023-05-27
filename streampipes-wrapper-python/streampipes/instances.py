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
import abc
from deprecated import deprecated

@deprecated(version='0.93.0', reason="Please switch to StreamPipes Python (see README)")
class NamedStreamPipesEntity:
    __metaclass__ = abc.ABC

    def get_description(self):
        pass

@deprecated(version='0.93.0', reason="Please switch to StreamPipes Python (see README)")
class RunningInstances:
    __metaclass__ = abc.ABC

    _running_instances = {}

    def add(self, instance_id, description, invocation):
        self._running_instances[instance_id] = [description, invocation]

    def get_invocation(self, instance_id):
        invocation = self._running_instances.get(instance_id)[1]
        return invocation if invocation is not None else None

    def get_description(self, instance_id):
        return self._running_instances.get(instance_id)[0]

    def remove(self, instance_id):
        self._running_instances.pop(instance_id)

    def get_running_instances_count(self):
        return len(self._running_instances)
