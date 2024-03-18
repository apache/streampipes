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
import { StaticPropertyUtils } from '../../support/utils/StaticPropertyUtils';
import { AdapterBuilder } from '../../support/builder/AdapterBuilder';

describe('Test Edit Adapter', () => {
    beforeEach('Setup Test', () => {
        // To set up test add a stream adapter that can be configured
        cy.initStreamPipesTest();
        ConnectUtils.goToConnect();
        ConnectUtils.goToNewAdapterPage();
    });

    it('Successfully edit adapter', () => {
        const WAIT_TIME_MS = 'wait-time-ms';
        const SIMULATOR_OPTION_PRESSURE = 'simulator-option-pressure';
        const TEMPLATE_NAME = 'Test Template';

        // configure adapter template
        const adapterTemplateInput = AdapterBuilder.create(
            'Machine_Data_Simulator',
        )
            .addInput('input', WAIT_TIME_MS, '3000')
            .addInput('radio', 'selected', SIMULATOR_OPTION_PRESSURE)
            .build();
        ConnectUtils.selectAdapter(adapterTemplateInput.adapterType);
        StaticPropertyUtils.input(adapterTemplateInput.adapterConfiguration);

        // store adapter template
        cy.dataCy('connect-use-template').should('not.exist');
        cy.dataCy('store-config-as-template').click();
        cy.dataCy('template-name').type(TEMPLATE_NAME);
        cy.dataCy('template-description').type('Test Description');
        cy.dataCy('create-template').click();
        cy.dataCy('connect-new-adapter-cancel').click();

        // Reload template configuration
        ConnectUtils.goToNewAdapterPage();
        ConnectUtils.selectAdapter(adapterTemplateInput.adapterType);
        cy.dataCy('connect-use-template').should('exist');
        cy.dataCy('connect-use-template').click({ force: true });
        cy.get('mat-option').contains('Test Template').click();

        // validate that the template values are selected
        cy.dataCy(WAIT_TIME_MS).should('have.value', '3000');
        cy.dataCy('selected-' + SIMULATOR_OPTION_PRESSURE).should(
            'have.class',
            'mat-mdc-radio-checked',
        );

        // Delete the template
        cy.dataCy('store-config-as-template').click();
        cy.dataCy('delete-pipeline-element-template').should('have.length', 1);
        cy.dataCy('delete-pipeline-element-template').click();
        cy.dataCy('delete-pipeline-element-template').should('have.length', 0);
    });
});
