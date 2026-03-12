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

import { UserRole } from '../../../src/app/core/auth/user-role.enum';
import { UserUtils } from '../../support/utils/UserUtils';
import { ConnectUtils } from '../../support/utils/connect/ConnectUtils';
import { User } from '../../support/model/User';
import { ChartUtils } from '../../support/utils/chart/ChartUtils';
import { PermissionUtils } from '../../support/utils/user/PermissionUtils';
import { ChartBtns } from '../../support/utils/chart/ChartBtns';
import { DatasetUtils } from '../../support/utils/dataset/DatasetUtils';
import { GeneralUtils } from '../../support/utils/GeneralUtils';

describe('Test Dataset Permissions', () => {
    const datasetName = 'Persist simulator';
    let datasetUser1: User;
    let datasetAdmin1: User;
    let datasetAdmin2: User;
    let chartAdmin1: User;
    let chartUser1: User;
    let dashboardAdmin1: User;

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

        chartUser1 = UserUtils.createUser(
            'chartUser1',
            UserRole.ROLE_DATA_EXPLORER_USER,
        );

        dashboardAdmin1 = UserUtils.createUser(
            'dashboardAdmin1',
            UserRole.ROLE_DASHBOARD_ADMIN,
        );
    });

    it('Dataset is not shared with other users', () => {
        generateDataset();

        assertDatasetIsVisibleAndEditableCanChangePermissions(
            UserUtils.adminUser,
        );

        assertDatasetIsNotVisible(datasetUser1);

        UserUtils.switchUser(datasetUser1);

        assertDatasetIsNotVisible(datasetAdmin2);
    });

    it('Datasets only usable in charts if permissions were configured', () => {
        generateDataset();

        UserUtils.switchUser(chartAdmin1);

        assertDatasetAvailabilityInCharts(false);

        authUserOnDataset('chartAdmin1@streampipes.apache.org');

        UserUtils.switchUser(chartAdmin1);

        assertDatasetAvailabilityInCharts(true);

        ChartUtils.goToDatalake();

        PermissionUtils.authorizeUser(
            'test',
            'chartUser1@streampipes.apache.org',
        );

        // I am not quite sure why this is needed, but without it the test fails
        cy.dataCy('confirm-delete', { timeout: 10000 }).click();

        UserUtils.switchUser(chartUser1);

        ChartUtils.checkAmountOfCharts(1);

        GeneralUtils.openMenuForRow('test');

        ChartBtns.viewWidget('test').click();

        assertAlertBanner(true);

        assertDatasetIsNotVisible(chartUser1);

        authUserOnDataset('chartUser1@streampipes.apache.org');

        UserUtils.switchUser(chartUser1);

        ChartUtils.checkAmountOfCharts(1);

        GeneralUtils.openMenuForRow('test');

        ChartBtns.viewWidget('test').click();

        assertAlertBanner(false);
    });

    it('Data only shown in dashboard if permissions were configured', () => {
        generateDataset();

        authUserOnDataset('chartAdmin1@streampipes.apache.org');

        UserUtils.switchUser(chartAdmin1);

        assertDatasetAvailabilityInCharts(true);

        PermissionUtils.authorizeUser(
            'test',
            'dashboardAdmin1@streampipes.apache.org',
        );

        UserUtils.switchUser(dashboardAdmin1);

        generateDashboard('TestDB');

        assertAlertBanner(true);

        ChartBtns.discardDashboard().click();

        assertDatasetIsNotVisible(dashboardAdmin1);

        authUserOnDataset('dashboardAdmin1@streampipes.apache.org');

        UserUtils.switchUser(dashboardAdmin1);

        generateDashboard('TestDB2');

        assertAlertBanner(false);
    });

    function assertDatasetAvailabilityInCharts(available: boolean) {
        ChartUtils.goToDatalake();
        ChartBtns.openNewDataViewBtn().click();
        if (!available) {
            cy.get('sp-alert-banner').should('be.visible');
        } else {
            ChartUtils.assertSelectDataSet('simulator');
            ChartUtils.addDataViewAndTableWidget('test', 'simulator', true);
            ChartUtils.saveDataViewConfiguration();
        }
    }

    function generateDataset() {
        UserUtils.switchUser(datasetAdmin1);
        ConnectUtils.addMachineDataSimulator('simulator', true);
    }

    function generateDashboard(name: string) {
        ChartUtils.goToDashboard();
        ChartUtils.createNewDashboard(name);
        ChartUtils.editDashboard(name);
        ChartUtils.addDataViewToDashboard('test', true);
    }
    function assertDatasetIsVisibleAndEditableCanChangePermissions(user: User) {
        UserUtils.switchUser(user);
        DatasetUtils.goToDatasets();
        DatasetUtils.checkAmountOfDatasets(1);
        PermissionUtils.validateUserCanChangePermissions(datasetName);
    }

    function assertDatasetIsNotVisible(user: User) {
        UserUtils.switchUser(user);
        DatasetUtils.goToDatasets();
        DatasetUtils.checkAmountOfDatasets(0);
    }

    function assertAlertBanner(exists: boolean) {
        if (exists) {
            cy.dataCy('sp-alert-banner-error').should('exist');
        } else {
            cy.dataCy('sp-alert-banner-error').should('not.exist');
        }
    }

    function authUserOnDataset(email: string) {
        UserUtils.switchUser(datasetAdmin1);

        DatasetUtils.authorizeUserOnDataset('simulator', email);
    }
});
