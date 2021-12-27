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

import { ConnectUtils } from '../../support/utils/ConnectUtils';
import { FileManagementUtils } from '../../support/utils/FileManagementUtils';
import { GenericAdapterBuilder } from '../../support/builder/GenericAdapterBuilder';
import { ConnectEventSchemaUtils } from '../../support/utils/ConnectEventSchemaUtils';
import { DataLakeUtils } from '../../support/utils/DataLakeUtils';

describe('Connect schema rule transformations', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
        FileManagementUtils.addFile('connect/valueRules/input.csv');
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


        ConnectUtils.goToConnect();
        ConnectUtils.selectAdapter(adapterConfiguration.adapterType);
        ConnectUtils.configureAdapter(adapterConfiguration.protocolConfiguration);
        ConnectUtils.configureFormat(adapterConfiguration);

        // wait till schema is shown
        cy.dataCy('sp-connect-schema-editor', { timeout: 60000 }).should('be.visible');

        // Edit timestamp property
        let propertyName = 'timestamp';
        const timestampRegex = 'yyyy-MM-dd\'T\'HH:mm:ss.SSS\'Z\'';
        cy.dataCy('edit-' + propertyName.toLowerCase(), { timeout: 10000 }).click();
        cy.dataCy('sp-mark-as-timestamp').children().click();
        cy.dataCy('connect-timestamp-converter').click().get('mat-option').contains('String').click();
        cy.dataCy('connect-timestamp-string-regex').type(timestampRegex);

        cy.dataCy('sp-save-edit-property').click();

        cy.dataCy('edit-' + propertyName.toLowerCase(), { timeout: 10000 }).click({ force: true });
        cy.dataCy('connect-timestamp-string-regex', { timeout: 10000 }).should('have.value', timestampRegex);
        cy.dataCy('sp-save-edit-property').click();


        // Number transformation
        propertyName = 'value';
        const value = '10';
        cy.dataCy('edit-' + propertyName.toLowerCase(), { timeout: 10000 }).click();
        cy.dataCy('connect-schema-correction-value').type(value);
        cy.dataCy('connect-schema-correction-operator').click().get('mat-option').contains('Multiply').click();

        cy.dataCy('sp-save-edit-property').click();
        cy.dataCy('edit-' + propertyName.toLowerCase(), { timeout: 10000 }).click({ force: true });
        cy.dataCy('connect-schema-correction-value', { timeout: 10000 }).should('have.value', value);
        cy.dataCy('sp-save-edit-property').click();

        // Unit transformation
        propertyName = 'temperature';
        const fromUnit = '';
        const toUnit = '';
        cy.dataCy('edit-' + propertyName.toLowerCase(), { timeout: 10000 }).click();
        cy.dataCy('connect-schema-unit-from-dropdown').type('Degree Celsius');
        cy.dataCy('connect-schema-unit-transform-btn').click();
        cy.dataCy('connect-schema-unit-to-dropdown').click().get('mat-option').contains('Degree Fahrenheit').click();
        cy.dataCy('sp-save-edit-property').click();

        // TODO fix check when editing
        // cy.dataCy('edit-' + propertyName.toLowerCase(), { timeout: 10000 }).click({ force: true });
        // cy.dataCy('connect-schema-unit-from-input', { timeout: 10000 }).should('have.value', 'Degree Celsius');
        // cy.dataCy('connect-schema-unit-to-dropdown', { timeout: 10000 }).should('have.value', 'Degree Fahrenheit');
        // cy.dataCy('sp-save-edit-property').click();


        ConnectEventSchemaUtils.finishEventSchemaConfiguration();

        ConnectUtils.startSetAdapter(adapterConfiguration);

        // Wait till data is stored
        cy.wait(10000);

        DataLakeUtils.checkResults(
            'adaptertotestschemarules',
            'cypress/fixtures/connect/valueRules/expected.csv',
            false);
    });

});
