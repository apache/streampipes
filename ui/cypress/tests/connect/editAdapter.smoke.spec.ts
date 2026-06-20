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
import { ChartUtils } from '../../support/utils/chart/ChartUtils';
import { ChartBtns } from '../../support/utils/chart/ChartBtns';
import { SharedUtils } from '../../support/utils/shared/SharedUtils';
import { SharedBtns } from '../../support/utils/shared/SharedBtns';
import { ConnectEventSchemaUtils } from '../../support/utils/connect/ConnectEventSchemaUtils';

describe('Test Edit Adapter', () => {
    beforeEach('Setup Test', () => {
        // To set up test add a stream adapter that can be configured
        cy.initStreamPipesTest();
    });

    it('Successfully edit adapter', () => {
        ConnectUtils.addMachineDataSimulator('simulator');
        const newAdapterName = 'Edited Adapter';

        ConnectUtils.goToConnect();

        // stop adapter
        ConnectUtils.stopAdapterAndWaitForStateTransition();

        // click edit adapter
        ConnectBtns.openActionsMenu('simulator');
        ConnectBtns.editAdapter().should('not.be.disabled');
        ConnectBtns.editAdapter().click();

        // Change adapter name and wait time

        const newUserConfiguration = AdapterBuilder.create(
            'Machine_Data_Simulator',
        )
            .addInput('input', 'wait-time-ms', '2000')
            .addInput('radio', 'selected', 'simulator-option-pressure')
            .build();

        ConnectUtils.configureAdapter(newUserConfiguration);
        SharedUtils.confirmDialogVisible();
        SharedBtns.confirmDialogConfirmBtn().click();

        // Update event schema
        ConnectBtns.getNewSampleBtn().click();
        ConnectUtils.finishEventSchemaConfiguration();
        SharedUtils.confirmDialogVisible();
        SharedBtns.confirmDialogConfirmBtn().click();

        ConnectUtils.refreshEventSchema();
        ConnectUtils.finishConfigureFieldsConfiguration();

        ConnectBtns.adapterNameInput().clear().type(newAdapterName);

        // This wait is required to ensure that there is no couch db update conflict
        ConnectBtns.storeEditAdapter().click();

        ConnectBtns.connectAdapterAddedSuccessfully().should('be.visible');

        ConnectUtils.closeAdapterPreview();

        ConnectUtils.startAndValidateAdapter('Edited Adapter', 3);
        ConnectUtils.goToConnect();

        // Validate that name of adapter and data stream
        cy.dataCy('adapter-name').contains(newAdapterName);
    });

    it('Successfully edit adapter with persistence pipeline', () => {
        ConnectUtils.addMachineDataSimulator('simulator', true, '200');

        ConnectUtils.goToConnect();

        // stop adapter and edit adapter
        ConnectUtils.stopAdapterAndWaitForStateTransition();
        ConnectBtns.openActionsMenu('simulator');
        ConnectBtns.editAdapter().click();

        // change data type of density to integer
        ConnectBtns.adapterSettingsNextBtn().click();

        ConnectUtils.replaceAdapterScript(
            '  event.density = event.density * 2;\n' +
                '  out.collect(event);\n',
        );
        ConnectBtns.configureSchemaRunScriptBtn().click();

        ConnectUtils.finishEventSchemaConfiguration();
        SharedUtils.confirmDialogVisible();
        SharedBtns.confirmDialogConfirmBtn().click();
        SharedUtils.confirmDialogClosed();
        ConnectEventSchemaUtils.markPropertyAsTimestamp('timestamp');

        storeAndStartEditedAdapter();

        // Validate that the data is further persisted in the database by checking if the amount of events in the data lake changes
        ChartUtils.goToDatalakeConfiguration();

        ChartUtils.waitForCountingResults();

        let initialValue;

        ChartUtils.getDatalakeNumberOfEvents().then(value => {
            initialValue = value;
        });

        cy.wait(3000);

        ChartBtns.refreshDataLakeMeasures().click();

        ChartUtils.waitForCountingResults();

        ChartUtils.getDatalakeNumberOfEvents().then(newValue => {
            expect(newValue).not.equal(initialValue);
        });
    });

    const storeAndStartEditedAdapter = () => {
        ConnectUtils.finishConfigureFieldsConfiguration();
        ConnectBtns.storeEditAdapter().click();
        ConnectBtns.updateAndMigratePipelines().click();
        ConnectUtils.closeAdapterPreview();
        ConnectBtns.startAdapter().click();
    };
});
