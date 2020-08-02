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
from flask import Blueprint, make_response, jsonify, request

welcome_blueprint = Blueprint('welcome', __name__)


@welcome_blueprint.route('/', methods=['GET'])
def get_welcome_page_html():

    if request.content_type == 'application/json':
        resp = {'welcome': 'hello-world!'}
        return make_response(jsonify(resp), 200)

    elif request.content_type == 'text/html':
        return "<p>Got it!</p>"

