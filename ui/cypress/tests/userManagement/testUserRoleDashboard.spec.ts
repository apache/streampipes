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

describe('Test User Roles for Dashboards', () => {
    const dashboardName = 'test-dashboard';
    let dashboardUser1: User;
    let dashboardAdmin1: User;
    let dashboardAdmin2: User;

    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();

        dashboardUser1 = UserUtils.createUser(
            'dashboardUser1',
            UserRole.ROLE_DASHBOARD_USER,
        );

        dashboardAdmin1 = UserUtils.createUser(
            'dashboardAdmin1',
            UserRole.ROLE_DASHBOARD_ADMIN,
            UserRole.ROLE_DATA_EXPLORER_ADMIN,
            UserRole.ROLE_CONNECT_ADMIN,
            UserRole.ROLE_PIPELINE_ADMIN,
        );

        dashboardAdmin2 = UserUtils.createUser(
            'dashboardAdmin2',
            UserRole.ROLE_DASHBOARD_ADMIN,
        );
    });

    it('Dashboard is not shared with other users', () => {
        UserUtils.switchUser(dashboardAdmin1);
        DataExplorerUtils.createNewDashboard(dashboardName);

        // check admin
        dashboardIsVisibleAndEditableCanChangePermissions(UserUtils.adminUser);

        // check other users
        dashboardIsNotVisible(dashboardAdmin2);
    });

    it('Make dashboard public', () => {
        UserUtils.switchUser(dashboardAdmin1);
        DataExplorerUtils.createNewDashboard(dashboardName);
        PermissionUtils.markElementAsPublic(dashboardName);

        dashboardIsVisibleAndEditableCanChangePermissions(UserUtils.adminUser);

        dashboardIsVisibleButNotEditable(dashboardUser1);

        dashboardIsVisibleAndEditableCannotChangePermissions(dashboardAdmin2);
    });

    it('Share dashboard with other user and change ownership', () => {
        UserUtils.switchUser(dashboardAdmin1);
        DataExplorerUtils.createNewDashboard(dashboardName);

        PermissionUtils.authorizeUser(dashboardName, dashboardAdmin2.email);

        dashboardIsVisibleAndEditableCanChangePermissions(UserUtils.adminUser);

        dashboardIsVisibleAndEditableCannotChangePermissions(dashboardAdmin2);

        dashboardIsNotVisible(dashboardUser1);

        UserUtils.switchUser(dashboardAdmin1);
        DataExplorerUtils.goToDashboard();
        PermissionUtils.changeOwnership(dashboardName, dashboardAdmin2.email);

        dashboardIsNotVisible(dashboardAdmin1);

        dashboardIsVisibleAndEditableCanChangePermissions(UserUtils.adminUser);

        dashboardIsVisibleAndEditableCanChangePermissions(dashboardAdmin2);

        dashboardIsNotVisible(dashboardUser1);
    });

    it('Dashboard is shared with group for user 2', () => {
        const dashboardAdminGroup = 'dashboard_admin_group';
        UserUtils.createGroup(
            dashboardAdminGroup,
            UserRole.ROLE_DASHBOARD_ADMIN,
        );
        UserUtils.addGroupToUser(dashboardAdminGroup, dashboardAdmin2.name);

        UserUtils.switchUser(dashboardAdmin1);
        DataExplorerUtils.createNewDashboard(dashboardName);

        PermissionUtils.authorizeGroup(dashboardName, dashboardAdminGroup);

        dashboardIsVisibleAndEditableCanChangePermissions(UserUtils.adminUser);

        dashboardIsNotVisible(dashboardUser1);

        dashboardIsVisibleAndEditableCannotChangePermissions(dashboardAdmin2);
    });

    it('Test Dashboard and Data Explorer Permissions', () => {
        UserUtils.switchUser(dashboardAdmin1);

        ConnectUtils.addMachineDataSimulator('simulator', true);
        addChart('chart1');
        cy.wait(1000);
        addChart('chart2');

        DataExplorerUtils.createNewDashboard(dashboardName);

        DataExplorerUtils.editDashboard(dashboardName);
        DataExplorerUtils.addDataViewToDashboard('chart1', true);
        DataExplorerUtils.addDataViewToDashboard('chart2', true);
        DataExplorerUtils.saveDashboard();

        PermissionUtils.markElementAsPublic(dashboardName);

        UserUtils.switchUser(dashboardAdmin2);
        DataExplorerUtils.goToDashboard();
        DataExplorerUtils.viewDashboard(dashboardName);
        DataExplorerBtns.moreOptionsBtn('chart1').should('exist');
        DataExplorerBtns.moreOptionsBtn('chart2').should('exist');
        DataExplorerBtns.removeWidgetBtn('chart1').should('not.exist');
        DataExplorerBtns.removeWidgetBtn('chart2').should('not.exist');

        DataExplorerUtils.goToDashboard();
        DataExplorerUtils.editDashboard(dashboardName);
        DataExplorerBtns.moreOptionsBtn('chart1').should('exist');
        DataExplorerBtns.moreOptionsBtn('chart2').should('exist');
        DataExplorerBtns.removeWidgetBtn('chart1').should('exist');
        DataExplorerBtns.removeWidgetBtn('chart2').should('exist');

        // Validate to add new widget to dashboard
        DataExplorerBtns.createChartBtn().should('not.exist');
        DataExplorerBtns.removeWidgetBtn('chart2').click();
        DataExplorerUtils.saveDashboard();

        UserUtils.switchUser(dashboardUser1);
        DataExplorerUtils.goToDashboard();
        DataExplorerUtils.viewDashboard(dashboardName);
        DataExplorerBtns.moreOptionsBtn('chart1').should('exist');
        DataExplorerBtns.moreOptionsBtn('chart2').should('not.exist');
    });

    function dashboardIsVisibleAndEditableCanChangePermissions(user: User) {
        UserUtils.switchUser(user);
        DataExplorerUtils.checkAmountOfDashboards(1);
        DataExplorerUtils.checkDashboardCanBeEdited(dashboardName);

        PermissionUtils.validateUserCanChangePermissions(dashboardName);
    }

    function dashboardIsVisibleAndEditableCannotChangePermissions(user: User) {
        UserUtils.switchUser(user);
        DataExplorerUtils.checkAmountOfDashboards(1);
        DataExplorerUtils.checkDashboardCanBeEdited(dashboardName);

        PermissionUtils.validateUserCanNotChangePermissions(dashboardName);
    }

    function dashboardIsVisibleButNotEditable(user: User) {
        UserUtils.switchUser(user);
        DataExplorerUtils.checkAmountOfDashboards(1);
        DataExplorerUtils.checkDashboardCanNotBeEdited(dashboardName);

        PermissionUtils.validateUserCanNotChangePermissions(dashboardName);
    }

    function dashboardIsNotVisible(user: User) {
        UserUtils.switchUser(user);
        DataExplorerUtils.checkAmountOfDashboards(0);
    }

    function addChart(chartName: string) {
        DataExplorerUtils.addDataViewAndTableWidget(
            chartName,
            'simulator',
            true,
        );
        DataExplorerUtils.saveDataViewConfiguration();
    }
});
