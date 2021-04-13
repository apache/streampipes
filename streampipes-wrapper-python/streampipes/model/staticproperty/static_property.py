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
import sys, os
sys.path.append(os.path.abspath("streampipes-wrapper-python"))

from streampipes.model.base.unnamed_streampipes_entity import UnnamedStreamPipesEntity 

class StaticProperty(UnnamedStreamPipesEntity):
  __serialVersionUID = 2509153122084646025
  
  
  def __init__(self):
      self.__set_StaticPropertyType(staticPropertyType)

  def get_StaticPropertyType(self):
    return staticPropertyType

  def __set_StaticPropertyType(self, staticPropertyType):
    self.staticPropertyType = staticPropertyType
