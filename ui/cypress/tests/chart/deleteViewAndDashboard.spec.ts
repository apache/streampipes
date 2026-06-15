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

describe('Test Deletion of Data View and Dashboard', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
        ChartUtils.loadDataIntoDataLake('datalake/sample.csv');
    });

    it('Perform Test', () => {
        const dashboard = 'TestDashboard';
        const dataView = 'TestView';

        ChartUtils.goToDatalake();

        ChartUtils.addDataViewAndTableWidget(ChartUtils.ADAPTER_NAME);

        ChartUtils.saveDataViewConfiguration(false, false, dataView);

        ChartUtils.checkRowsViewsTable(1);

        ChartUtils.goToDashboard();

        ChartUtils.createAndEditDashboard(dashboard);

        ChartUtils.addDataViewToDashboard(dataView, true);

        ChartUtils.saveDashboardConfiguration();

        ChartUtils.checkRowsDashboardTable(1);

        // Click "Delete" but cancel action and check if dashboard and view are still displayed
        ChartUtils.cancelDeleteDashboard(dashboard);

        ChartUtils.checkRowsDashboardTable(1);

        ChartUtils.goToDatalake();

        ChartUtils.cancelDeleteDataView(dataView);

        ChartUtils.checkRowsViewsTable(1);

        ChartUtils.deleteDataView(dataView);

        ChartUtils.checkRowsViewsTable(0);

        ChartUtils.goToDashboard();

        ChartUtils.editDashboard(dashboard);

        // Validate that data view is removed from dashboard
        ChartUtils.getEmptyDashboardInformation().should('be.visible');

        ChartUtils.saveDashboardConfiguration();

        ChartUtils.deleteDashboard(dashboard);

        ChartUtils.checkRowsDashboardTable(0);
    });
});
