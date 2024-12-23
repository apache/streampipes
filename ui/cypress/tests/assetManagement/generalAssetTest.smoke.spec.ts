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

import { ConnectUtils } from '../../support/utils/connect/ConnectUtils';
import { AssetUtils } from '../../support/utils/asset/AssetUtils';
import { DashboardUtils } from '../../support/utils/DashboardUtils';
import { AssetBtns } from '../../support/utils/asset/AssetBtns';

describe('Creates a new adapter, add to assets', () => {
    const adapterName = 'Machine_Data_Simulator';

    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
        ConnectUtils.addMachineDataSimulator(adapterName);
    });

    it('Perform Test', () => {
        // Create new asset from adapters
        AssetUtils.goToAssets();

        AssetUtils.addNewAsset('Test Asset');

        AssetBtns.assetLinksTab().click();
        AssetUtils.openManageAssetLinks();

        AssetUtils.selectAdapterAssetLink(adapterName);
        AssetUtils.selectDataStreamAssetLink(adapterName);
        AssetBtns.updateAssetLinksBtn().click();

        AssetUtils.checkAmountOfLinkedResources(2);
        AssetBtns.saveAssetBtn().click();
        AssetUtils.goBackToOverview();

        // // Leave and navigate back to Assets
        DashboardUtils.goToDashboard();
        AssetUtils.goToAssets();
        AssetUtils.checkAmountOfAssets(1);
    });
});
