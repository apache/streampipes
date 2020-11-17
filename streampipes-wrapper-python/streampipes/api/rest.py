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
""" API endpoints """
import threading

from waitress import serve
from flask import Flask

from streampipes.api.resources.dummy import DummyInterimsResource
from streampipes.api.resources.processor import SepaElementResource
from streampipes.api.resources.welcome import WelcomeResource


class PipelineElementApi(object):
    _FLASK_CONFIG = {
        'DEBUG': False,
        'DEVELOPMENT': False
    }

    def __init__(self):
        self.app = Flask(__name__, instance_relative_config=False)
        self.app.config.from_object(self._FLASK_CONFIG)

        with self.app.app_context():

            # register resources
            SepaElementResource.register(self.app, route_base='/', route_prefix='sepa')
            WelcomeResource.register(self.app, route_base='/')
            # TODO: delete when finished
            DummyInterimsResource.register(self.app, route_base='/')

    def run(self, port: int):
        print('serving API via waitress WSGI server ... http://{}:{}'.format('0.0.0.0', port))
        threading.Thread(target=serve, args=(self.app,), kwargs={'host': '0.0.0.0', 'port': int(port), '_quiet': True}).start()

