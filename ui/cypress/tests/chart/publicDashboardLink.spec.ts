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
import { Inspector } from '../../support/utils/dashboard/Inspector';
import { PermissionUtils } from '../../support/utils/user/PermissionUtils';

describe('Public dashboard links', () => {
    const dashboardName = 'public-dashboard';
    const chartName = 'public-dashboard-chart';

    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
        ChartUtils.loadDataIntoDataLake('datalake/sample.csv');
    });

    it('allows anonymous users to view a dashboard with a chart', () => {
        ChartUtils.addDataViewAndTableWidget(ChartUtils.ADAPTER_NAME);
        ChartUtils.saveDataViewConfiguration(false, false, chartName);

        ChartUtils.goToDashboard();
        ChartUtils.createAndEditDashboard(dashboardName);
        ChartUtils.addDataViewToDashboard(chartName, true);
        ChartUtils.saveDashboardConfiguration();

        PermissionUtils.markElementAsAnonymousPublic(dashboardName);
        PermissionUtils.validateAnonymousPublicLinkIsEnabled(dashboardName);

        Inspector.getDashboardIdByName(dashboardName).then(dashboardId => {
            Inspector.openDashboardKioskAsAnonymous(dashboardId);
            Inspector.validateDashboardKioskWithTableChart(dashboardName);
        });
    });
});
