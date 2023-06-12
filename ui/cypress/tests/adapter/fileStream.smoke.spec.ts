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

describe(
    'Test File Stream Adapter',
    {
        retries: {
            runMode: 4,
            openMode: 1,
        },
    },
    () => {
        beforeEach('Setup Test', () => {
            cy.initStreamPipesTest();
            FileManagementUtils.addFile('fileTest/random.csv');
        });

        it('Perform Test', () => {
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
    },
);
