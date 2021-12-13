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
import { SpecificAdapterBuilder } from '../../support/builder/SpecificAdapterBuilder';

describe('Test Random Data Simulator Stream Adapter', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
    });

    it('Perform Test', () => {

        const adapterConfiguration = SpecificAdapterBuilder
            .create('Machine_Data_Simulator')
            .setName('Machine Data Simulator Test')
            .addInput('input', 'wait-time-ms', '1000')
            .build();


        AdapterUtils.goToConnect();

        AdapterUtils.selectAdapter(adapterConfiguration.adapterType);

        AdapterUtils.configureAdapter(adapterConfiguration.adapterConfiguration);

        // wait till schema is shown

       cy.dataCy('sp-connect-schema-editor', { timeout: 60000 }).should('be.visible');

        // Add fixed property

        // Click add a static value to event
        cy.dataCy('connect-add-static-property', { timeout: 10000 }).click();

        // Edit new property
        const propertyName = 'staticPropertyName';
        const propertyValue = 'id1';
        cy.dataCy('edit-key_0', { timeout: 10000 }).click();

        cy.dataCy('connect-edit-field-runtime-name', { timeout: 10000 })
            .type('{backspace}{backspace}{backspace}{backspace}{backspace}' + propertyName);
        cy.dataCy('connect-edit-field-static-value', { timeout: 10000 }).type(propertyValue);

        cy.dataCy('sp-save-edit-property').click();

        // validate that static value is persisted
        cy.dataCy('edit-' + propertyName, { timeout: 10000 }).click();
        cy.dataCy('connect-edit-field-static-value', { timeout: 10000 }).should('have.value', propertyValue);
        cy.dataCy('sp-save-edit-property').click();

        AdapterUtils.finishEventSchemaConfiguration();

        AdapterUtils.startAdapter(adapterConfiguration, 'sp-connect-adapter-live-preview');

        // Add timestamp
        // Delete
        // Change data type
    });

});
