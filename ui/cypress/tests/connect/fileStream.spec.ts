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

import { ConnectUtils } from '../../support/utils/connect/ConnectUtils';
import { FileManagementUtils } from '../../support/utils/FileManagementUtils';
import { AdapterBuilder } from '../../support/builder/AdapterBuilder';
import { ConnectBtns } from '../../support/utils/connect/ConnectBtns';
import { ConnectEventSchemaUtils } from '../../support/utils/connect/ConnectEventSchemaUtils';

describe(
    'Test File Replay Adapter',
    {
        retries: {
            runMode: 4,
            openMode: 1,
        },
    },
    () => {
        beforeEach('Setup Test', () => {
            cy.initStreamPipesTest();
        });

        it('Test successful adapter generation for file stream adapter', () => {
            FileManagementUtils.addFile('fileTest/random.csv');
            const adapterInput = AdapterBuilder.create('File_Stream')
                .setName('File Stream Adapter Test')
                .setTimestampProperty('timestamp')
                .addProtocolInput('checkbox', 'replaceTimestamp', 'check')
                .setFormat('csv')
                .addFormatInput('input', ConnectBtns.csvDelimiter(), ';')
                .addFormatInput('checkbox', ConnectBtns.csvHeader(), 'check')
                .build();

            ConnectUtils.testAdapter(adapterInput);
            ConnectUtils.deleteAdapter();
        });

        it('File stream adapter should not allow add timestamp option in schema editor', () => {
            FileManagementUtils.addFile('connect/fileReplay/noTimestamp.csv');
            const adapterInput = AdapterBuilder.create('File_Stream')
                .setName('File Stream Adapter Test')
                .setAutoAddTimestampPropery()
                .setFormat('csv')
                .addFormatInput('input', ConnectBtns.csvDelimiter(), ';')
                .addFormatInput('checkbox', ConnectBtns.csvHeader(), 'check')
                .build();

            ConnectUtils.testAdapter(adapterInput, true);
        });

        it('File Stream adapter with unix timestamp in seconds', () => {
            FileManagementUtils.addFile(
                'connect/fileReplay/timestampInSeconds/input.csv',
            );
            const adapterConfiguration =
                ConnectUtils.setUpPreprocessingRuleTest(false);

            // Edit timestamp property
            ConnectEventSchemaUtils.editTimestampPropertyWithNumber(
                'timestamp',
                'Seconds',
            );

            ConnectEventSchemaUtils.finishEventSchemaConfiguration();
            ConnectUtils.tearDownPreprocessingRuleTest(
                adapterConfiguration,
                'cypress/fixtures/connect/fileReplay/timestampInSeconds/expected.csv',
                false,
                2000,
            );
        });

        it('File Stream adapter with unix timestamp in milliseconds', () => {
            FileManagementUtils.addFile(
                'connect/fileReplay/timestampInMilliseconds/input.csv',
            );
            const adapterConfiguration =
                ConnectUtils.setUpPreprocessingRuleTest(false);

            // Edit timestamp property
            ConnectEventSchemaUtils.editTimestampPropertyWithNumber(
                'timestamp',
                'Milliseconds',
            );

            ConnectEventSchemaUtils.finishEventSchemaConfiguration();
            ConnectUtils.tearDownPreprocessingRuleTest(
                adapterConfiguration,
                'cypress/fixtures/connect/fileReplay/timestampInMilliseconds/expected.csv',
                false,
                2000,
            );
        });

        it('File Stream adapter validate file is shown when editing file static property', () => {
            // Add a sample file adapter to edit
            FileManagementUtils.addFile('fileTest/random.csv');

            const adapterInput = AdapterBuilder.create('File_Stream')
                .setName('File Stream Adapter Test')
                .setTimestampProperty('timestamp')
                .addProtocolInput('checkbox', 'replaceTimestamp', 'check')
                .setFormat('csv')
                .addFormatInput('input', ConnectBtns.csvDelimiter(), ';')
                .addFormatInput('checkbox', ConnectBtns.csvHeader(), 'check')
                .setStartAdapter(false)
                .build();

            ConnectUtils.testAdapter(adapterInput);

            // click on edit adapter
            ConnectBtns.editAdapter().click();

            // validate that the file name is set as default
            cy.dataCy('file-input-file-name').should(
                'have.value',
                'random.csv',
            );
        });
    },
);
