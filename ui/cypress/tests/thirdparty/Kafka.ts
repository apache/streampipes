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

import { GenericAdapterBuilder } from '../../support/builder/GenericAdapterBuilder';
import { PipelineElementBuilder } from '../../support/builder/PipelineElementBuilder';
import { ThirdPartyIntegrationUtils } from '../../support/utils/ThirdPartyIntegrationUtils';
import { PipelineElementInput } from '../../support/model/PipelineElementInput';

describe('Test Kafka Integration', () => {
  before('Setup Test', () => {
    cy.initStreamPipesTest();
  });

  it('Perform Test', () => {
    const topicName = 'cypresstopic';

    const sink: PipelineElementInput = PipelineElementBuilder.create('kafka_publisher')
      .addInput('select', 'Unauthenticated', 'check')
      .addInput('input', 'host', 'localhost')
      .addInput('input', 'port', '{backspace}{backspace}{backspace}{backspace}9094')
      .addInput('input', 'topic', topicName)
      .build();

    const adapter = GenericAdapterBuilder
      .create('Apache_Kafka')
      .setName('Kafka4')
      .setTimestampProperty('timestamp')
      .addProtocolInput('select', 'Unauthenticated', 'check')
      .addProtocolInput('input', 'host', 'localhost')
      .addProtocolInput('input', 'port', '9094')
      .addProtocolInput('click', 'sp-reload', '')
      .addProtocolInput('select', topicName, 'check')
      .setFormat('json_object')
      .build();

    ThirdPartyIntegrationUtils.runTest(sink, adapter);
  });

});
