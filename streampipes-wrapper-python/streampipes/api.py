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
from flask import request, jsonify, Flask
from waitress import serve

from streampipes.configuration import flask_host, port
from streampipes.manager import ProcessorDispatcher


class EndpointAction(object):
    def __init__(self, action):
        self.action = action

    def __call__(self, *args, **kwargs):
        """ call corresponding handler method """
        response = self.action(**request.get_json())
        return jsonify(response)


class API(object):
    """ EventProcessorAPI contains relevant RESTful endpoints to start and stop """
    app = None

    def __init__(self, name='python-processor'):
        self.app = Flask(name)
        self.add_endpoints()

    def run(self):
        serve(self.app, host=flask_host, port=port)

    def add_endpoints(self):
        """ define and add event processor API endpoints """
        self.add_endpoint(endpoint='/invoke', endpoint_name='/invoke', methods=['POST'], handler=self.invoke)
        self.add_endpoint(endpoint='/detach', endpoint_name='/detach', methods=['POST'], handler=self.detach)

    def add_endpoint(self, endpoint=None, endpoint_name=None, handler=None, methods=None):
        self.app.add_url_rule(endpoint, endpoint_name, EndpointAction(handler), methods=methods)

    @staticmethod
    def invoke(**kwargs):
        """ Receives invocation graph from pipeline management in the backend

        :param json: contains invocation graph.
        :return: None.
        """
        return ProcessorDispatcher.start(**kwargs)

    @staticmethod
    def detach(**kwargs):
        """ Receives elementId to be terminated from pipeline management in the backend

        :param json: contains elementId to be terminated
        :return: None.
        """
        return ProcessorDispatcher.stop(**kwargs)