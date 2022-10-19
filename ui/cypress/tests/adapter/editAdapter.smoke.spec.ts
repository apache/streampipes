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

import { SpecificAdapterBuilder } from '../../support/builder/SpecificAdapterBuilder';
import { UserInputBuilder } from '../../support/builder/UserInputBuilder';
import { ConnectBtns } from '../../support/utils/connect/ConnectBtns';
import { ConnectUtils } from '../../support/utils/connect/ConnectUtils';

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
    ConnectUtils.goToConnect();

    // check that edit button is deactivated while adapter is running
    ConnectBtns.editAdapter().should('be.disabled');

    // stop adapter
    ConnectBtns.stopAdapter().click();

    // click edit adapter
    ConnectBtns.editAdapter().should('not.be.disabled');
    ConnectBtns.editAdapter().click();

    // Change config 'wait-time-ms' to 2000 and save adapter
    const newUserConfiguration = UserInputBuilder
      .create()
      .add('input', 'wait-time-ms', '2000')
      .build();
    ConnectUtils.configureAdapter(newUserConfiguration);

    ConnectUtils.finishEventSchemaConfiguration();

    ConnectBtns.storeEditAdapter().click();

    cy.dataCy('info-adapter-successfully-updated', {timeout: 60000}).should('be.visible');

    ConnectUtils.closeAdapterPreview();

    // Start Adapter
    ConnectBtns.startAdapter().should('not.be.disabled');
    ConnectBtns.startAdapter().click();

    // View data
    ConnectBtns.infoAdapter().click();
    cy.get('div').contains('Values').parent().click();

    cy.dataCy('sp-connect-adapter-live-preview', {timeout: 10000}).should('be.visible');
  });

});

