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
import os
from flask import jsonify, make_response, request
from flask_classful import FlaskView, route
from flask_negotiate import consumes


class Element(FlaskView):
    __metaclass__ = abc.ABC

    @route('/<element_id>', methods=['GET'])
    def get_description(self, element_id: str):
        # TODO: get element description
        # TODO: get element from declarer
        resp = {'element_id': element_id, 'description': 'dummy description'}
        return make_response(jsonify(resp), 200)

    @route('/<element_id>/assets', methods=['GET'])
    def get_assets(self, element_id: str):
        # TODO: send zipped asset
        resp = {'element_id': element_id, 'asset': 'dummy asset'}
        return make_response(jsonify(resp), 200)

    @route('/<element_id>/assets/icon', methods=['GET'])
    def get_assets_icon(self, element_id: str):
        # TODO: return icon as byte array
        resp = {'element_id': element_id, 'asset': 'dummy asset', 'icon': 'dummy icon'}
        return make_response(jsonify(resp), 200)

    @route('/<element_id>/assets/documentation', methods=['GET'])
    def get_assets_docs(self, element_id: str):
        # TODO: return icon as byte array
        resp = {'element_id': element_id, 'asset': 'dummy asset', 'docs': 'dummy docs'}
        return make_response(jsonify(resp), 200)

    @abc.abstractmethod
    def get_element_declarers(self):
        raise NotImplementedError()

    def get_declarer_by_id(self, element_id: str):
        return self.get_element_declarers().get(element_id=element_id)

    @classmethod
    def _get_json_ld(cls):
        pass

    @classmethod
    def _make_grounding(cls):
        pass

    @classmethod
    def _make_icon_path(cls, element_id: str):
        return cls._make_path(element_id, 'icon.png')

    @classmethod
    def _make_documentation_path(cls, element_id: str):
        return cls._make_path(element_id, 'documentation.md')

    @classmethod
    def _make_path(cls, element_id: str, asset_appendix: str):
        return element_id + '/' + asset_appendix


class InvocableElement(Element):
    __metaclass__ = abc.ABC

    @route('/<element_id>', methods=['POST'])
    @consumes('application/json')
    def invoke_runtime(self, element_id: str):
        # TODO: parse invocation graph
        # payload = request.json
        print(request.json)
        resp = {'element_id': element_id, 'status': 'sucess'}
        return make_response(jsonify(resp), 200)

    @route('/<element_id>/configurations', methods=['POST'])
    @consumes('application/json')
    def fetch_configurations(self, element_id: str):
        # payload = request.json
        resp = {'element_id': element_id, 'config': 'sucess'}
        return make_response(jsonify(resp), 200)

    @route('/<element_id>/output', methods=['POST'])
    @consumes('application/json')
    def fetch_output_configurations(self, element_id: str):
        # payload = request.json
        resp = {'element_id': element_id, 'output': 'sucess'}
        return make_response(jsonify(resp), 200)

    @route('/<element_id>/<running_instance_id>', methods=['DELETE'])
    def detach(self, element_id: str, running_instance_id: str):
        resp = {'element_id': element_id, 'running_instance_id': running_instance_id}
        return make_response(jsonify(resp), 200)

    @abc.abstractmethod
    def get_element_declarers(self):
        raise NotImplementedError()

    @abc.abstractmethod
    def get_instance_id(self, uri: str, element_id: str):
        raise NotImplementedError()

    @abc.abstractmethod
    def get_extractor(self, graph):
        raise NotImplementedError()

    @abc.abstractmethod
    def create_grounding_debug_information(self, graph):
        raise NotImplementedError()

    @staticmethod
    def _is_debug():
        return True if os.getenv('SP_DEBUG') == 'true' else False
