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

"""Deployment targets: which product a run configuration addresses and how it is cleaned up."""

from dataclasses import dataclass

from streampipes_performance.plugins import Registry

TARGETS = Registry("target")


@dataclass(frozen=True)
class Target:
    """A product a run can address.

    ``extends`` names the target whose cases also run here (a distribution built on
    StreamPipes extends ``streampipes``). ``apis`` returns additional resource APIs
    keyed by resource kind; the runner includes them in the empty-instance check and
    in delete cleanup. An API with ``stop_result`` is stopped together with adapters
    and deleted before pipelines; other APIs are deleted after streams, in reverse
    order. An API may define ``owns(identifier, prefix)`` to claim resources whose
    names do not start with the scenario prefix.
    """

    name: str
    display_name: str
    extends: str | None = None
    default_cleanup: str = "reset"
    supports_reset: bool = True

    def apis(self, client, scenarios):
        return {}

    def lineage(self):
        names, target = [], self
        while target is not None:
            names.append(target.name)
            target = TARGETS.get(target.extends) if target.extends else None
        return names

    def supports(self, case_class):
        return any(name in case_class.targets for name in self.lineage())

    def reset_unsupported(self):
        return ValueError(f"Reset cleanup is not supported for {self.display_name} resources; use delete")


def register_target(target):
    return TARGETS.add(target.name, target)


STREAMPIPES = register_target(Target("streampipes", "StreamPipes"))


def target(name):
    if name not in TARGETS:
        raise ValueError(f"Unknown target {name!r}; available: {', '.join(sorted(TARGETS))}")
    return TARGETS[name]
