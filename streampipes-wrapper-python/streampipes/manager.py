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
"""Manages processor life cycle"""
import logging
import abc
from streampipes.declarer import DeclarerSingleton


class ProcessorDispatcher(object):
    __metaclass__ = abc.ABC

    _running_instances = {}

    logger = logging.getLogger(__name__)

    @classmethod
    def start(cls, **kwargs):
        processor_id = kwargs.get('processor_id')
        try:
            processor = DeclarerSingleton().get_processor(processor_id)(**kwargs)
            processor.init()
            cls._running_instances[processor.invocation_id] = processor

            return {'status': 'success'}

        except KeyError:
            err = "KeyError. processor_id not found"
            cls.logger.info('{}: {}'.format(err, processor_id))
            return {'status': err}

    @classmethod
    def stop(cls, **kwargs):
        invocation_id = kwargs.get('invocation_id')
        try:
            processor = cls._running_instances[invocation_id]
            active_threads = processor.active_threads()
            processor.stop()
            active_threads['kafka'].join()

            del processor
            cls._running_instances.pop(invocation_id)

            return {'status': 'success'}

        except KeyError:
            err = "KeyError. invocation_id not found"
            cls.logger.info('{}: {}'.format(err,invocation_id))

            return {'status': err}
