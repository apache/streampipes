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

    AdapterUtils.addMachineDataSimulator(simulatorAdapterName);

    const topicname = 'cypresstopic';
    const pipelineInput = PipelineBuilder.create('Pipeline Test')
      .addSource(simulatorAdapterName)
      .addSink(
        PipelineElementBuilder.create('mysql_database')
          .addInput('input', 'host', 'localhost')
          .addInput('input', 'user', 'root')
          .addInput('input', 'password', '7uc4rAymrPhxv6a5')
          .addInput('input', 'db', 'sp')
          .addInput('input', 'table', topicname)
          .build())
      .build();

    PipelineUtils.testPipeline(pipelineInput);

    const adapterInput = SpecificAdapterBuilder
      .create('MySql_Stream_Adapter')
      .setName('MySQL Adapter')
      .setTimestampProperty('timestamp')
      .setStoreInDataLake()
      .addInput('input', 'mysqlHost', 'localhost')
      .addInput('input', 'mysqlUser', 'root')
      .addInput('input', 'mysqlPassword', '7uc4rAymrPhxv6a5')
      .addInput('input', 'mysqlDatabase', 'sp')
      .addInput('input', 'mysqlTable', topicname)
      .build();

    AdapterUtils.testSpecificStreamAdapter(adapterInput);
  });

});
