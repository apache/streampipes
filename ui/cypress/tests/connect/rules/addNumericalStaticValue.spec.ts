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
import { ConnectBtns } from '../../../support/utils/connect/ConnectBtns';
import { GeneralUtils } from '../../../support/utils/GeneralUtils';

describe('Connect schema rule transformations', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
    });

    it('Add a static property and transform it to a number', () => {
        FileManagementUtils.addFile(
            'connect/addNumericalStaticValue/input.csv',
        );
        const adapterConfiguration =
            ConnectUtils.setUpPreprocessingRuleTest(false);

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

        ConnectUtils.goToConnect();
        GeneralUtils.openMenuForRow(adapterConfiguration.adapterName);
        ConnectBtns.editAdapter().click();
        // This waiting time is required to ensure that the file is loaded correctly before the next button is clicked
        cy.wait(1000);
        ConnectBtns.nextBtn().click();

        // Validate that the preview is correct

        const expectedJsonPreview = {
            newValueTwo: 2,
            count: 122,
            timestamp: 1720018277000,
            newValueOne: 1,
        };
        ConnectEventSchemaUtils.schemaPreviewResultEvent().then($el => {
            let jsonPreview = $el.text();
            jsonPreview = jsonPreview.replace(/\s+/g, '');

            const actualJson = JSON.parse(jsonPreview);
            expect(actualJson).to.deep.equal(expectedJsonPreview);
        });
    });
});
