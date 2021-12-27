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
import { FileManagementUtils } from '../../support/utils/FileManagementUtils';
import { GenericAdapterBuilder } from '../../support/builder/GenericAdapterBuilder';
import { DataLakeUtils } from '../../support/utils/DataLakeUtils';

describe('Test Random Data Simulator Stream Adapter', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
        FileManagementUtils.addFile('connect/schemaRules/input.csv');
    });

    it('Perform Test', () => {

        const adapterConfiguration = GenericAdapterBuilder
            .create('File_Set')
            .setStoreInDataLake()
            .setTimestampProperty('timestamp')
            .setName('Adapter to test schema rules')
            .setFormat('csv')
            .addFormatInput('input', 'delimiter', ';')
            .addFormatInput('checkbox', 'header', 'check')
            .build();


        AdapterUtils.goToConnect();
        AdapterUtils.selectAdapter(adapterConfiguration.adapterType);
        AdapterUtils.configureAdapter(adapterConfiguration.protocolConfiguration);
        AdapterUtils.configureFormat(adapterConfiguration);

        // wait till schema is shown
       cy.dataCy('sp-connect-schema-editor', { timeout: 60000 }).should('be.visible');

        // Add static value to event

        // TODO FIX breaks adapter
        // Click add a static value to event
        // cy.dataCy('connect-add-static-property', { timeout: 10000 }).click();
        //
        // // Edit new property
        // const propertyName = 'staticPropertyName';
        // const propertyValue = 'id1';
        // cy.dataCy('edit-key_0', { timeout: 10000 }).click();
        //
        // cy.dataCy('connect-edit-field-runtime-name', { timeout: 10000 })
        //     .type('{backspace}{backspace}{backspace}{backspace}{backspace}' + propertyName);
        // cy.dataCy('connect-edit-field-static-value', { timeout: 10000 }).type(propertyValue);
        //
        // cy.dataCy('sp-save-edit-property').click();
        //
        // // validate that static value is persisted
        // cy.dataCy('edit-' + propertyName.toLowerCase(), { timeout: 10000 }).click();
        // cy.dataCy('connect-edit-field-static-value', { timeout: 10000 }).should('have.value', propertyValue);
        // cy.dataCy('sp-save-edit-property').click();

        // Delete property
        cy.dataCy('delete-property-density', { timeout: 10000 }).children().click({ force: true });
        cy.dataCy('connect-schema-delete-properties-btn', { timeout: 10000 }).click();

        // TODO FIX breaks adapter
        // Change data type
        // cy.dataCy('edit-temperature', { timeout: 10000 }).click();
        // cy.dataCy('connect-change-runtime-type').click().get('mat-option').contains('Float').click();
        // cy.dataCy('sp-save-edit-property').click();
        // // validate that static value is persisted
        // cy.dataCy('edit-temperature', { timeout: 10000 }).click({ force: true });
        // cy.dataCy('connect-change-runtime-type', { timeout: 10000 }).contains('Float');
        // cy.dataCy('sp-save-edit-property').click();

        // Add timestamp
        AdapterUtils.eventSchemaNextBtnDisabled();
        cy.dataCy('connect-schema-add-timestamp-btn', { timeout: 10000 }).click();
        AdapterUtils.eventSchemaNextBtnEnabled();

        AdapterUtils.finishEventSchemaConfiguration();

        AdapterUtils.startSetAdapter(adapterConfiguration);

        // Wait till data is stored
        cy.wait(10000);

        DataLakeUtils.checkResults(
            'adaptertotestschemarules',
            'cypress/fixtures/connect/schemaRules/expected.csv',
            true);
    });

});
