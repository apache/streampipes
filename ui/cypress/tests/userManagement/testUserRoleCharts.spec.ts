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

describe('Test User Roles for Charts', () => {
    const chartName = 'test-chart';
    let chartUser1: User;
    let chartAdmin1: User;
    let chartAdmin2: User;

    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();

        chartUser1 = UserUtils.createUser(
            'chartUser1',
            UserRole.ROLE_DATA_EXPLORER_USER,
        );

        chartAdmin1 = UserUtils.createUser(
            'chartAdmin1',
            UserRole.ROLE_DATA_EXPLORER_ADMIN,
            UserRole.ROLE_CONNECT_ADMIN,
            UserRole.ROLE_PIPELINE_ADMIN,
        );

        chartAdmin2 = UserUtils.createUser(
            'chartAdmin2',
            UserRole.ROLE_DATA_EXPLORER_ADMIN,
        );
    });

    it('Chart is not shared with other users', () => {
        setup();

        // check admin
        chartIsVisibleAndEditableCanChangePermissions(UserUtils.adminUser);

        // check other users
        chartIsNotVisible(chartAdmin2);
    });

    it('Make chart public', () => {
        setup();

        PermissionUtils.markElementAsPublic(chartName);

        chartIsVisibleAndEditableCanChangePermissions(UserUtils.adminUser);

        chartIsVisibleButNotEditable(chartUser1);

        chartIsVisibleAndEditableCannotChangePermissions(chartAdmin2);
    });

    it('Share chart with other user and change ownership', () => {
        setup();

        PermissionUtils.authorizeUser(chartName, chartAdmin2.email);

        chartIsVisibleAndEditableCanChangePermissions(UserUtils.adminUser);

        chartIsVisibleAndEditableCannotChangePermissions(chartAdmin2);

        chartIsNotVisible(chartUser1);

        UserUtils.switchUser(chartAdmin1);
        DataExplorerUtils.goToDatalake();
        PermissionUtils.changeOwnership(chartName, chartAdmin2.email);

        chartIsNotVisible(chartAdmin1);

        chartIsVisibleAndEditableCanChangePermissions(UserUtils.adminUser);

        chartIsVisibleAndEditableCanChangePermissions(chartAdmin2);

        chartIsNotVisible(chartUser1);
    });

    it('Chart is shared with group for user 2', () => {
        const chartAdminGroup = 'chart_admin_group';
        UserUtils.createGroup(
            chartAdminGroup,
            UserRole.ROLE_DATA_EXPLORER_ADMIN,
        );
        UserUtils.addGroupToUser(chartAdminGroup, chartAdmin2.name);

        setup();

        PermissionUtils.authorizeGroup(chartName, chartAdminGroup);

        chartIsVisibleAndEditableCanChangePermissions(UserUtils.adminUser);

        chartIsNotVisible(chartUser1);

        chartIsVisibleAndEditableCannotChangePermissions(chartAdmin2);
    });

    function setup() {
        UserUtils.switchUser(chartAdmin1);
        ConnectUtils.addMachineDataSimulator('simulator', true);
        DataExplorerUtils.addDataViewAndTableWidget(
            chartName,
            'simulator',
            true,
        );
        DataExplorerUtils.saveDataViewConfiguration();
        DataExplorerUtils.goToDatalake();
    }

    function chartIsVisibleAndEditableCanChangePermissions(user: User) {
        UserUtils.switchUser(user);
        DataExplorerUtils.checkAmountOfCharts(1);
        DataExplorerUtils.checkChartCanBeEdited(chartName);

        PermissionUtils.validateUserCanChangePermissions(chartName);
    }

    function chartIsVisibleAndEditableCannotChangePermissions(user: User) {
        UserUtils.switchUser(user);
        DataExplorerUtils.checkAmountOfCharts(1);
        DataExplorerUtils.checkChartCanBeEdited(chartName);

        PermissionUtils.validateUserCanNotChangePermissions(chartName);
    }

    function chartIsVisibleButNotEditable(user: User) {
        UserUtils.switchUser(user);
        DataExplorerUtils.checkAmountOfCharts(1);
        DataExplorerUtils.checkChartCanNotBeEdited(chartName);

        PermissionUtils.validateUserCanNotChangePermissions(chartName);
    }

    function chartIsNotVisible(user: User) {
        UserUtils.switchUser(user);
        DataExplorerUtils.checkAmountOfCharts(0);
    }
});
