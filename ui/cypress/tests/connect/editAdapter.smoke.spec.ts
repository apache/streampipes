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
import { DataLakeUtils } from '../../support/utils/datalake/DataLakeUtils';
import { DataLakeBtns } from '../../support/utils/datalake/DataLakeBtns';

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
        ConnectBtns.editAdapter().should('not.be.disabled');
        ConnectBtns.editAdapter().click();

        const newUserConfiguration = AdapterBuilder.create(
            'Machine_Data_Simulator',
        )
            .addInput('input', 'wait-time-ms', '2000')
            .addInput('radio', 'selected', 'simulator-option-pressure')
            .build();

        ConnectUtils.configureAdapter(newUserConfiguration);

        // Update event schema

        ConnectUtils.finishEventSchemaConfiguration();

        cy.dataCy('sp-adapter-name').clear().type(newAdapterName);

        ConnectBtns.storeEditAdapter().click();

        cy.dataCy('sp-connect-adapter-success-added', {
            timeout: 60000,
        }).should('be.visible');

        ConnectUtils.closeAdapterPreview();

        ConnectUtils.startAndValidateAdapter(3);
        ConnectUtils.goToConnect();

        // Validate that name of adapter and data stream
        cy.dataCy('adapter-name').contains(newAdapterName);
    });

    it('Successfully edit adapter with persistence pipeline', () => {
        ConnectUtils.addMachineDataSimulator('simulator', true);

        ConnectUtils.goToConnect();

        // stop adapter and edit adapter
        ConnectBtns.stopAdapter().click();
        ConnectBtns.editAdapter().click();

        // change data type of density to integer
        ConnectBtns.nextBtn().click();
        ConnectEventSchemaUtils.changePropertyDataType(
            'density',
            'Integer',
            true,
        );

        ConnectUtils.storeAndStartEditedAdapter();

        // Validate that the data is further persisted in the database by checking if the amount of events in the data lake changes
        DataLakeUtils.goToDatalakeConfiguration();

        DataLakeUtils.waitForCountingResults();

        let initialValue;

        DataLakeUtils.getDatalakeNumberOfEvents().then(value => {
            initialValue = value;
        });

        DataLakeBtns.refreshDataLakeMeasures().click();

        DataLakeUtils.waitForCountingResults();

        DataLakeUtils.getDatalakeNumberOfEvents().then(newValue => {
            // IMPORTANT: Currently we implemented a workaround by showing the user a warning message when the data type is changed.
            // In the future, we need a migration mechanism to automatically change all the StreamPipes resources that are effected
            // by the change. Once this is implemented the following line must be changed to .not.equal.
            // The issue is tracked here: https://github.com/apache/streampipes/issues/2954
            expect(newValue).equal(initialValue);
        });
    });
});
