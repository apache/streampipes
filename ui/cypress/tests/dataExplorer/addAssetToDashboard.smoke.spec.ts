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
import { AssetBtns } from '../../support/utils/asset/AssetBtns';
import { AssetUtils } from '../../support/utils/asset/AssetUtils';
import { DataExplorerUtils } from '../../support/utils/dataExplorer/DataExplorerUtils';

describe('Test add Assets To Dashboard', () => {
    const assetName1 = 'TestAsset1';
    const assetName2 = 'TestAsset2';
    const assetName3 = 'TestAsset3';
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
        AssetUtils.goToAssets();
        AssetUtils.addAndSaveAsset(assetName3);
        AssetUtils.addAndSaveAsset(assetName2);
        AssetUtils.addAndSaveAsset(assetName1);
        DataExplorerUtils.loadDataIntoDataLake('datalake/sample.csv');
    });

    it('Create Dashboard and add Assets', () => {
        const dataView = 'TestView';

        const name = 'Dashboard1';

        const assetNameList = [assetName1, assetName2];
        DataExplorerUtils.createDashboardWithLinkedAssets(
            dataView,
            name,
            assetNameList,
        );

        //Go Back to Asset
        AssetUtils.goToAssets();
        AssetUtils.checkAmountOfAssetsGreaterThan(0);

        AssetUtils.editAsset(assetName1);
        AssetBtns.assetLinksTab().click();

        //Check if Link is there
        AssetUtils.checkAmountOfLinkedResources(1);
    });

    it('Edit Dashboard and edit Asset Links', () => {
        const dataView = 'TestView';

        const name = 'Dashboard1';

        const assetNameList = [assetName1, assetName2];
        DataExplorerUtils.createDashboardWithLinkedAssets(
            dataView,
            name,
            assetNameList,
        );
        DataExplorerUtils.editDashboardSettings(name);
        DataExplorerUtils.renameDashboard('NEW');
        const assetNameList2 = [assetName2, assetName3];
        DataExplorerUtils.addToAsset(assetNameList2);
        DataExplorerUtils.saveDataView();

        AssetUtils.checkAmountOfLinkedResourcesByAssetName(assetName1, 1);
        AssetUtils.checkAmountOfLinkedResourcesByAssetName(assetName3, 1);

        // Test Renaming
        AssetUtils.checkResourceNamingByAssetName(assetName1, 'NEW');
    });
});
