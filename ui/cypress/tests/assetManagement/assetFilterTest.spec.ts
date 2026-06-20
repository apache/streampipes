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

import { DashboardUtils } from '../../support/utils/DashboardUtils';
import { FilterUtils } from '../../support/utils/filter/FilterUtils';
import { ConnectUtils } from '../../support/utils/connect/ConnectUtils';
import { PipelineUtils } from '../../support/utils/pipeline/PipelineUtils';
import { DatasetUtils } from '../../support/utils/dataset/DatasetUtils';
import { AssetUtils } from '../../support/utils/asset/AssetUtils';

describe('Test asset filters', () => {
    const adapter1 = 'adapter-1_0';
    const adapter1_1 = 'adapter-1_1';
    const adapter1_2 = 'adapter-1_2';
    const adapter2 = 'adapter-2_0';
    const adapter2_1 = 'adapter-2_1';
    const adapter2_2 = 'adapter-2_2';
    const adapter3_1 = 'adapter-3_1';
    const adapter3_2 = 'adapter-3_2';
    const pipeline1 = 'Persist ' + adapter1;
    const pipeline1_1 = 'Persist ' + adapter1_1;
    const pipeline1_2 = 'Persist ' + adapter1_2;
    const pipeline2 = 'Persist ' + adapter2;
    const pipeline2_1 = 'Persist ' + adapter2_1;
    const pipeline2_2 = 'Persist ' + adapter2_2;
    const pipeline3_1 = 'Persist ' + adapter3_1;
    const pipeline3_2 = 'Persist ' + adapter3_2;

    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
        cy.importAssetResources();
        // This is currently required because the assets are only loaded on page load
        cy.reload();
    });

    it('Filter Dashboards', () => {
        DashboardUtils.goToDashboard();
        // Select one asset
        FilterUtils.clearFilter();
        FilterUtils.filterAssets(['asset-1_0']);
        DashboardUtils.checkInList([
            'dashboard-1_0',
            'dashboard-1_1',
            'dashboard-1_2',
        ]);
        // Select asset 1 & asset 2
        FilterUtils.clearFilter();
        FilterUtils.filterAssets(['asset-1_0', 'asset-2_0']);
        DashboardUtils.checkInList([
            'dashboard-1_0',
            'dashboard-1_1',
            'dashboard-1_2',
            'dashboard-2_0',
            'dashboard-2_1',
            'dashboard-2_2',
        ]);
        // Select site 1
        FilterUtils.clearFilter();
        FilterUtils.filterSites(['site1']);
        DashboardUtils.checkInList([
            'dashboard-1_0',
            'dashboard-1_1',
            'dashboard-1_2',
        ]);
        // Select site 1 & site 2
        FilterUtils.clearFilter();
        FilterUtils.filterSites(['site1', 'site2']);
        DashboardUtils.checkInList([
            'dashboard-1_0',
            'dashboard-1_1',
            'dashboard-1_2',
            'dashboard-2_0',
            'dashboard-2_1',
            'dashboard-2_2',
        ]);
        // Select label 3
        FilterUtils.clearFilter();
        FilterUtils.filterLabels(['label3']);

        DashboardUtils.checkInList([
            'dashboard-1_2',
            'dashboard-2_2',
            'dashboard-3_2',
        ]);
        // Select label 2 & 3
        FilterUtils.clearFilter();
        FilterUtils.filterLabels(['label2', 'label3']);
        DashboardUtils.checkInList([
            'dashboard-1_1',
            'dashboard-1_2',
            'dashboard-2_1',
            'dashboard-2_2',
            'dashboard-3_1',
            'dashboard-3_2',
        ]);
        // Select type PRODUCTION_LINE
        FilterUtils.clearFilter();
        FilterUtils.filterTypes(['PRODUCTION_LINE']);
        DashboardUtils.checkInList([
            'dashboard-1_0',
            'dashboard-2_0',
            'dashboard-3_0',
        ]);
        FilterUtils.clearFilter();
        FilterUtils.filterTypes(['WORK_CELL']);
        DashboardUtils.checkInList([
            'dashboard-1_1',
            'dashboard-1_2',
            'dashboard-2_1',
            'dashboard-2_2',
            'dashboard-3_1',
            'dashboard-3_2',
        ]);
        // Select asset 1 & site 1 & label 2
        FilterUtils.clearFilter();
        FilterUtils.filterAssets(['asset-1_0']);
        FilterUtils.filterSites(['site1']);
        FilterUtils.filterLabels(['label2']);
        DashboardUtils.checkInList(['dashboard-1_1']);

        AssetUtils.goToAssets();

        FilterUtils.clearFilter();
        FilterUtils.filterAssets(['asset-1_0']);
        AssetUtils.checkAmountOfAssets(1);

        FilterUtils.clearFilter();
        FilterUtils.filterLabels(['label2']);
        AssetUtils.checkAmountOfAssets(3);

        FilterUtils.clearFilter();
        FilterUtils.filterTypes(['PRODUCTION_LINE']);
        AssetUtils.checkAmountOfAssets(3);

        FilterUtils.clearFilter();
        FilterUtils.filterSites(['site2']);
        AssetUtils.checkAmountOfAssets(1);
    });

    it('Filter adapters', () => {
        ConnectUtils.goToConnect();
        //Select one asset
        FilterUtils.clearFilter();
        FilterUtils.filterAssets(['asset-1_0']);
        checkTableResources('all-adapters-table', [
            adapter1,
            adapter1_1,
            adapter1_2,
        ]);
        // Select asset 1 & asset 2
        FilterUtils.clearFilter();
        FilterUtils.filterAssets(['asset-1_0', 'asset-2_0']);
        checkTableResources('all-adapters-table', [
            adapter1,
            adapter1_1,
            adapter1_2,
            adapter2,
            adapter2_1,
            adapter2_2,
        ]);
        //Select one label
        FilterUtils.clearFilter();
        FilterUtils.filterLabels(['label3']);
        checkTableResources('all-adapters-table', [
            adapter1_2,
            adapter2_2,
            adapter3_2,
        ]);
        //Select label 2 & 3
        FilterUtils.clearFilter();
        FilterUtils.filterLabels(['label2', 'label3']);
        checkTableResources('all-adapters-table', [
            adapter1_1,
            adapter1_2,
            adapter2_1,
            adapter2_2,
            adapter3_1,
            adapter3_2,
        ]);
        //Select  one site
        FilterUtils.clearFilter();
        FilterUtils.filterSites(['site1']);
        checkTableResources('all-adapters-table', [
            adapter1,
            adapter1_1,
            adapter1_2,
        ]);
        //Select site 1 &  site 2
        FilterUtils.clearFilter();
        FilterUtils.filterSites(['site1', 'site2']);
        checkTableResources('all-adapters-table', [
            adapter1,
            adapter1_1,
            adapter1_2,
            adapter2,
            adapter2_1,
            adapter2_2,
        ]);
        //Select one type
        FilterUtils.clearFilter();
        FilterUtils.filterTypes(['WORK_CELL']);
        checkTableResources('all-adapters-table', [
            adapter1_1,
            adapter1_2,
            adapter2_1,
            adapter2_2,
            adapter3_1,
            adapter3_2,
        ]);
        // Select asset 1 & site 1 & label 2
        FilterUtils.clearFilter();
        FilterUtils.filterAssets(['asset-1_0']);
        FilterUtils.filterSites(['site1']);
        FilterUtils.filterLabels(['label2']);
        checkTableResources('all-adapters-table', [adapter1_1]);
    });

    it('Filters pipelines', () => {
        PipelineUtils.goToPipelines();
        //select one asset
        FilterUtils.clearFilter();
        FilterUtils.filterAssets(['asset-1_0']);
        checkTableResources('all-pipelines-table', [
            pipeline1,
            pipeline1_1,
            pipeline1_2,
        ]);
        // Select asset 1 & asset 2
        FilterUtils.clearFilter();
        FilterUtils.filterAssets(['asset-1_0', 'asset-2_0']);
        checkTableResources('all-pipelines-table', [
            pipeline1,
            pipeline1_1,
            pipeline1_2,
            pipeline2,
            pipeline2_1,
            pipeline2_2,
        ]);
        //select ine label
        FilterUtils.clearFilter();
        FilterUtils.filterLabels(['label3']);
        checkTableResources('all-pipelines-table', [
            pipeline1_2,
            pipeline2_2,
            pipeline3_2,
        ]);
        //Select label 2 & 3
        FilterUtils.clearFilter();
        FilterUtils.filterLabels(['label2', 'label3']);
        checkTableResources('all-pipelines-table', [
            pipeline1_1,
            pipeline1_2,
            pipeline2_1,
            pipeline2_2,
            pipeline3_1,
            pipeline3_2,
        ]);
        //Select  one site
        FilterUtils.clearFilter();
        FilterUtils.filterSites(['site1']);
        checkTableResources('all-pipelines-table', [
            pipeline1,
            pipeline1_1,
            pipeline1_2,
        ]);
        //Select site 1 &  site 2
        FilterUtils.clearFilter();
        FilterUtils.filterSites(['site1', 'site2']);
        checkTableResources('all-pipelines-table', [
            pipeline1,
            pipeline1_1,
            pipeline1_2,
            pipeline2,
            pipeline2_1,
            pipeline2_2,
        ]);
        //select one type
        FilterUtils.clearFilter();
        FilterUtils.filterTypes(['WORK_CELL']);
        checkTableResources('all-pipelines-table', [
            pipeline1_1,
            pipeline1_2,
            pipeline2_1,
            pipeline2_2,
            pipeline3_1,
            pipeline3_2,
        ]);
        // Select asset 1 & site 1 & label 2
        FilterUtils.clearFilter();
        FilterUtils.filterAssets(['asset-1_0']);
        FilterUtils.filterSites(['site1']);
        FilterUtils.filterLabels(['label2']);
        checkTableResources('all-pipelines-table', [pipeline1_1]);
    });

    it('Filters datasets', () => {
        DatasetUtils.goToDatasets();
        //sekect one asset
        FilterUtils.clearFilter();
        FilterUtils.filterAssets(['asset-1_0']);
        checkTableResources('datalake-settings', [
            adapter1,
            adapter1_1,
            adapter1_2,
        ]);
        // Select asset 1 & asset 2
        FilterUtils.clearFilter();
        FilterUtils.filterAssets(['asset-1_0', 'asset-2_0']);
        checkTableResources('datalake-settings', [
            adapter1,
            adapter1_1,
            adapter1_2,
            adapter2,
            adapter2_1,
            adapter2_2,
        ]);
        //select one label
        FilterUtils.clearFilter();
        FilterUtils.filterLabels(['label3']);
        checkTableResources('datalake-settings', [
            adapter1_2,
            adapter2_2,
            adapter3_2,
        ]);
        //Select label 2 & 3
        FilterUtils.clearFilter();
        FilterUtils.filterLabels(['label2', 'label3']);
        checkTableResources('datalake-settings', [
            adapter1_1,
            adapter1_2,
            adapter2_1,
            adapter2_2,
            adapter3_1,
            adapter3_2,
        ]);
        //Select site 1
        FilterUtils.clearFilter();
        FilterUtils.filterSites(['site1']);
        checkTableResources('datalake-settings', [
            adapter1,
            adapter1_1,
            adapter1_2,
        ]);

        FilterUtils.clearFilter();
        FilterUtils.filterSites(['site1', 'site2']);
        checkTableResources('datalake-settings', [
            adapter1,
            adapter1_1,
            adapter1_2,
            adapter2,
            adapter2_1,
            adapter2_2,
        ]);
        //select one type
        FilterUtils.clearFilter();
        FilterUtils.filterTypes(['WORK_CELL']);
        checkTableResources('datalake-settings', [
            adapter1_1,
            adapter1_2,
            adapter2_1,
            adapter2_2,
            adapter3_1,
            adapter3_2,
        ]);

        // Select asset 1 & site 1 & label 2
        FilterUtils.clearFilter();
        FilterUtils.filterAssets(['asset-1_0']);
        FilterUtils.filterSites(['site1']);
        FilterUtils.filterLabels(['label2']);
        checkTableResources('datalake-settings', [adapter1_1]);
    });

    function checkTableResources(tableDataCy: string, resources: string[]) {
        cy.get(`[data-cy="${tableDataCy}"] tbody tr`, {
            timeout: 10000,
        }).should('have.length', resources.length);
        resources.forEach(resource => {
            cy.get(`[data-cy="${tableDataCy}"]`)
                .contains(resource)
                .should('exist');
        });
    }
});
