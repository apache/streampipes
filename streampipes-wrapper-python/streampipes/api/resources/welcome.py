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
from flask import make_response, jsonify, request, render_template
from flask_classful import FlaskView, route
from flask_negotiate import produces


class WelcomeResource(FlaskView):
    dummy_processors = [{
            'name': 'Greeter Python',
            'uri': 'http://localhost:5000/sepa/org.apache.streampipes.python.processor.greeter',
            'description': 'Greeter Python Description'
        },
        {
            'name': 'DoNothing Python',
            'uri': 'http://localhost:5000/sepa/org.apache.streampipes.python.processor.donothing',
            'description': 'Donothing Python Description'
        }]

    @route('/', methods=['GET'])
    @produces('application/json','text/html')
    def welcome(self):
        # TODO: get description of all declared semantic event processor agents (sepa)
        # TODO: DeclarerSingleton().get_declarers()
        if request.accept_mimetypes['text/html']:
            return render_template('index.html', processors=self.dummy_processors)

        if request.accept_mimetypes['application/json']:
            resp = {'success': True}
            return make_response(jsonify(resp), 200)

