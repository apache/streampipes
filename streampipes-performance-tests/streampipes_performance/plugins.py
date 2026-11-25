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

"""Lazy registries filled by the built-in cases and by installed plugins.

A plugin is a Python package that declares an entry point in the group
``streampipes_performance.plugins``. The entry point names a module; importing it
registers the plugin's cases (``cases.base.register``), targets
(``targets.register_target``), operation labels and configuration directories.
Registries load the built-ins and every plugin on first read, so plugin modules
may import any framework module without circular-import concerns.
"""

import logging
from collections.abc import MutableMapping
from importlib import import_module
from importlib.metadata import entry_points
from pathlib import Path

ENTRY_POINT_GROUP = "streampipes_performance.plugins"
BUILTIN_MODULES = (
    "streampipes_performance.targets",
    "streampipes_performance.cases.streampipes.adapter_ingestion.case",
    "streampipes_performance.cases.streampipes.adapter_latency.case",
    "streampipes_performance.cases.streampipes.adapter_start_stop.case",
    "streampipes_performance.cases.streampipes.dataset_query.case",
    "streampipes_performance.cases.streampipes.pipeline_start_stop.case",
)

_state = {"loaded": False, "loading": False}
logger = logging.getLogger(__name__)


def load():
    """Import built-in modules and installed plugins once."""
    if _state["loaded"] or _state["loading"]:
        return
    _state["loading"] = True
    try:
        for name in BUILTIN_MODULES:
            import_module(name)
        for entry_point in sorted(entry_points(group=ENTRY_POINT_GROUP), key=lambda e: e.name):
            entry_point.load()
        _state["loaded"] = True
    finally:
        _state["loading"] = False


def plugin_names():
    return sorted(entry_point.name for entry_point in entry_points(group=ENTRY_POINT_GROUP))


class Registry(MutableMapping):
    """Mapping that loads plugins before its first lookup; item assignment is for tests."""

    def __init__(self, kind):
        self.kind = kind
        self._items = {}

    def add(self, name, value):
        if name in self._items:
            raise ValueError(f"Duplicate {self.kind} registration: {name}")
        self._items[name] = value
        return value

    def __getitem__(self, name):
        load()
        return self._items[name]

    def __iter__(self):
        load()
        return iter(self._items)

    def __len__(self):
        load()
        return len(self._items)

    def __setitem__(self, name, value):
        load()
        self._items[name] = value

    def __delitem__(self, name):
        load()
        del self._items[name]

    def __contains__(self, name):
        load()
        return name in self._items

    def __repr__(self):
        return f"Registry({self.kind}: {sorted(self._items)})"


OPERATION_LABELS = Registry("operation label")
CONFIG_DIRECTORIES = []


def register_operation_label(prefix, label):
    """Name an operation prefix in metric names, e.g. ``ml`` -> ``ML model``."""
    OPERATION_LABELS.add(prefix, label)


def register_config_directory(path):
    """Make a plugin's supplied run configurations discoverable by name."""
    path = Path(path)
    if path not in CONFIG_DIRECTORIES:
        CONFIG_DIRECTORIES.append(path)
