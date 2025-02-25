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
import { DataLakeUtils } from '../../../support/utils/datalake/DataLakeUtils';

describe('Connect schema rule transformations', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
    });

    it('Add a static property and transform it to a number', () => {
        FileManagementUtils.addFile(
            'connect/addNumericalStaticValue/input.csv',
        );
        const adapterConfiguration =
            ConnectUtils.setUpPreprocessingRuleTest(true);

        const newValueOne = 'newValueOne';
        const newValueTwo = 'newValueTwo';

        ConnectEventSchemaUtils.addStaticProperty(newValueOne, '1.0');
        ConnectEventSchemaUtils.addStaticProperty(newValueTwo, '2');

        ConnectEventSchemaUtils.changePropertyDataType(newValueOne, 'Float');
        ConnectEventSchemaUtils.changePropertyDataType(newValueTwo, 'Integer');

        ConnectEventSchemaUtils.markPropertyAsMeasurement(newValueOne);
        ConnectEventSchemaUtils.markPropertyAsMeasurement(newValueTwo);

        ConnectEventSchemaUtils.markPropertyAsTimestamp('timestamp');

        ConnectEventSchemaUtils.finishEventSchemaConfiguration();

        ConnectUtils.startAdapter(adapterConfiguration, true);

        cy.wait(1000);

        DataLakeUtils.checkResults(
            'Adapter to test rules',
            'cypress/fixtures/connect/addNumericalStaticValue/expected.json',
            true,
        );
    });
});
