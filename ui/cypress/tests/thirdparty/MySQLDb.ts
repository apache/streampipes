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
import { PipelineElementBuilder } from '../../support/builder/PipelineElementBuilder';
import { ThirdPartyIntegrationUtils } from '../../support/utils/ThirdPartyIntegrationUtils';
import { PipelineElementInput } from '../../support/model/PipelineElementInput';

describe('Test MySQL Integration', () => {
  before('Setup Test', () => {
    cy.initStreamPipesTest();
  });

  it('Perform Test', () => {
    const dbName = 'cypressDatabase';

    const sink: PipelineElementInput = PipelineElementBuilder.create('mysql_database')
      .addInput('input', 'host', 'localhost')
      .addInput('input', 'user', 'root')
      .addInput('input', 'password', '7uc4rAymrPhxv6a5')
      .addInput('input', 'db', 'sp')
      .addInput('input', 'table', dbName)
      .build();

    const adapter = SpecificAdapterBuilder
      .create('MySql_Stream_Adapter')
      .setName('MySQL Adapter')
      .setTimestampProperty('timestamp')
      .addInput('input', 'mysqlHost', 'localhost')
      .addInput('input', 'mysqlUser', 'root')
      .addInput('input', 'mysqlPassword', '7uc4rAymrPhxv6a5')
      .addInput('input', 'mysqlDatabase', 'sp')
      .addInput('input', 'mysqlTable', dbName)
      .build();

    ThirdPartyIntegrationUtils.runTest(sink, adapter);
  });

});
