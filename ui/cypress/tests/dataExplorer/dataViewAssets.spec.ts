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
import { DataExplorerUtils } from '../../support/utils/dataExplorer/DataExplorerUtils';

describe('Creates a new adapter with a linked asset', () => {
    const assetName1 = 'TestAsset1';
    const assetName2 = 'TestAsset2';
    const assetName3 = 'TestAsset3';

    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
        AssetUtils.goToAssets();
        AssetUtils.addAndSaveAsset(assetName3);
        AssetUtils.addAndSaveAsset(assetName2);
        AssetUtils.addAndSaveAsset(assetName1);
    });

    it('Add Assets during Chart generation', () => {
        DataExplorerUtils.createDataViewWithAssets([assetName1, assetName2]);
        //Test
        AssetUtils.checkAmountOfLinkedResourcesByAssetName(assetName1, 1);
        AssetUtils.checkAmountOfLinkedResourcesByAssetName(assetName2, 1);
    });

    it('Edit Assets during Chart generation', () => {
        DataExplorerUtils.createDataViewWithAssets([assetName1, assetName2]);
        //Test
        AssetUtils.checkAmountOfLinkedResourcesByAssetName(assetName1, 1);
        AssetUtils.checkAmountOfLinkedResourcesByAssetName(assetName2, 1);

        // Go To Chart and Edit
        DataExplorerUtils.goToDatalake();
        DataExplorerUtils.editDataView('NewWidget');
        DataExplorerUtils.renameWidget('Rename');

        DataExplorerUtils.saveToAddAssets();
        DataExplorerUtils.addToAsset([assetName1, assetName3]);

        AssetUtils.checkAmountOfLinkedResourcesByAssetName(assetName2, 1);
        AssetUtils.checkAmountOfLinkedResourcesByAssetName(assetName3, 1);
        AssetUtils.checkResourceNamingByAssetName(assetName2, 'Rename');
    });
});
