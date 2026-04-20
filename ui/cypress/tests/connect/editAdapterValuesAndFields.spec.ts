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
import { ConnectBtns } from '../../support/utils/connect/ConnectBtns';
import { AdapterBuilder } from '../../support/builder/AdapterBuilder';
import { ConnectEventSchemaUtils } from '../../support/utils/connect/ConnectEventSchemaUtils';
import { SharedUtils } from '../../support/utils/shared/SharedUtils';
import { SharedBtns } from '../../support/utils/shared/SharedBtns';

describe('Test Edit Adapter', () => {
    const adapterName = 'Test Adapter';

    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
    });

    it('Edit and persist adapter schema and reconfigure adapter', () => {
        createAdapterWithSchemaEdits();

        validateSavedAdapterEdits();

        reconfigureAdapterToUsePressureSensorInsteadOfFlowRate();
    });

    function createAdapterWithSchemaEdits() {
        const adapterInput = AdapterBuilder.create('Machine_Data_Simulator')
            .setName(adapterName)
            .addInput('input', 'wait-time-ms', '1000')
            .setTimestampProperty('timestamp')
            .build();

        ConnectUtils.goToConnect();

        ConnectUtils.goToNewAdapterPage();

        ConnectUtils.selectAdapter(adapterInput.adapterType);

        ConnectUtils.configureAdapter(adapterInput);

        cy.wait(1000);
        ConnectBtns.configureSchemaNextBtn().click();

        // Edit property density
        const propertyName = 'density';
        ConnectEventSchemaUtils.changePropertyDataType(propertyName, 'Double');
        ConnectEventSchemaUtils.changeSemanticType(
            propertyName,
            'http://schema.org/Numbers',
        );

        ConnectEventSchemaUtils.markPropertyAsTimestamp('timestamp');

        ConnectBtns.configureFieldsNextBtn().click();

        ConnectUtils.startAdapter(adapterInput);
    }

    function validateSavedAdapterEdits() {
        // Edit the adapter and check if given values and added property still provided
        ConnectBtns.openActionsMenu(adapterName);
        ConnectBtns.editAdapter().should('not.be.disabled');
        ConnectBtns.editAdapter().click();
        ConnectBtns.adapterSettingsNextBtn().click();
        ConnectBtns.configureSchemaNextBtn().click();
        ConnectEventSchemaUtils.clickEditProperty('density');

        ConnectBtns.semanticTypeInput().should(
            'have.value',
            'http://schema.org/Numbers',
        );
        ConnectBtns.changeRuntimeType().should('include.text', 'Double');

        ConnectBtns.changeRuntimeType()
            .click()
            .get('mat-option')
            .contains('Float')
            .click();
        ConnectBtns.saveEditProperty().click();

        storeAndCloseAdapterPreview();
    }

    function reconfigureAdapterToUsePressureSensorInsteadOfFlowRate() {
        // Configure the adapter with pressure instead of flowrate
        ConnectBtns.openActionsMenu(adapterName);
        ConnectBtns.editAdapter().click();

        const adapterInput = AdapterBuilder.create('Machine_Data_Simulator')
            .addInput('input', 'wait-time-ms', '2000')
            .addInput('radio', 'selected', 'simulator-option-pressure')
            .build();
        ConnectUtils.configureAdapter(adapterInput);
        SharedUtils.confirmDialogVisible();
        SharedBtns.confirmDialogConfirmBtn().click();
        ConnectBtns.configureSchemaNextBtn().click();
        SharedUtils.confirmDialogVisible();
        SharedBtns.confirmDialogConfirmBtn().click();

        storeAndCloseAdapterPreview();
    }

    function storeAndCloseAdapterPreview() {
        ConnectBtns.configureFieldsNextBtn().click();
        ConnectBtns.storeEditAdapter().click();
        ConnectUtils.closeAdapterPreview();
    }
});
