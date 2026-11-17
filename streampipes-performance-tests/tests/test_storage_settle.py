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

"""The optional storage settle step waits for flushed caches and finished compactions."""

from types import SimpleNamespace

import pytest
from test_framework import Clock

from streampipes_performance.cases import common
from streampipes_performance.config import Timing
from streampipes_performance.runtime import EvaluationError


def metrics_text(cache, queued, done, files):
    return "\n".join([
        "# TYPE storage_cache_inuse_bytes gauge",
        f'storage_cache_inuse_bytes{{bucket="b",id="1"}} {cache}',
        f'storage_cache_inuse_bytes{{bucket="b",id="2"}} 0',
        f'storage_compactions_queued{{bucket="b",id="1",level="1"}} {queued}',
        f'storage_compactions_duration_seconds_count{{bucket="b",id="1",level="full"}} {done}',
        f'storage_compactions_duration_seconds_sum{{bucket="b",id="1",level="full"}} 12.5',
        f'storage_tsm_files_total{{bucket="b",id="1"}} {files}',
        f'storage_tsm_files_total{{bucket="b",id="2"}} 1',
        "",
    ])


def context(clock, **timing):
    logged = []
    return SimpleNamespace(
        timing=Timing(**timing), clock=clock, phase="setup", samples=[],
        log=lambda message, *args, **kwargs: logged.append(message % args), logged=logged,
    )


def test_settle_is_skipped_without_metrics_url(monkeypatch):
    monkeypatch.delenv(common.STORAGE_METRICS_ENV, raising=False)
    ctx = context(Clock())
    assert common.wait_for_storage_settled(ctx) is None
    assert ctx.samples == []


def test_settle_waits_for_cache_flush_and_quiet_compactions(monkeypatch):
    monkeypatch.setenv(common.STORAGE_METRICS_ENV, "http://metrics")
    # Cache draining, then a compaction completes, then nothing changes any more (a stale queued gauge is ignored).
    sequence = iter([(500000, 0, 3, 8), (0, 1, 3, 8), (0, 1, 4, 2)])
    last = [None]

    def fake_metrics(url, timeout):
        assert url == "http://metrics" and timeout == 30
        last[0] = next(sequence, last[0])
        return common.storage_metrics_from_text(metrics_text(*last[0]))

    monkeypatch.setattr(common, "storage_metrics", fake_metrics)
    clock = Clock()
    ctx = context(clock, settle_stable_seconds=10, settle_timeout_seconds=100)
    totals = common.wait_for_storage_settled(ctx)
    assert totals["tsm_files"] == 3 and totals["cache_bytes"] == 0
    assert 10 <= clock.now < 100
    sample = ctx.samples[-1]
    assert sample["operation"] == "storage.settled" and sample["success"] and sample["kind"] == "readiness"
    assert any(message.startswith("Storage settled after") for message in ctx.logged)


def test_settle_times_out_while_cache_is_not_empty(monkeypatch):
    monkeypatch.setenv(common.STORAGE_METRICS_ENV, "http://metrics")
    monkeypatch.setattr(common, "storage_metrics",
                        lambda url, timeout: common.storage_metrics_from_text(metrics_text(1024, 0, 1, 1)))
    ctx = context(Clock(), settle_stable_seconds=5, settle_timeout_seconds=20)
    with pytest.raises(EvaluationError, match="storage_settle_timeout: cache_bytes=1024, compactions_done=1"):
        common.wait_for_storage_settled(ctx)
    assert ctx.samples[-1]["operation"] == "storage.settled" and not ctx.samples[-1]["success"]


def test_metrics_parser_sums_gauges_across_shards():
    totals = common.storage_metrics_from_text(metrics_text(7, 2, 5, 4))
    assert totals == {"cache_bytes": 7, "compactions_queued": 2, "compactions_done": 5, "tsm_files": 5}
