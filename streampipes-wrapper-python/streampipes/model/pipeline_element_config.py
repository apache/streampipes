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
from streampipes.model.config_item import ConfigItem


class Config(object):
    def __init__(self, app_id: str) -> None:
        if not app_id:
            raise ValueError

        self.app_id = app_id
        self.host: str = ""
        self.port: int = 0
        self.service: str = ""
        self.config_items = {}

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

        config_item = self._create_config_item(env_key, default, description, configuration_scope, is_password)
        self.config_items[env_key] = config_item

    def _create_config_item(self, env_key: str, default, description: str, configuration_scope=None, is_password=None):
        config_item = ConfigItem()

        if env_key is not None:
            if os.getenv(env_key):
                env_value = os.getenv(env_key)
                config_item.value = env_value
                config_item.value_type = self._check_default_type(env_value)
            else:
                config_item.value = default
                config_item.value_type = self._check_default_type(default)
        else:
            config_item.value = default
            config_item.value_type = self._check_default_type(default)

        config_item.description = description

        if configuration_scope is not None:
            config_item.configuration_scope = configuration_scope
        else:
            # TODO: configuration_scope needed? Currently manually set
            config_item.configuration_scope = 'CONTAINER_STARTUP_CONFIG'

        if is_password is not None:
            config_item.is_password = is_password
        else:
            config_item.is_password = False

        return config_item

    @staticmethod
    def _env_or_default(key, default):
        if key is not None:
            if os.getenv(key):
                return os.getenv(key)
        return default

    @staticmethod
    def _check_default_type(value) -> str:
        if isinstance(value, int):
            return 'xs:integer'
        elif isinstance(value, str):
            return 'xs:string'
        elif isinstance(value, bool):
            return 'xs:boolean'
        elif isinstance(value, float):
            return 'xs:float'
