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


        ConnectUtils.goToConnect();
        ConnectUtils.selectAdapter(adapterConfiguration.adapterType);
        ConnectUtils.configureAdapter(adapterConfiguration.protocolConfiguration);
        ConnectUtils.configureFormat(adapterConfiguration);

        // wait till schema is shown
        cy.dataCy('sp-connect-schema-editor', { timeout: 60000 }).should('be.visible');

        // Add static value to event
        ConnectEventSchemaUtils.addStaticProperty('staticPropertyName', 'id1');

        // Delete one property
        ConnectEventSchemaUtils.deleteProperty('density');

        // TODO use data type class
        ConnectEventSchemaUtils.changePropertyDataType('temperature', 'Float');

        // Add a timestamp property
        ConnectEventSchemaUtils.addTimestampProperty();

        ConnectEventSchemaUtils.finishEventSchemaConfiguration();

        ConnectUtils.startSetAdapter(adapterConfiguration);

        // Wait till data is stored
        cy.wait(10000);

        DataLakeUtils.checkResults(
            'adaptertotestschemarules',
            'cypress/fixtures/connect/schemaRules/expected.csv',
            true);
    });

});
