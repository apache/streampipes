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
from typing import List

from streampipes.functions.streampipes_function import StreamPipesFunction


class Registration:
    """Manages the existing StreamPipesFunctions and registers them.

    Attributes
    ----------
    functions: List[StreamPipesFunction]
        List of all registered StreamPipesFunction

    """

    def __init__(self) -> None:
        self.functions: List[StreamPipesFunction] = []

    def register(self, streampipes_function: StreamPipesFunction):
        """Registers a new function.

        Parameters
        ----------
        streampipes_function: StreamPipesFunction
            The function to register.

        Returns
        -------
        self: Registration
            The updated Registration instance
        """
        self.functions.append(streampipes_function)  # TODO register function to AdminAPI + consul
        return self

    def getFunctions(self) -> List[StreamPipesFunction]:
        """Get all registered functions.

        This method exists to be consistent with the Java client.

        Returns
        -------
        functions: List[StreamPipesFunction]
            List of all registered functions.
        """
        return self.functions
