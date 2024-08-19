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
import { DataLakeUtils } from '../../support/utils/datalake/DataLakeUtils';

describe('Test Deletion of Data View and Dashboard', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
        DataLakeUtils.loadDataIntoDataLake('datalake/sample.csv', false);
    });

    it('Perform Test', () => {
        DataLakeUtils.goToDatalake();

        DataLakeUtils.addDataViewAndTableWidget('TestView', 'Persist');

        DataLakeUtils.saveDataViewConfiguration();

        DataLakeUtils.createAndEditDashboard('TestDashboard');

        DataLakeUtils.saveDashboardConfiguration();

        DataLakeUtils.checkRowsDashboardTable(1);

        DataLakeUtils.checkRowsViewsTable(1);

        // Click "Delete" but cancel action and check if dashboard and view are still displayed
        DataLakeUtils.cancelDeleteDashboard('TestDashboard');

        DataLakeUtils.checkRowsDashboardTable(1);

        DataLakeUtils.cancelDeleteDataView('TestView');

        DataLakeUtils.checkRowsViewsTable(1);

        DataLakeUtils.deleteDashboard('TestDashboard');

        DataLakeUtils.deleteDataView('TestView');

        DataLakeUtils.checkRowsDashboardTable(0);

        DataLakeUtils.checkRowsViewsTable(0);
    });
});
