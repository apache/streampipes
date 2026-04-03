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
import { ConnectBtns } from '../../../support/utils/connect/ConnectBtns';
import { ChartUtils } from '../../../support/utils/chart/ChartUtils';

describe('Connect schema rule transformations', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
    });

    it('Test several schema rules', () => {
        FileManagementUtils.addFile('connect/schemaRules/input.csv');
        const adapterConfiguration =
            ConnectUtils.setUpPreprocessingRuleTest(true);

        ConnectUtils.replaceAdapterScript(
            'utils.rename(event, "contains.dot", "dot");\n  ' +
                '  out.collect(event);\n' +
                '',
        );

        ConnectBtns.configureSchemaRunScriptBtn().click();

        cy.wait(1000);
        ConnectUtils.finishEventSchemaConfiguration();

        // Set data type to integer
        ConnectEventSchemaUtils.changePropertyDataType(
            'temperature',
            'Integer',
        );

        // Add a timestamp property
        ConnectEventSchemaUtils.markPropertyAsTimestamp('timestamp');

        ConnectUtils.finishConfigureFieldsConfiguration();

        ConnectUtils.startAdapter(adapterConfiguration, true);
        ConnectUtils.restartAdapter(adapterConfiguration.adapterName);

        ChartUtils.checkResults(
            'Adapter to test rules',
            'cypress/fixtures/connect/schemaRules/expected.csv',
            true,
        );
    });
});
