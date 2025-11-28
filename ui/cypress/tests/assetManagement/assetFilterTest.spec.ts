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
import { DataExplorerUtils } from '../../support/utils/dataExplorer/DataExplorerUtils';
import { AssetBuilder } from '../../support/builder/AssetBuilder';
import { ConfigurationUtils } from '../../support/utils/configuration/ConfigurationUtils';
import { SiteUtils } from '../../support/utils/configuration/SiteUtils';
import { FilterUtils } from '../../support/utils/filter/FilterUtils';

describe('Test asset filters', () => {
    const label1 = 'label1';
    const label2 = 'label2';
    const label3 = 'label3';

    const site1 = 'site1';
    const site2 = 'site2';
    const site3 = 'site3';

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
        cy.initStreamPipesTest();
        prepareLabels();
        prepareSites();
        prepareAssets();
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

    it('Perform Test', () => {
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

    function prepareAssets() {
        AssetUtils.goToAssets();
        AssetUtils.addAndSaveAsset(asset1);
        AssetUtils.addAndSaveAsset(asset2);
        AssetUtils.addAndSaveAsset(asset3);
    }

    function prepareDashboards() {
        DataExplorerUtils.createNewDashboardWithAssetLinks('dashboard-1_0', [
            'asset-1_0',
        ]);
        DataExplorerUtils.createNewDashboardWithAssetLinks('dashboard-1_1', [
            'asset-1_0.asset-1_1',
        ]);
        DataExplorerUtils.createNewDashboardWithAssetLinks('dashboard-1_2', [
            'asset-1_0.asset-1_2',
        ]);

        DataExplorerUtils.createNewDashboardWithAssetLinks('dashboard-2_0', [
            'asset-2_0',
        ]);
        DataExplorerUtils.createNewDashboardWithAssetLinks('dashboard-2_1', [
            'asset-2_0.asset-2_1',
        ]);
        DataExplorerUtils.createNewDashboardWithAssetLinks('dashboard-2_2', [
            'asset-2_0.asset-2_2',
        ]);

        DataExplorerUtils.createNewDashboardWithAssetLinks('dashboard-3_0', [
            'asset-3_0',
        ]);
        DataExplorerUtils.createNewDashboardWithAssetLinks('dashboard-3_1', [
            'asset-3_0.asset-3_1',
        ]);
        DataExplorerUtils.createNewDashboardWithAssetLinks('dashboard-3_2', [
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
