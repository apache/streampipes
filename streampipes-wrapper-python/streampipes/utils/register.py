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
""""""
import os
import consul
from consul import Check

from streampipes.model.config_item import ConfigItem


class ConsulUtils(object):
    _DEFAULT_CONSUL_CONFIG = {
        'CONSUL_HOST': 'consul',
        'CONSUL_PORT': 8500,
        'HEALTHCHECK_INTERVAL': '10s',
        'CONSUL_BASIC_ROUTE': 'sp/v1/'
    }

    if os.getenv('CONSUL_LOCATION'):
        consul = consul.Consul(host=os.getenv('CONSUL_LOCATION'), port=_DEFAULT_CONSUL_CONFIG['CONSUL_PORT'])
    elif os.getenv('SP_DEBUG'):
        consul = consul.Consul(host='localhost', port=_DEFAULT_CONSUL_CONFIG['CONSUL_PORT'])
    else:
        consul = consul.Consul(host=_DEFAULT_CONSUL_CONFIG['CONSUL_HOST'], port=_DEFAULT_CONSUL_CONFIG['CONSUL_PORT'])

    @classmethod
    def register_service(cls, app_id: str, host: str, port: int):
        # TODO: add service tags
        cls.consul.agent.service.register(name='pe',
                                          service_id=app_id,
                                          address=host,
                                          port=port,
                                          tags=['pe', 'python', app_id],
                                          check=Check.http(url='http://' + host + ':' + str(port),
                                                           interval=cls._DEFAULT_CONSUL_CONFIG['HEALTHCHECK_INTERVAL']))

    @classmethod
    def register_config(cls, app_id: str, env_key: str, default, description: str, configuration_scope=None, is_password=None):
        config_item = ConfigItem()

        if env_key is not None:
            if os.getenv(env_key):
                env_value = os.getenv(env_key)
                config_item.value = env_value
                config_item.value_type = cls.__check_default_type(env_value)
            else:
                config_item.value = default
                config_item.value_type = cls.__check_default_type(default)
        else:
            config_item.value = default
            config_item.value_type = cls.__check_default_type(default)

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

        key = cls.__get_consul_key(app_id, env_key)
        data = cls.consul.kv.get(key)
        # TODO: update existing keys?
        if data is None:
            cls.consul.kv.put(key, config_item.to_json())

    @classmethod
    def __get_consul_key(cls, app_id, key):
        return cls._DEFAULT_CONSUL_CONFIG['CONSUL_BASIC_ROUTE'] + app_id + '/' + key

    @staticmethod
    def __env_or_default(key, default):
        if key is not None:
            if os.getenv(key):
                return os.getenv(key)
            else:
                return default
        else:
            return default

    @staticmethod
    def __check_default_type(value) -> str:
        if isinstance(value, int):
            return 'xs:integer'
        elif isinstance(value, str):
            return 'xs:string'
        elif isinstance(value, bool):
            return 'xs:boolean'
        elif isinstance(value, float):
            return 'xs:float'
