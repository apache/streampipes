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
import { ConnectUtils } from '../../support/utils/connect/ConnectUtils';
import { ConnectEventSchemaUtils } from '../../support/utils/connect/ConnectEventSchemaUtils';
import { ConnectBtns } from '../../support/utils/connect/ConnectBtns';
import { AdapterBuilder } from '../../support/builder/AdapterBuilder';
import { StaticPropertyUtils } from '../../support/utils/userInput/StaticPropertyUtils';
import { AdapterInput } from '../../support/model/AdapterInput';
import { SharedUtils } from '../../support/utils/shared/SharedUtils';
import { SharedBtns } from '../../support/utils/shared/SharedBtns';

describe('Validate Warning Pops For Configuration Changes ', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
    });

    it('Perform Test', () => {
        const name = 'Validate Configuration Changes Adapter';

        const adapterConfiguration = AdapterBuilder.create(
            'Machine_Data_Simulator',
        )
            .setName(name)
            .setTimestampProperty('timestamp')
            .addInput('input', 'wait-time-ms', '1000');

        setUpAdapterTillConfigureFields(adapterConfiguration.build());

        // Change configuration
        adapterConfiguration.addInput(
            'radio',
            'selected',
            'simulator-option-pressure',
        );
        StaticPropertyUtils.input(
            adapterConfiguration.build().adapterConfiguration,
        );

        // Update event schema
        ConnectBtns.adapterSettingsNextBtn().click();
        SharedUtils.confirmDialogVisible();
        ConnectBtns.configureSchemaNextBtn().should('be.disabled');
        SharedBtns.confirmDialogConfirmBtn().click();
        ConnectBtns.configureSchemaNextBtn().should('not.be.disabled');
        ConnectBtns.configureSchemaNextBtn().click();

        // Update Event fields
        SharedUtils.confirmDialogVisible();
        ConnectEventSchemaUtils.configureFieldsNextBtnDisabled();
        SharedBtns.confirmDialogConfirmBtn().click();
        ConnectEventSchemaUtils.markPropertyAsTimestamp(
            adapterConfiguration.build().timestampProperty,
        );
        ConnectEventSchemaUtils.configureFieldsNextBtnEnabled();
        ConnectBtns.configureFieldsNextBtn().click();

        ConnectUtils.startAdapter(adapterConfiguration.build());
    });

    const setUpAdapterTillConfigureFields = (adapter: AdapterInput) => {
        ConnectUtils.goToConnect();
        ConnectUtils.goToNewAdapterPage();
        ConnectUtils.selectAdapter(adapter.adapterType);
        ConnectUtils.configureAdapter(adapter);
        ConnectUtils.configureSchema(adapter);
        ConnectUtils.eventSchemaWithFieldsShouldBeVisible();
        ConnectBtns.configureFieldsBackBtn().click();
        ConnectBtns.configureSchemaBackBtn().click();
    };
});
