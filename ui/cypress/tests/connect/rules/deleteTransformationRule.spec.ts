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
import { ConnectUtils } from '../../../support/utils/connect/ConnectUtils';
import { FileManagementUtils } from '../../../support/utils/FileManagementUtils';
import { ConnectEventSchemaUtils } from '../../../support/utils/connect/ConnectEventSchemaUtils';
import { AdapterBuilder } from '../../../support/builder/AdapterBuilder';
import { ConnectBtns } from '../../../support/utils/connect/ConnectBtns';

describe('Connect delete rule transformation', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
    });

    it('Test delete with same prefix', () => {
        FileManagementUtils.addFile(
            'connect/deleteTransformationRule/prefixInput.csv',
        );

        ConnectUtils.setUpPreprocessingRuleTest(false);

        ConnectEventSchemaUtils.deleteProperty('reserved bit');
        ConnectEventSchemaUtils.deleteProperty('reserved bit_1');
        ConnectEventSchemaUtils.deleteProperty('reserved bit_2');

        cy.dataCy('schema-preview-result-event').should(
            'have.text',
            '{\u00A0\u00A0\u00A0\u00A0"timestamp":\u00A01715356080000}',
        );
    });

    it('Test delete nested properties', () => {
        FileManagementUtils.addFile(
            'connect/deleteTransformationRule/nestedInput.json',
        );

        const adapterConfigurationBuilder = AdapterBuilder.create('File_Stream')
            .setStoreInDataLake()
            .setTimestampProperty('timestamp')
            .addProtocolInput(
                'radio',
                'speed',
                'fastest_\\(ignore_original_time\\)',
            )
            .addProtocolInput('radio', 'replayonce', 'yes')
            .setName('Adapter to test rules')
            .setFormat('json');

        ConnectUtils.setUpPreprocessingRuleTest(
            false,
            adapterConfigurationBuilder,
        );

        // Test to delete the child property
        ConnectEventSchemaUtils.deleteProperty('child');

        // The resulting string contains non-breaking spaces character (\u00A0)
        cy.dataCy('schema-preview-result-event').should(
            'have.text',
            '{\u00A0\u00A0\u00A0\u00A0"parent":\u00A0{\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0"child_two":\u00A0"textTwo"\u00A0\u00A0\u00A0\u00A0},\u00A0\u00A0\u00A0\u00A0"timestamp":\u00A01667904471000}',
        );

        ConnectBtns.refreshSchema().click();

        // Test to delete the parent property
        ConnectEventSchemaUtils.deleteProperty('parent');
        cy.dataCy('schema-preview-result-event').should(
            'have.text',

            '{\u00A0\u00A0\u00A0\u00A0"timestamp":\u00A01667904471000}',
        );

        ConnectBtns.refreshSchema().click();
        ConnectEventSchemaUtils.deleteProperty('child');
        ConnectEventSchemaUtils.deleteProperty('child_two');

        cy.dataCy('schema-preview-result-event').should(
            'have.text',
            '{\u00A0\u00A0\u00A0\u00A0"parent":\u00A0{},\u00A0\u00A0\u00A0\u00A0"timestamp":\u00A01667904471000}',
        );

        ConnectBtns.refreshSchema().click();

        // Test to delete the parent property
        ConnectEventSchemaUtils.deleteProperty('parent');
        cy.dataCy('schema-preview-result-event').should(
            'have.text',

            '{\u00A0\u00A0\u00A0\u00A0"timestamp":\u00A01667904471000}',
        );

        ConnectBtns.refreshSchema().click();

        // Test to delete both child properties
        ConnectEventSchemaUtils.deleteProperty('child');
        ConnectEventSchemaUtils.deleteProperty('child_two');

        cy.dataCy('schema-preview-result-event').should(
            'have.text',
            '{\u00A0\u00A0\u00A0\u00A0"parent":\u00A0{},\u00A0\u00A0\u00A0\u00A0"timestamp":\u00A01667904471000}',
        );
    });
});
