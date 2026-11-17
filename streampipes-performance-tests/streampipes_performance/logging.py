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

"""Console logging scoped to a CLI run, without changing caller logging permanently."""

import logging
from contextlib import contextmanager


@contextmanager
def console_logging(level="INFO"):
    """Emit framework progress to stderr and keep client HTTP logging quiet."""
    logger = logging.getLogger("streampipes_performance")
    client_logger = logging.getLogger("streampipes")
    previous = logger.level, logger.propagate, client_logger.level
    handler = logging.StreamHandler()
    handler.setFormatter(logging.Formatter("%(asctime)s %(levelname)s %(message)s", datefmt="%H:%M:%S"))
    logger.addHandler(handler)
    logger.setLevel(level)
    logger.propagate = False
    client_logger.setLevel(logging.WARNING)
    try:
        yield
    finally:
        logger.removeHandler(handler)
        handler.close()
        logger.setLevel(previous[0])
        logger.propagate = previous[1]
        client_logger.setLevel(previous[2])
