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
import { DataExplorerUtils } from '../../support/utils/dataExplorer/DataExplorerUtils';

describe('Test Deletion of Data View and Dashboard', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
        DataExplorerUtils.loadDataIntoDataLake('datalake/sample.csv');
    });

    it('Perform Test', () => {
        const dashboard = 'TestDashboard';
        const dataView = 'TestView';

        DataExplorerUtils.goToDatalake();

        DataExplorerUtils.addDataViewAndTableWidget(
            dataView,
            DataExplorerUtils.ADAPTER_NAME,
        );

        DataExplorerUtils.saveDataViewConfiguration();

        DataExplorerUtils.checkRowsViewsTable(1);

        DataExplorerUtils.goToDashboard();

        DataExplorerUtils.createAndEditDashboard(dashboard);

        DataExplorerUtils.addDataViewToDashboard(dataView, true);

        DataExplorerUtils.saveDashboardConfiguration();

        DataExplorerUtils.checkRowsDashboardTable(1);

        // Click "Delete" but cancel action and check if dashboard and view are still displayed
        DataExplorerUtils.cancelDeleteDashboard(dashboard);

        DataExplorerUtils.checkRowsDashboardTable(1);

        DataExplorerUtils.goToDatalake();

        DataExplorerUtils.cancelDeleteDataView(dataView);

        DataExplorerUtils.checkRowsViewsTable(1);

        DataExplorerUtils.deleteDataView(dataView);

        DataExplorerUtils.checkRowsViewsTable(0);

        DataExplorerUtils.goToDashboard();

        DataExplorerUtils.editDashboard(dashboard);

        // Validate that data view is removed from dashboard
        DataExplorerUtils.getEmptyDashboardInformation().should('be.visible');

        DataExplorerUtils.saveDashboardConfiguration();

        DataExplorerUtils.deleteDashboard(dashboard);

        DataExplorerUtils.checkRowsDashboardTable(0);
    });
});
