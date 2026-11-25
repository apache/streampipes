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

# Adapter start/stop

Create running simulator adapters, then repeatedly stop and start all adapters.

Configure this case in [`run_configs/adapter-start-stop.json`](../../../../run_configs/adapter-start-stop.json). Arrays specify alternatives; their Cartesian product produces independent scenarios. Only the parameters below are accepted. The run configuration controls timeouts, cleanup and repetitions.

## Parameters

- `number_of_adapters`: Number of adapters.
- `wait_time_ms`: Simulator event interval in ms; events/second = 1000 / wait_time_ms.
- `cycles`: Number of complete stop/start cycles. Each cycle stops all adapters, waits one second, starts all adapters, and waits one second.

Adapters always run with scripting disabled; this case measures lifecycle operations.

## Metrics

- `operations.Adapter start time`: Start API request duration, including client processing, in ms.
- `operations.Adapter stop time`: Stop API request duration, including client processing, in ms.
- `Adapter start time (measurement readiness)`: Start request through observed running state, in ms.
- `Adapter stop time (measurement readiness)`: Stop request through observed stopped state, in ms.

All created resources are tracked for cleanup, including partial setup failures.

## Code layout

[`case.py`](case.py) declares all accepted parameters, documentation and fixed constants, then shows the ordered setup, measurement and result preparation steps. Shared helpers handle technical operations; this case owns its schemas, generated data and workload choices.
