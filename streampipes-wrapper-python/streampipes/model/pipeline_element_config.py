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
import os

from streampipes.utils.register import ConsulUtils


class Config(object):
    def __init__(self, app_id: str) -> None:
        if not app_id:
            raise ValueError

        self.app_id = app_id
        self.host: str = ""
        self.port: int = 0
        self.service: str = ""

    def register(self, type: str, env_key: str, default, description: str, configuration_scope=None, is_password=None):
        if not type:
            raise ValueError
        if not env_key:
            raise ValueError
        if not default:
            raise ValueError

        if type is 'host':
            self.host = self._env_or_default(env_key, default)
        elif type is 'port':
            self.port = self._env_or_default(env_key, default)
        elif type is 'service':
            self.service = self._env_or_default(env_key, default)

        ConsulUtils().register_config(self.app_id, env_key, default, description, configuration_scope, is_password)

    @staticmethod
    def _env_or_default(key, default):
        if key is not None:
            if os.getenv(key):
                return os.getenv(key)
        return default
