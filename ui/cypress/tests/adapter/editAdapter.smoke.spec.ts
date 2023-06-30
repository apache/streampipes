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

import { ConnectUtils } from '../../support/utils/connect/ConnectUtils';
import { ConnectBtns } from '../../support/utils/connect/ConnectBtns';
import { AdapterBuilder } from '../../support/builder/AdapterBuilder';

describe('Test Edit Adapter', () => {
    beforeEach('Setup Test', () => {
        // To set up test add a stream adapter that can be configured
        cy.initStreamPipesTest();
        ConnectUtils.addMachineDataSimulator('simulator');
    });

    it('Successfully edit adapter', () => {
        const newAdapterName = 'Edited Adapter';

        ConnectUtils.goToConnect();

        // check that edit button is deactivated while adapter is running
        ConnectBtns.editAdapter().should('be.disabled');

        // stop adapter
        ConnectBtns.stopAdapter().click();

        // click edit adapter
        ConnectBtns.editAdapter().should('not.be.disabled');
        ConnectBtns.editAdapter().click();

        const newUserConfiguration = AdapterBuilder.create(
            'Machine_Data_Simulator',
        )
            // .setName(name)
            .addInput('input', 'wait-time-ms', '2000')
            .addInput('radio', 'selected', 'simulator-option-pressure')
            .build();

        ConnectUtils.configureAdapter(newUserConfiguration);

        cy.get('.schema-validation-text-warning').contains('Edit mode');

        // Update event schema
        ConnectBtns.refreshSchema().click();

        cy.dataCy('schema-validation-ok').should('not.exist');

        ConnectUtils.finishEventSchemaConfiguration();

        cy.dataCy('sp-adapter-name').clear().type(newAdapterName);

        ConnectBtns.storeEditAdapter().click();

        cy.dataCy('info-adapter-successfully-updated', {
            timeout: 60000,
        }).should('be.visible');

        ConnectUtils.closeAdapterPreview();

        ConnectUtils.startAndValidateAdapter(3);

        // Validate that name of adapter and data source
        cy.dataCy('adapter-name').contains(newAdapterName);
        cy.get('.sp-dialog-content').contains(newAdapterName);
    });
});
