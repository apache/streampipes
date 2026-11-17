<!--
  ~ Licensed to the Apache Software Foundation (ASF) under one or more
  ~ contributor license agreements.  See the NOTICE file distributed with
  ~ this work for additional information regarding copyright ownership.
  ~ The ASF licenses this file to You under the Apache License, Version 2.0
  ~ (the "License"); you may not use this file except in compliance with
  ~ the License.  You may obtain a copy of the License at
  ~
  ~    http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  ~
-->

# Pipeline start/stop

Create one simulator source and storage pipeline per resource; cycle the pipelines while sources remain running.

Configure this case in [`run_configs/pipeline-start-stop.json`](../../../../run_configs/pipeline-start-stop.json). Arrays specify alternatives; their Cartesian product produces independent scenarios. Only the parameters below are accepted. The run configuration controls timeouts, cleanup and repetitions.

## Parameters

- `number_of_pipelines`: Number of pipelines, each with a dedicated simulator adapter.
- `cycles`: Number of complete stop/start cycles, with a fixed one-second interval after each transition.
- `wait_time_ms`: Source event interval in ms; events/second per pipeline = 1000 / wait_time_ms.

## Metrics

- `operations.Pipeline start time`: Start API request duration in ms.
- `operations.Pipeline stop time`: Stop API request duration in ms.
- `Pipeline start time (measurement readiness)`: Start request through observed running state, in ms.
- `Pipeline stop time (measurement readiness)`: Stop request through observed stopped state, in ms.

All created resources are tracked for cleanup, including partial setup failures.

## Code layout

[`case.py`](case.py) declares all accepted parameters, documentation, workload presets and fixed constants, then shows the ordered setup, measurement and result preparation steps. Shared helpers handle technical operations; this case owns its schemas, generated data and workload choices.
