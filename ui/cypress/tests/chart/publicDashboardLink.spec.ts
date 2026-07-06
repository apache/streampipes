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

import { ChartUtils } from '../../support/utils/chart/ChartUtils';
import { DataLakeSeedUtils } from '../../support/utils/dataset/DataLakeSeedUtils';
import { Inspector } from '../../support/utils/dashboard/Inspector';
import { PermissionUtils } from '../../support/utils/user/PermissionUtils';

const dashboardName = 'public-dashboard';
const chartName = 'public-dashboard-chart';
const tableColumns = ['time', 'randombool', 'randomnumber', 'randomtext'];

describe('Public dashboard links', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
        DataLakeSeedUtils.importCsvData({
            headers: ['timestamp', 'randombool', 'randomnumber', 'randomtext'],
            rows: kioskTableRows(),
            measurementName: ChartUtils.ADAPTER_NAME,
            timestampColumn: 'timestamp',
            columnOverrides: {
                randomtext: {
                    propertyScope: 'DIMENSION_PROPERTY',
                },
            },
        });
    });

    it('allows logged-out users to view all table columns in kiosk mode', () => {
        createPublicDashboardWithTableChart();

        getDashboardId().then(dashboardId => {
            cy.logout();
            cy.location('hash', { timeout: 10000 }).should('eq', '#/login');
            Inspector.openDashboardKioskAsLoggedOutUser(dashboardId);
            Inspector.validateDashboardKioskWithTableChart(
                dashboardName,
                tableColumns,
            );
        });
    });
});

function createPublicDashboardWithTableChart(): void {
    ChartUtils.addDataViewAndTableWidget(ChartUtils.ADAPTER_NAME);
    ChartUtils.saveDataViewConfiguration(false, false, chartName);

    ChartUtils.goToDashboard();
    ChartUtils.createAndEditDashboard(dashboardName);
    ChartUtils.addDataViewToDashboard(chartName, true);
    ChartUtils.saveDashboardConfiguration();

    PermissionUtils.markElementAsAnonymousPublic(dashboardName);
    PermissionUtils.validateAnonymousPublicLinkIsEnabled(dashboardName);
}

function getDashboardId() {
    return Inspector.getDashboardIdByName(dashboardName);
}

function kioskTableRows(): string[][] {
    const baseTimestamp = Date.now() - 60_000;

    return [
        [baseTimestamp.toString(), 'true', '62.0', 'c'],
        [(baseTimestamp + 1_000).toString(), 'false', '46.0', 'a'],
        [(baseTimestamp + 2_000).toString(), 'true', '41.0', 'b'],
    ];
}
