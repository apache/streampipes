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
import { AssetBtns } from '../../support/utils/asset/AssetBtns';

describe('Test create asset from existing', () => {
    const sourceAsset = 'asset-1_0';
    const assetCopy = 'AssetCopy';
    const subAssets = ['asset-1_1', 'asset-1_2'];
    const linkedResources = 5;

    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
        cy.importAssetResources();
        AssetUtils.waitForAssets(['asset-1_0', 'asset-2_0', 'asset-3_0']);
        // This is currently required because the assets are only loaded on page load
        cy.reload();
        AssetUtils.goToAssets();
        AssetUtils.checkAmountOfAssets(3);
    });

    it('Copies the whole hierarchy without the resource links', () => {
        // Asserts the prefilled copy name
        AssetUtils.createAssetFromExisting(sourceAsset);

        subAssets.forEach(subAsset => AssetUtils.checkSubAssetExists(subAsset));
        // Resource links are intentionally not copied
        AssetUtils.checkAmountOfLinkedResources(0);

        AssetUtils.renameAsset(assetCopy);
        AssetBtns.saveAssetBtn().click();
        AssetBtns.createAssetBtn().should('be.visible');

        AssetUtils.checkAmountOfAssets(4);
        AssetUtils.checkAssetListed(sourceAsset);
        AssetUtils.checkAssetListed(assetCopy);

        // The source asset keeps its resource links
        AssetUtils.checkAmountOfLinkedResourcesByAssetName(
            sourceAsset,
            linkedResources,
        );
    });

    it('Does not store the copy when the details view is left without saving', () => {
        AssetUtils.createAssetFromExisting(sourceAsset);
        AssetUtils.goToAssets();

        AssetUtils.checkAmountOfAssets(3);
        AssetUtils.checkAssetListed(sourceAsset);
    });
});
