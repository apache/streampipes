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
import json


class ConfigItem(object):
    def __init__(self):
        self.value = None
        self.value_type = None
        self.description = None
        self.configuration_scope = None
        self.is_password = None

    def to_json(self):
        d = {}
        for k,v in self.__dict__.items():
            elements = k.split('_')
            camel_case = elements[0] + ''.join(x.title() for x in elements[1:])
            d[camel_case] = v

        return json.dumps(d)
