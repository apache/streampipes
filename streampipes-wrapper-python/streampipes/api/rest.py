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
import sys
import threading

from flask import Flask
from waitress import serve


class FlaskProductionConfig(object):
    DEBUG = False
    DEVELOPMENT = False


class PipelineElementApi(object):

    def __init__(self):
        self.app = Flask(__name__, instance_relative_config=False)
        self.app.config.from_object(FlaskProductionConfig)

        with self.app.app_context():
            # import endpoints
            from streampipes.api.resources import welcome
            from streampipes.api.resources import sepa

            # register blueprints
            self.app.register_blueprint(welcome.welcome_blueprint)
            self.app.register_blueprint(sepa.sepa_blueprint)

    def run(self, port: int):
        threading.Thread(target=serve, kwargs={'app': self.app, 'host': '0.0.0.0', 'port': port}).start()
