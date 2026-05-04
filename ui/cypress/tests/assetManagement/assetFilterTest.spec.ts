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

import { AssetUtils } from '../../support/utils/asset/AssetUtils';
import { DashboardUtils } from '../../support/utils/DashboardUtils';
import { ChartUtils } from '../../support/utils/chart/ChartUtils';
import { AssetBuilder } from '../../support/builder/AssetBuilder';
import { ConfigurationUtils } from '../../support/utils/configuration/ConfigurationUtils';
import { SiteUtils } from '../../support/utils/configuration/SiteUtils';
import { FilterUtils } from '../../support/utils/filter/FilterUtils';
import { ConnectUtils } from '../../support/utils/connect/ConnectUtils';
import { AdapterBuilder } from '../../support/builder/AdapterBuilder';
import { PipelineUtils } from '../../support/utils/pipeline/PipelineUtils';
import { DatasetUtils } from '../../support/utils/dataset/DatasetUtils';

describe('Test asset filters', () => {
    const label1 = 'label1';
    const label2 = 'label2';
    const label3 = 'label3';

    const site1 = 'site1';
    const site2 = 'site2';
    const site3 = 'site3';
    const adapter1 = 'adapter-1_0';
    const adapter1_1 = 'adapter-1_1';
    const adapter1_2 = 'adapter-1_2';
    const adapter2 = 'adapter-2_0';
    const adapter2_1 = 'adapter-2_1';
    const adapter2_2 = 'adapter-2_2';
    const adapter3 = 'adapter-3_0';
    const adapter3_1 = 'adapter-3_1';
    const adapter3_2 = 'adapter-3_2';
    const pipeline1 = 'Persist ' + adapter1;
    const pipeline1_1 = 'Persist ' + adapter1_1;
    const pipeline1_2 = 'Persist ' + adapter1_2;
    const pipeline2 = 'Persist ' + adapter2;
    const pipeline2_1 = 'Persist ' + adapter2_1;
    const pipeline2_2 = 'Persist ' + adapter2_2;
    const pipeline3 = 'Persist ' + adapter3;
    const pipeline3_1 = 'Persist ' + adapter3_1;
    const pipeline3_2 = 'Persist ' + adapter3_2;

    const asset1 = AssetBuilder.create('asset-1_0')
        .addLabel(label1)
        .setSite(site1)
        .setAssetType('PRODUCTION_LINE')
        .addSubAsset(
            AssetBuilder.create('asset-1_1')
                .addLabel(label2)
                .setAssetType('WORK_CELL')
                .build(),
        )
        .addSubAsset(
            AssetBuilder.create('asset-1_2')
                .addLabel(label3)
                .setAssetType('WORK_CELL')
                .build(),
        )
        .build();

    const asset2 = AssetBuilder.create('asset-2_0')
        .addLabel(label1)
        .setSite(site2)
        .setAssetType('PRODUCTION_LINE')
        .addSubAsset(
            AssetBuilder.create('asset-2_1')
                .addLabel(label2)
                .setAssetType('WORK_CELL')
                .build(),
        )
        .addSubAsset(
            AssetBuilder.create('asset-2_2')
                .addLabel(label3)
                .setAssetType('WORK_CELL')
                .build(),
        )
        .build();

    const asset3 = AssetBuilder.create('asset-3_0')
        .addLabel(label1)
        .setSite(site3)
        .setAssetType('PRODUCTION_LINE')
        .addSubAsset(
            AssetBuilder.create('asset-3_1')
                .addLabel(label2)
                .setAssetType('WORK_CELL')
                .build(),
        )
        .addSubAsset(
            AssetBuilder.create('asset-3_2')
                .addLabel(label3)
                .setAssetType('WORK_CELL')
                .build(),
        )
        .build();

    beforeEach('Setup Test', () => {
        const assetResourceFixtureDirectory = 'assetResources';

        cy.initStreamPipesTest();
        //AssetUtils.importAssetResources(assetResourceFixtureDirectory);

        prepareLabels();
        prepareSites();
        prepareAssets();
        preparePersistedAdapters();
        prepareDashboards();
        DashboardUtils.goToDashboard();
        DashboardUtils.checkAmountOfDashboards(9);
        DashboardUtils.checkInList([
            'dashboard-1_0',
            'dashboard-1_1',
            'dashboard-1_2',
            'dashboard-2_0',
            'dashboard-2_1',
            'dashboard-2_2',
            'dashboard-3_0',
            'dashboard-3_1',
            'dashboard-3_2',
        ]);

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
    /**
    it('Filter adapters', () => {
        ConnectUtils.goToConnect();
        //Select one asset
        FilterUtils.clearFilter();
        FilterUtils.filterAssets(['asset-1_0']);
        checkTableResources('all-adapters-table', [adapter1, adapter1_1, adapter1_2]);
        // Select asset 1 & asset 2
        FilterUtils.clearFilter();
        FilterUtils.filterAssets(['asset-1_0','asset-2_0']);
        checkTableResources('all-adapters-table', [adapter1, adapter1_1, adapter1_2,adapter2, adapter2_1,adapter2_2]);
        //Select one label
        FilterUtils.clearFilter();
        FilterUtils.filterLabels(['label3']);
        checkTableResources('all-adapters-table', [adapter1_2,adapter2_2,adapter3_2]);
        //Select label 2 & 3
        FilterUtils.clearFilter();
        FilterUtils.filterLabels(['label2','label3']);
        checkTableResources('all-adapters-table', [adapter1_1,adapter1_2,adapter2_1,adapter2_2,adapter3_1,adapter3_2]);
        //Select  one site 
        FilterUtils.clearFilter();
        FilterUtils.filterSites(['site1']);
        checkTableResources('all-adapters-table', [adapter1, adapter1_1,adapter1_2]);
        //Select site 1 &  site 2
        FilterUtils.clearFilter();
        FilterUtils.filterSites(['site1','site2']);
        checkTableResources('all-adapters-table', [adapter1, adapter1_1, adapter1_2,adapter2, adapter2_1,adapter2_2]);
        //Select one type
        FilterUtils.clearFilter();
        FilterUtils.filterTypes(['WORK_CELL']);
        checkTableResources('all-adapters-table', [ adapter1_1, adapter1_2,adapter2_1, adapter2_2, adapter3_1,adapter3_2]);
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
        checkTableResources('all-pipelines-table', [pipeline1, pipeline1_1, pipeline1_2]);
        // Select asset 1 & asset 2
        FilterUtils.clearFilter();
        FilterUtils.filterAssets(['asset-1_0','asset-2_0']);
        checkTableResources('all-pipelines-table', [pipeline1, pipeline1_1, pipeline1_2,  pipeline2, pipeline2_1, pipeline2_2]);
        //select ine label
        FilterUtils.clearFilter();
        FilterUtils.filterLabels(['label3']);
        checkTableResources('all-pipelines-table', [pipeline1_2,pipeline2_2,pipeline3_2]);
        //Select label 2 & 3
        FilterUtils.clearFilter();
        FilterUtils.filterLabels(['label2','label3']);
        checkTableResources('all-pipelines-table', [pipeline1_1,pipeline1_2, pipeline2_1,pipeline2_2,pipeline3_1,pipeline3_2]);
        //Select  one site 
        FilterUtils.clearFilter();
        FilterUtils.filterSites(['site1']);
        checkTableResources('all-pipelines-table', [pipeline1,pipeline1_1,pipeline1_2]);
        //Select site 1 &  site 2
        FilterUtils.clearFilter();
        FilterUtils.filterSites(['site1','site2']);
        checkTableResources('all-pipelines-table', [pipeline1,pipeline1_1,pipeline1_2, pipeline2, pipeline2_1,pipeline2_2]);
        //select one type
        FilterUtils.clearFilter();
        FilterUtils.filterTypes(['WORK_CELL']);
        checkTableResources('all-pipelines-table', [pipeline1_1,pipeline1_2,pipeline2_1, pipeline2_2, pipeline3_1,pipeline3_2]);
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
        checkTableResources('datalake-settings', [adapter1, adapter1_1, adapter1_2]);
        // Select asset 1 & asset 2
        FilterUtils.clearFilter();
        FilterUtils.filterAssets(['asset-1_0','asset-2_0']);
        checkTableResources('datalake-settings', [adapter1, adapter1_1, adapter1_2,adapter2, adapter2_1,adapter2_2]);
        //select one label
            FilterUtils.clearFilter();
        FilterUtils.filterLabels(['label3']);
        checkTableResources('datalake-settings', [adapter1_2,adapter2_2,adapter3_2]);
        //Select label 2 & 3
        FilterUtils.clearFilter();
        FilterUtils.filterLabels(['label2','label3']);
        checkTableResources('datalake-settings', [adapter1_1,adapter1_2,adapter2_1,adapter2_2,adapter3_1,adapter3_2]);
        //Select site 1
        FilterUtils.clearFilter();
        FilterUtils.filterSites(['site1']);
        checkTableResources('datalake-settings', [adapter1, adapter1_1,adapter1_2]);

        FilterUtils.clearFilter();
        FilterUtils.filterSites(['site1','site2']);
        checkTableResources('datalake-settings', [adapter1, adapter1_1, adapter1_2,adapter2, adapter2_1,adapter2_2]);
        //select one type
        FilterUtils.clearFilter();
        FilterUtils.filterTypes(['WORK_CELL']);
        checkTableResources('datalake-settings', [adapter1_1, adapter1_2,adapter2_1, adapter2_2, adapter3_1,adapter3_2]);

         // Select asset 1 & site 1 & label 2
        FilterUtils.clearFilter();
        FilterUtils.filterAssets(['asset-1_0']);
        FilterUtils.filterSites(['site1']);
        FilterUtils.filterLabels(['label2']);
        checkTableResources('datalake-settings', [adapter1_1]);
    });*/

    function prepareAssets() {
        AssetUtils.goToAssets();
        AssetUtils.addAndSaveAsset(asset1);
        AssetUtils.addAndSaveAsset(asset2);
        AssetUtils.addAndSaveAsset(asset3);
    }

    function preparePersistedAdapters() {
        createPersistedAdapterWithAssetLink(adapter1, 'asset-1_0');
        createPersistedAdapterWithAssetLink(adapter1_1, 'asset-1_0.asset-1_1');
        createPersistedAdapterWithAssetLink(adapter1_2, 'asset-1_0.asset-1_2');
        createPersistedAdapterWithAssetLink(adapter2, 'asset-2_0');
        createPersistedAdapterWithAssetLink(adapter2_1, 'asset-2_0.asset-2_1');
        createPersistedAdapterWithAssetLink(adapter2_2, 'asset-2_0.asset-2_2');
        createPersistedAdapterWithAssetLink(adapter3, 'asset-3_0');
        createPersistedAdapterWithAssetLink(adapter3_1, 'asset-3_0.asset-3_1');
        createPersistedAdapterWithAssetLink(adapter3_2, 'asset-3_0.asset-3_2');
    }

    function createPersistedAdapterWithAssetLink(
        adapterName: string,
        assetName: string,
    ) {
        const adapterConfiguration = AdapterBuilder.create(
            'Machine_Data_Simulator',
        )
            .setName(adapterName + '_' + assetName)
            .setTimestampProperty('timestamp')
            .setStoreInDataLake()
            .addInput('input', 'wait-time-ms', '1000')
            .build();

        ConnectUtils.addAdapterWithLinkedAssets(adapterConfiguration, [
            assetName,
        ]);
    }

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

    function prepareDashboards() {
        ChartUtils.createNewDashboardWithAssetLinks('dashboard-1_0', [
            'asset-1_0',
        ]);
        ChartUtils.createNewDashboardWithAssetLinks('dashboard-1_1', [
            'asset-1_0.asset-1_1',
        ]);
        ChartUtils.createNewDashboardWithAssetLinks('dashboard-1_2', [
            'asset-1_0.asset-1_2',
        ]);

        ChartUtils.createNewDashboardWithAssetLinks('dashboard-2_0', [
            'asset-2_0',
        ]);
        ChartUtils.createNewDashboardWithAssetLinks('dashboard-2_1', [
            'asset-2_0.asset-2_1',
        ]);
        ChartUtils.createNewDashboardWithAssetLinks('dashboard-2_2', [
            'asset-2_0.asset-2_2',
        ]);

        ChartUtils.createNewDashboardWithAssetLinks('dashboard-3_0', [
            'asset-3_0',
        ]);
        ChartUtils.createNewDashboardWithAssetLinks('dashboard-3_1', [
            'asset-3_0.asset-3_1',
        ]);
        ChartUtils.createNewDashboardWithAssetLinks('dashboard-3_2', [
            'asset-3_0.asset-3_2',
        ]);
    }

    function prepareLabels() {
        ConfigurationUtils.goToLabelConfiguration();
        ConfigurationUtils.addNewLabel(label1);
        ConfigurationUtils.addNewLabel(label2);
        ConfigurationUtils.addNewLabel(label3);
    }

    function prepareSites() {
        ConfigurationUtils.goToSitesConfiguration();
        SiteUtils.createNewSite(site1);
        SiteUtils.createNewSite(site2);
        SiteUtils.createNewSite(site3);
    }
});
