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
import { DataExplorerUtils } from '../../support/utils/dataExplorer/DataExplorerUtils';
import { DataExplorerBtns } from '../../support/utils/dataExplorer/DataExplorerBtns';

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
        ConnectBtns.stopAdapter().click();

        // click edit adapter
        ConnectBtns.adapterOperationInProgressSpinner().should('not.exist');
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

        // Update event schema
        ConnectBtns.getNewSampleBtn().click();
        ConnectUtils.finishEventSchemaConfiguration();

        cy.wait(1000);
        ConnectBtns.refreshSchemaBtn().click();
        ConnectEventSchemaUtils.markPropertyAsTimestamp('timestamp');
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
        ConnectBtns.stopAdapter().click();
        ConnectBtns.openActionsMenu('simulator');
        ConnectBtns.editAdapter().click();

        // change data type of density to integer
        ConnectBtns.adapterSettingsNextBtn().click();

        ConnectUtils.replaceAdapterScript(
            '  event.density = event.density * 2;\n' +
                '  out.collect(event);\n' +
                '}',
        );
        ConnectBtns.configureSchemaRunScriptBtn().click();
        cy.wait(1000);

        ConnectBtns.configureSchemaNextBtn().click();

        storeAndStartEditedAdapter();

        // Validate that the data is further persisted in the database by checking if the amount of events in the data lake changes
        DataExplorerUtils.goToDatalakeConfiguration();

        DataExplorerUtils.waitForCountingResults();

        let initialValue;

        DataExplorerUtils.getDatalakeNumberOfEvents().then(value => {
            initialValue = value;
        });

        cy.wait(3000);

        DataExplorerBtns.refreshDataLakeMeasures().click();

        DataExplorerUtils.waitForCountingResults();

        DataExplorerUtils.getDatalakeNumberOfEvents().then(newValue => {
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
