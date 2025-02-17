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
import { ConnectEventSchemaUtils } from '../../support/utils/connect/ConnectEventSchemaUtils';

describe('Test Edit Adapter', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
    });

    it('Test configuration of adapter fields ', () => {
        // Set up new adapter
        const builder = AdapterBuilder.create('Machine_Data_Simulator')
            .setName('Machine_Data_Simulator')
            .addInput('input', 'wait-time-ms', '1000');
        const configuration = builder.build();
        ConnectUtils.goToConnect();
        ConnectUtils.goToNewAdapterPage();
        ConnectUtils.selectAdapter(configuration.adapterType);
        cy.contains('Next').click();

        // Add new property and edit field
        cy.dataCy('connect-add-static-property').click();
        cy.dataCy('connect-add-field-name').type('test-property-1');
        cy.dataCy('connect-add-field-name-button').click();
        cy.dataCy('edit-density').click();
        // Change runtime name
        ConnectBtns.runtimeNameInput().clear().type('test-density');
        // Change field semantic type
        cy.get('[id="domainproperty"]')
            .clear()
            .type('http://schema.org/Numbers');
        // Change field data type
        ConnectBtns.changeRuntimeType()
            .click()
            .get('mat-option')
            .contains('Double')
            .click();
        // Provide correction value and operator
        cy.dataCy('connect-schema-correction-value').type('2');
        cy.dataCy('connect-schema-correction-operator')
            .click()
            .get('mat-option')
            .contains('Multiply')
            .click();
        cy.dataCy('sp-save-edit-property').click();
        cy.dataCy('sp-event-schema-next-button').click();

        // Fill in adapter name and close view
        cy.dataCy('sp-adapter-name').type('Test Adapter');
        cy.dataCy('adapter-settings-start-adapter-btn').click();
        ConnectUtils.closeAdapterPreview();

        // Edit adapter and check if given values and added property still provided
        ConnectBtns.editAdapter().should('not.be.disabled');
        ConnectBtns.editAdapter().click();
        cy.contains('Next').click();
        cy.dataCy('edit-density').click();
        ConnectEventSchemaUtils.validateRuntimeName('test-density');

        cy.get('[id="domainproperty"]').should(
            'have.value',
            'http://schema.org/Numbers',
        );
        ConnectBtns.changeRuntimeType().should('include.text', 'Double');
        cy.dataCy('connect-schema-correction-value').should('have.value', '2');
        cy.dataCy('connect-schema-correction-operator').should(
            'include.text',
            'Multiply',
        );

        // Delete inserted values in edit field
        ConnectBtns.runtimeNameInput().clear();
        cy.get('[id="domainproperty"]').clear();
        ConnectBtns.changeRuntimeType()
            .click()
            .get('mat-option')
            .contains('Float')
            .click();
        cy.dataCy('connect-schema-correction-value').clear();
        cy.dataCy('sp-save-edit-property').click();
        cy.get('[class="general-panel"]').should(
            'include.text',
            'test-property-1',
        );
        cy.dataCy('sp-event-schema-next-button').click();
        cy.dataCy('store-edit-adapter').click();
        ConnectUtils.closeAdapterPreview();

        // Configure adapter with pressure instead of flowrate
        ConnectBtns.editAdapter().click();
        const newUserConfiguration = AdapterBuilder.create(
            'Machine_Data_Simulator',
        )
            .addInput('input', 'wait-time-ms', '2000')
            .addInput('radio', 'selected', 'simulator-option-pressure')
            .build();
        ConnectUtils.configureAdapter(newUserConfiguration);

        // Check if given property still exists and close view afterwards
        cy.get('[class="general-panel"]').should(
            'include.text',
            'test-property-1',
        );
        cy.dataCy('sp-event-schema-next-button').click();
        cy.dataCy('store-edit-adapter').click();
        ConnectUtils.closeAdapterPreview();
    });
});
