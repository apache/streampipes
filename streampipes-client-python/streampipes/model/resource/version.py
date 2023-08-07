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

__all__ = [
    "Version",
]

from typing import Dict

from pydantic import StrictStr

from streampipes.model.resource.resource import Resource


class Version(Resource):
    """Metadata about the version of the connected StreamPipes server.

    Attributes
    ----------
    backend_version: str
        version of the StreamPipes backend the client is connected to
    """

    def convert_to_pandas_representation(self) -> Dict:
        """Returns the dictionary representation of the version metadata
        to be used when creating a pandas Dataframe.
        """
        return self.to_dict(use_source_names=False)

    backend_version: StrictStr
