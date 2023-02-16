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
import { ConnectEventSchemaUtils } from '../../../support/utils/ConnectEventSchemaUtils';

describe('Connect value rule transformations', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
        FileManagementUtils.addFile('connect/valueRules/input.csv');
    });

    it('Perform Test', () => {
        const adapterConfiguration = ConnectUtils.setUpPreprocessingRuleTest();

        // Edit timestamp property
        ConnectEventSchemaUtils.editTimestampProperty(
            'timestamp',
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        );

        // Number transformation
        ConnectEventSchemaUtils.numberTransformation('value', '10');

        // Unit transformation
        ConnectEventSchemaUtils.unitTransformation(
            'temperature',
            'Degree Celsius',
            'Degree Fahrenheit',
        );

        ConnectEventSchemaUtils.finishEventSchemaConfiguration();

        ConnectUtils.tearDownPreprocessingRuleTest(
            adapterConfiguration,
            'cypress/fixtures/connect/valueRules/expected.csv',
            false,
            2000,
        );
    });
});
