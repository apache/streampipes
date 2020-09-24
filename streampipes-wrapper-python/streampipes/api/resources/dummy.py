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
from flask import request
from flask_classful import FlaskView, route
from streampipes.manager import ProcessorDispatcher


# TODO: Delete when finished
# will be deleted and is only necessary for interims working example still relying on Java
class DummyInterimsResource(FlaskView):

    @route('/invoke', methods=['POST'])
    def start(self):
        return ProcessorDispatcher.start(**request.get_json())

    @route('/detach', methods=['POST'])
    def stop(self):
        return ProcessorDispatcher.stop(**request.get_json())