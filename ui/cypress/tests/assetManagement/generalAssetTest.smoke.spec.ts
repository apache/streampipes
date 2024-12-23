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
import { AssetBtns } from '../../support/utils/asset/AssetBtns';

describe('Creates a new adapter, add to assets', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
    });

    it('Perform Test', () => {
        const assetName = 'TestAsset';

        AssetUtils.addAssetWithOneAdapter(assetName);

        // // Leave and navigate back to Assets
        DashboardUtils.goToDashboard();
        AssetUtils.goToAssets();
        AssetUtils.checkAmountOfAssets(1);

        // Check that everything was stored correctly
        AssetUtils.editAsset(assetName);
        AssetBtns.assetLinksTab().click();
        AssetUtils.checkAmountOfLinkedResources(2);

        // Check that deletion of asset works
        AssetUtils.goToAssets();
        AssetUtils.checkAmountOfAssets(1);
        AssetUtils.deleteAsset(assetName);
        AssetUtils.checkAmountOfAssets(0);
    });
});
