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

import { ConnectUtils } from '../../../support/utils/connect/ConnectUtils';
import { AdapterBuilder } from '../../../support/builder/AdapterBuilder';
import { ConnectBtns } from '../../../support/utils/connect/ConnectBtns';
import { GeneralUtils } from '../../../support/utils/GeneralUtils';
import { CompactUtils } from '../../../support/utils/CompactUtils';

describe('Test Compact Adapters', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
    });

    it('Validate that code for existing adapter is displayed correctly', () => {
        const adapterInput = AdapterBuilder.create('Machine_Data_Simulator')
            .setName('Machine Data Simulator Test')
            .addInput('input', 'wait-time-ms', '1000')
            .build();

        ConnectUtils.addAdapter(adapterInput);

        // Validate code editor in start dialog
        ConnectBtns.showCodeCheckbox().parent().click();
        validateCodeEditor();

        ConnectUtils.startAdapter(adapterInput);

        // Validate code editor in adapter details
        ConnectBtns.detailsAdapter().click();
        GeneralUtils.tab('Code');
        validateCodeEditor();
    });

    const validateCodeEditor = () => {
        CompactUtils.ymlConfiguration().should('be.visible');
        cy.contains('span', 'JSON').click();
        CompactUtils.jsonConfiguration().should('be.visible');
    };
});
