/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

import { ConnectUtils } from '../../support/utils/ConnectUtils';
import { SpecificAdapterBuilder } from '../../support/builder/SpecificAdapterBuilder';

describe('Test Edit Adapter', () => {
  beforeEach('Setup Test', () => {
    // To set up test add a stream adapter that can be configured
    cy.initStreamPipesTest();
    const adapterInput = SpecificAdapterBuilder
      .create('Machine_Data_Simulator')
      .setName('Machine Data Simulator Test')
      .addInput('input', 'wait-time-ms', '1000')
      .build();

    ConnectUtils.testSpecificStreamAdapter(adapterInput);
  });

  it('Perform Test', () => {
    // TODO check that edit button is deactivated while adapter is running

    // TODO stop adapter

    // TODO Click edit adapter

    // TODO Change config 'wait-time-ms' to 2000

    // TODO Save changes

    // TODO Start adapter

    // TODO Validate that events are coming

  });

  // Different cases
  // TODO Specific adapter
  // TODO Generic adapter
  // TODO Set adapter
  // TODO Stream adapter

});

