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
import { AdapterBuilder } from '../../support/builder/AdapterBuilder';
import { AssetUtils } from '../../support/utils/asset/AssetUtils';
import { ConnectBtns } from '../../support/utils/connect/ConnectBtns';
import { AssetBtns } from '../../support/utils/asset/AssetBtns';

describe('Creates a new adapter with a linked asset', () => {
    const assetName = 'TestAsset';
    const assetName2 = 'TestAsset2';
    const assetName3 = 'TestAsset3';
    const adapterConfiguration = AdapterBuilder.create('Machine_Data_Simulator')
        .setName('Machine Data Simulator Test')
        .addInput('input', 'wait-time-ms', '1000')
        .setStartAdapter(false)
        .build();

    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();

        AssetUtils.addAssetWithNoAdapter(assetName3);
        AssetUtils.checkAmountOfAssets(1);
        AssetUtils.addAssetWithNoAdapter(assetName2);
        AssetUtils.checkAmountOfAssets(2);
        AssetUtils.addAssetWithNoAdapter(assetName);
        AssetUtils.checkAmountOfAssets(3);
    });

    it('Add Assets during Adapter generation', () => {
        // Create

        ConnectUtils.addAdapterWithLinkedAssets(adapterConfiguration, [
            assetName,
        ]);

        // Go Back to Asset
        AssetUtils.goToAssets();
        // CLick on Asset

        AssetUtils.editAsset(assetName);
        AssetBtns.assetLinksTab().click();

        //Check if Link is there
        AssetUtils.checkAmountOfLinkedResources(2);
    });

    it('Edit Assets during Adapter editing', () => {
        // Add the first two Asssets by default
        ConnectUtils.addAdapterWithLinkedAssets(adapterConfiguration, [
            assetName,
            assetName2,
        ]);

        //Check if Added Correctly
        AssetUtils.checkAmountOfLinkedResourcesByAssetName(assetName, 2);
        AssetUtils.checkAmountOfLinkedResourcesByAssetName(assetName2, 2);

        //Edit
        ConnectUtils.goToConnect();
        ConnectBtns.openActionsMenu('Machine Data Simulator Test');
        ConnectBtns.editAdapter().should('not.be.disabled');
        ConnectBtns.editAdapter().click();

        // Go over the first two steps
        ConnectBtns.nextBtn().click();
        ConnectUtils.finishEventSchemaConfiguration();

        // Rename
        cy.dataCy('sp-adapter-name').clear().type('Changed');
        cy.dataCy('sp-adapter-name').should('have.value', 'Changed');

        // Deselect Asset 2
        ConnectUtils.editAsset([assetName]);

        // Select Asset 3
        ConnectUtils.editAsset([assetName3]);

        ConnectBtns.storeEditAdapter().click();

        cy.dataCy('sp-connect-adapter-success-added', {
            timeout: 60000,
        }).should('be.visible');

        ConnectUtils.closeAdapterPreview();

        // Test Number of Asset Links
        AssetUtils.checkAmountOfLinkedResourcesByAssetName(assetName2, 2);
        AssetUtils.checkAmountOfLinkedResourcesByAssetName(assetName3, 2);

        // Test Renaming
        AssetUtils.checkResourceNamingByAssetName(assetName2, 'Changed');
    });
});
