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
import { ConnectEventSchemaUtils } from '../../../support/utils/connect/ConnectEventSchemaUtils';
import { ConnectBtns } from '../../../support/utils/connect/ConnectBtns';
import { FileManagementUtils } from '../../../support/utils/FileManagementUtils';

describe('Connect value rule transformations', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
        FileManagementUtils.addFile('connect/regexRule/input.csv');
    });

    it('Perform Test Replace All True', () => {
        runTest('aenaor01', 'a', true, 'expectedReplaceAllTrue.csv');
    });

    it('Perform Test Replace All False', () => {
        runTest('aensor01', 'a', false, 'expectedReplaceAllFalse.csv');
    });

    it('Perform Test Replace With Empty String', () => {
        runTest(
            'enor01',
            '',
            true,
            'expectedReplaceAllTrueWithEmptyString.csv',
        );
    });

    function runTest(
        expectedPreview: string,
        replaceWith: string,
        replaceAll: boolean,
        expecedCsvFile: string,
    ) {
        const adapterConfiguration =
            ConnectUtils.setUpPreprocessingRuleTest(false);

        // Ensure regex input is not shown for number property
        ConnectEventSchemaUtils.clickEditProperty('density');
        ConnectEventSchemaUtils.regexValueInput().should('not.exist');
        ConnectBtns.saveEditProperty().click();
        ConnectEventSchemaUtils.clickEditProperty('sensorId');
        ConnectEventSchemaUtils.regexValueInput().should('be.visible');

        // Validate that preview works
        ConnectEventSchemaUtils.regexValueInput().type('s');
        if (replaceWith !== '') {
            ConnectEventSchemaUtils.regexReplaceWithValueInput().type(
                replaceWith,
            );
        }
        if (replaceAll) {
            ConnectEventSchemaUtils.regexReplaceAllCheckbox().click();
        }

        ConnectBtns.saveEditProperty().click();

        ConnectEventSchemaUtils.schemaPreviewResultEvent().should(
            'contain.text',
            expectedPreview,
        );
        ConnectEventSchemaUtils.markPropertyAsTimestamp('timestamp');
        ConnectEventSchemaUtils.finishEventSchemaConfiguration();

        ConnectUtils.tearDownPreprocessingRuleTest(
            adapterConfiguration,
            'cypress/fixtures/connect/regexRule/' + expecedCsvFile,
            true,
        );
    }
});
