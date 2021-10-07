/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import { SpecificAdapterBuilder } from '../../support/builder/SpecificAdapterBuilder';
import { PipelineBuilder } from '../../support/builder/PipelineBuilder';
import { PipelineElementBuilder } from '../../support/builder/PipelineElementBuilder';
import { AdapterUtils } from '../../support/utils/AdapterUtils';
import { PipelineUtils } from '../../support/utils/PipelineUtils';

describe('Test Random Data Simulator Stream Adapter', () => {
  before('Setup Test', () => {
    cy.initStreamPipesTest();
  });

  it('Perform Test', () => {
    const simulatorAdapterName = 'simulator';

    const machineAdapter = SpecificAdapterBuilder
      .create('Machine_Data_Simulator')
      .setName(simulatorAdapterName)
      .addInput('input', 'wait-time-ms', '1000')
      .build();

    AdapterUtils.testSpecificStreamAdapter(machineAdapter);

    const topicname = 'cypresstopic';
    const pipelineInput = PipelineBuilder.create('Pipeline Test')
      .addSource(simulatorAdapterName)
      .addSink(
        PipelineElementBuilder.create('influxdb')
          .addInput('input', 'db_host', 'http://localhost')
          .addInput('input', 'db_name', 'sp')
          .addInput('input', 'db_measurement', topicname)
          .addInput('input', 'db_user', 'sp')
          .addInput('input', 'db_password', 'default')
          .addInput('input', 'batch_interval_actions', '2')
          .addInput('input', 'max_flush_duration', '{backspace}{backspace}{backspace}{backspace}500')
          .addInput('drop-down', 'timestamp_mapping', 'timestamp')
          .build())
      .build();

    PipelineUtils.testPipeline(pipelineInput);

    const adapterInput = SpecificAdapterBuilder
      .create('InfluxDB_Stream_Adapter')
      .setName('InfluxDB Adapter')
      .setTimestampProperty('time')
      .setStoreInDataLake()
      .addInput('input', 'influxDbHost', 'http://localhost')
      .addInput('input', 'influxDbPort', '8086')
      .addInput('input', 'influxDbDatabase', 'sp')
      .addInput('input', 'influxDbMeasurement', topicname)
      .addInput('input', 'influxDbUsername', 'sp')
      .addInput('input', 'influxDbPassword', 'default')
      .addInput('input', 'pollingInterval', '200')
      .build();

    AdapterUtils.testSpecificStreamAdapter(adapterInput);
  });

});
