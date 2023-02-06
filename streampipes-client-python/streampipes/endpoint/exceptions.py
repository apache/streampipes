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

"""
Custom exceptions dedicated for the endpoints module
"""

__all__ = [
    "MessagingEndpointNotConfiguredError",
]


class MessagingEndpointNotConfiguredError(Exception):
    """Exception that indicates that an instance of a messaging endpoint has not been configured.

    This error occurs when an instance of a messaging endpoint is used before
    the broker instance to be used is configured by passing it to the `configure()` method.

    Parameters
    ----------
    endpoint_name: str
        The name of the endpoint that caused the error

    """

    def __init__(
        self,
        endpoint_name: str,
    ):
        super().__init__(
            f"\nIt looks like the endpoint used is not configured properly.\n"
            f"This error occurs because the endpoint `{endpoint_name}` is a messaging endpoint,\n"
            f"which always require first of all the passing of the "
            f"broker instance to be used with the `configure()`method.\n"
            f"One can easily overcome this error by entering the following command before proceeding:\n"
            f"\n `client.{endpoint_name}.configure(broker=broker)`\n\n"
            f"The variable `broker` hereby needs to be an instance of a StreamPipes broker."
        )
