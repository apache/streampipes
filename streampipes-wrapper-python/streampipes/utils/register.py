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
from streampipes.model.pipeline_element_config import Config


class ConsulUtils(object):
    _DEFAULT_CONSUL_CONFIG = {
        'CONSUL_HOST': 'consul',
        'CONSUL_PORT': 8500,
        'HEALTHCHECK_INTERVAL': '10s',
        'CONSUL_BASIC_ROUTE': 'sp/v1/'
    }

    def __init__(self):
        self.consul = self._consul()

    def register_service(self, app_id: str, host: str, port: int):
        if not app_id:
            raise ValueError
        if not host:
            raise ValueError
        if not port:
            raise ValueError

        self.consul.agent.service.register(name='pe', service_id=app_id, address=host, port=port,
                                           tags=['pe', 'python', app_id],
                                           check=Check.http(
                                               url='http://' + host + ':' + str(port),
                                               interval=self._DEFAULT_CONSUL_CONFIG['HEALTHCHECK_INTERVAL'],
                                               header={"Accept": ["application/json"]}
                                           ))

    def register_configs(self, config: Config):
        for key, config_item in config.config_items.items():
            key_route = self._get_consul_key_route(config.app_id, key)
            index, data = self.consul.kv.get(key_route)
            # TODO: update existing keys?
            if data is None:
                self.consul.kv.put(key_route, config_item.to_json())

    def _get_consul_key_route(self, app_id, key):
        return self._DEFAULT_CONSUL_CONFIG['CONSUL_BASIC_ROUTE'] + app_id + '/' + key

    def _consul(self):
        if os.getenv('CONSUL_LOCATION'):
            return consul.Consul(host=os.getenv('CONSUL_LOCATION'),
                                 port=self._DEFAULT_CONSUL_CONFIG['CONSUL_PORT'])
        elif os.getenv('SP_DEBUG'):
            return consul.Consul(host='localhost',
                                 port=self._DEFAULT_CONSUL_CONFIG['CONSUL_PORT'])
        else:
            return consul.Consul(host=self._DEFAULT_CONSUL_CONFIG['CONSUL_HOST'],
                                 port=self._DEFAULT_CONSUL_CONFIG['CONSUL_PORT'])
