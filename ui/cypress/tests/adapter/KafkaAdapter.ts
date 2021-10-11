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

import { AdapterUtils } from '../../support/utils/AdapterUtils';
import { GenericAdapterBuilder } from '../../support/builder/GenericAdapterBuilder';
import { SpecificAdapterBuilder } from '../../support/builder/SpecificAdapterBuilder';
import { PipelineBuilder } from '../../support/builder/PipelineBuilder';
import { PipelineElementBuilder } from '../../support/builder/PipelineElementBuilder';
import { PipelineUtils } from '../../support/utils/PipelineUtils';

describe('Test Kafka Integration', () => {
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
        PipelineElementBuilder.create('kafka_publisher')
          .addInput('select', 'Unauthenticated', 'check')
          .addInput('input', 'host', 'localhost')
          .addInput('input', 'port', '{backspace}{backspace}{backspace}{backspace}9094')
          .addInput('input', 'topic', topicname)
          .build())
      .build();

    PipelineUtils.testPipeline(pipelineInput);

    const adapterInput = GenericAdapterBuilder
      .create('Apache_Kafka')
      .setName('Kafka4')
      .setTimestampProperty('timestamp')
      .addProtocolInput('select', 'Unauthenticated', 'check')
      .addProtocolInput('input', 'host', 'localhost')
      .addProtocolInput('input', 'port', '9094')
      .addProtocolInput('click', 'sp-reload', '')
      .addProtocolInput('select', topicname, 'check')
      .setFormat('json_object')
      .build();

    AdapterUtils.testGenericStreamAdapter(adapterInput);
  });

});
