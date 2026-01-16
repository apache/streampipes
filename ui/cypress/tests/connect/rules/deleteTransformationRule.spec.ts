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
import { AdapterBuilder } from '../../../support/builder/AdapterBuilder';
import { ConnectBtns } from '../../../support/utils/connect/ConnectBtns';

describe('Connect delete rule transformation', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
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

        ConnectUtils.replaceAdapterScript(
            '  delete event.toRemove;\n' +
                '  delete event.parent.child_two;\n' +
                '  out.collect(event);\n' +
                '}',
        );

        ConnectBtns.configureSchemaRunScriptBtn().click();

        ConnectBtns.configureSchemaEventPreviewResult()
            .invoke('text')
            .then(text => {
                const normalizedText = text.replace(/\u00a0/g, ' ').trim();

                const actualJson = JSON.parse(normalizedText);
                const expectedJson = {
                    parent: {
                        child: 'text',
                    },
                    timestamp: 1667904471000,
                };

                expect(actualJson).to.deep.equal(expectedJson);
            });
    });
});
