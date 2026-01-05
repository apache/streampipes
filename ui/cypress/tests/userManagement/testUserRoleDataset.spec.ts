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

import { UserRole } from '../../../src/app/_enums/user-role.enum';
import { UserUtils } from '../../support/utils/UserUtils';
import { ConnectUtils } from '../../support/utils/connect/ConnectUtils';
import { User } from '../../support/model/User';
import { DataExplorerUtils } from '../../support/utils/dataExplorer/DataExplorerUtils';
import { PermissionUtils } from '../../support/utils/user/PermissionUtils';
import { DataExplorerBtns } from '../../support/utils/dataExplorer/DataExplorerBtns';
import { DataSetUtils } from '../../support/utils/DataSetUtils';
import { DatasetUtils } from '../../support/utils/dataset/DatasetUtils';

describe('Test Dataset Permissions', () => {
    const datasetName = 'Persist simulator';
    let datasetUser1: User;
    let datasetAdmin1: User;
    let datasetAdmin2: User;
    let chartAdmin1: User;

    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();

        datasetUser1 = UserUtils.createUser(
            'datasetUser1',
            UserRole.ROLE_PIPELINE_USER,
        );

        datasetAdmin1 = UserUtils.createUser(
            'datasetAdmin1',
            UserRole.ROLE_CONNECT_ADMIN,
            UserRole.ROLE_PIPELINE_ADMIN,
        );

        datasetAdmin2 = UserUtils.createUser(
            'datasetAdmin2',
            UserRole.ROLE_PIPELINE_ADMIN,
        );

        chartAdmin1 = UserUtils.createUser(
            'chartAdmin1',
            UserRole.ROLE_DATA_EXPLORER_ADMIN,
        );
    });

    /**it('Dataset is not shared with other users', () => {

            UserUtils.switchUser(datasetAdmin1);
        ConnectUtils.addMachineDataSimulator('simulator', true);
        assertDatasetIsVisibleAndEditableCanChangePermissions(
            UserUtils.adminUser,
        );

        assertPipelineIsNotVisible(datasetUser1);

        UserUtils.switchUser(datasetUser1);

        assertPipelineIsNotVisible(datasetAdmin2);
    });*/

    it('Datasets only usable in charts if permissions were configured', () => {
        UserUtils.switchUser(datasetAdmin1);
        ConnectUtils.addMachineDataSimulator('simulator', true);

        UserUtils.switchUser(chartAdmin1);

        assertDatasetAvailabilityInCharts(false);

        UserUtils.switchUser(datasetAdmin1);

        DatasetUtils.authorizeUserOnDataset(
            'simulator',
            'chartAdmin1@streampipes.apache.org',
        );

        UserUtils.switchUser(chartAdmin1);

        assertDatasetAvailabilityInCharts(true);
    });

    /**it('Data only shown in dashboard if permissions were configured', () => {
    });*/
    function assertDatasetAvailabilityInCharts(available: boolean) {
        DataExplorerUtils.goToDatalake();
        DataExplorerBtns.openNewDataViewBtn().click();
        if (!available) {
            cy.get('sp-alert-banner').should('be.visible');
        } else {
            DataExplorerUtils.assertSelectDataSet('simulator');
            DataExplorerUtils.addDataViewAndTableWidget(
                'test',
                'simulator',
                true,
            );
            DataExplorerUtils.saveDataViewConfiguration();
        }
    }
    function assertDatasetIsVisibleAndEditableCanChangePermissions(user: User) {
        UserUtils.switchUser(user);
        DatasetUtils.goToDatasets();
        DatasetUtils.checkAmountOfDatasets(1);
        PermissionUtils.validateUserCanChangePermissions(datasetName);
    }

    function assertPipelineIsNotVisible(user: User) {
        UserUtils.switchUser(user);
        DatasetUtils.goToDatasets();
        DatasetUtils.checkAmountOfDatasets(0);
    }
});
