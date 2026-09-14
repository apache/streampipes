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

describe('Test create dashboard from existing', () => {
    const sourceChart = 'SourceChart';
    const sourceDashboard = 'SourceDashboard';
    const dashboardCopy = 'DashboardCopy';

    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
        ChartUtils.loadDataIntoDataLake('datalake/sample.csv');

        ChartUtils.goToDatalake();
        ChartUtils.createTableChart(ChartUtils.ADAPTER_NAME);
        ChartUtils.saveChartConfiguration(false, false, sourceChart);

        ChartUtils.goToDashboard();
        ChartUtils.createAndEditDashboard(sourceDashboard);
        ChartUtils.addChartToDashboard(sourceChart, true);
        ChartUtils.saveDashboardConfiguration();
        ChartUtils.checkRowsDashboardTable(1);
    });

    it('Shares the existing charts by default', () => {
        ChartUtils.createDashboardFromExisting(sourceDashboard);
        // Asserts the prefilled copy title before renaming and storing
        ChartUtils.saveDashboardFromExisting(
            sourceDashboard,
            false,
            dashboardCopy,
        );

        ChartUtils.checkRowsDashboardTable(2);
        ChartUtils.checkDashboardListed(sourceDashboard);
        ChartUtils.checkDashboardListed(dashboardCopy);

        // Both dashboards use the same chart, no chart copy is created
        ChartUtils.goToDatalake();
        ChartUtils.checkRowsChartsTable(1);
        ChartUtils.checkChartListed(sourceChart);
    });

    it('Creates a copy of every chart when the option is selected', () => {
        ChartUtils.createDashboardFromExisting(sourceDashboard);
        ChartUtils.saveDashboardFromExisting(
            sourceDashboard,
            true,
            dashboardCopy,
        );

        ChartUtils.checkRowsDashboardTable(2);
        ChartUtils.checkDashboardListed(dashboardCopy);

        // The chart of the dashboard is copied as well
        ChartUtils.goToDatalake();
        ChartUtils.checkRowsChartsTable(2);
        ChartUtils.checkChartListed(sourceChart);
    });
});
