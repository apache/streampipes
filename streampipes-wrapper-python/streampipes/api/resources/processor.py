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
from streampipes.api.resources.base import InvocableElement


# TODO: implement
class SepaElementResource(InvocableElement):

    def get_instance_id(self, uri: str, element_id: str):
        pass

    def get_element_declarers(self):
        pass

    def get_extractor(self, graph):
        pass

    def create_grounding_debug_information(self, graph):
        pass

