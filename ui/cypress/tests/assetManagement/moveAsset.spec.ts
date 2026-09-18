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

import { AssetBuilder } from '../../support/builder/AssetBuilder';
import { AssetBtns } from '../../support/utils/asset/AssetBtns';
import { AssetUtils } from '../../support/utils/asset/AssetUtils';
import { ConfigurationUtils } from '../../support/utils/configuration/ConfigurationUtils';
import { SiteUtils } from '../../support/utils/configuration/SiteUtils';

describe('Move assets between hierarchies', () => {
    const source = 'Source';
    const child = 'Machine';
    const target = 'Target';
    const parent = 'ProductionLine';

    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
        AssetUtils.goToAssets();
    });

    const createHierarchy = (name: string, childName: string) => {
        AssetUtils.addAndSaveAsset(
            AssetBuilder.create(name)
                .addSubAssetBuilder(AssetBuilder.create(childName))
                .build(),
        );
    };

    it('Moves a complete model under a nested parent and preserves its hierarchy and IDs', () => {
        createHierarchy(source, child);
        createHierarchy(target, parent);
        AssetUtils.getStoredAssets().then(before => {
            const original = before.find(asset => asset.assetName === source)!;
            AssetUtils.openMoveAsset(source);
            AssetBtns.moveTarget(source).should('not.exist');
            AssetUtils.selectMoveTarget(target, parent);
            AssetBtns.moveSiteWarning().should('not.exist');
            AssetUtils.saveMove();
            AssetUtils.checkAmountOfAssets(1);
            AssetUtils.getStoredAssets().then(assets => {
                expect(assets.map(asset => asset.assetName)).to.deep.equal([
                    target,
                ]);
                const moved = assets[0].assets[0].assets[0];
                // Root document metadata is not part of a nested SpAsset.
                expect(moved).to.deep.equal({
                    additionalData: original.additionalData,
                    assetDescription: original.assetDescription,
                    assetId: original.assetId,
                    assetLinks: original.assetLinks,
                    assetName: original.assetName,
                    assetSite: original.assetSite,
                    assetType: original.assetType,
                    assets: original.assets,
                    labelIds: original.labelIds,
                });
            });
            cy.reload();
            AssetUtils.editAsset(target);
            AssetUtils.checkSubAssetExists(parent);
        });
    });

    it('Moves a sub-asset to another root while retaining the source and its siblings', () => {
        AssetUtils.addAndSaveAsset(
            AssetBuilder.create(source)
                .setAssetType('PRODUCTION_LINE')
                .addSubAssetBuilder(AssetBuilder.create(child))
                .addSubAssetBuilder(AssetBuilder.create('Sibling'))
                .build(),
        );
        AssetUtils.addAndSaveAsset(AssetBuilder.create(target).build());
        AssetUtils.getStoredAssets().then(before => {
            const original = before.find(asset => asset.assetName === source)!
                .assets[0];
            AssetUtils.editAsset(source);
            AssetUtils.openMoveSubAsset(child);
            AssetBtns.moveTarget(source).should('not.exist');
            AssetUtils.selectMoveTarget(target);
            AssetUtils.saveMove();
            AssetUtils.checkAmountOfAssets(2);
            AssetUtils.getStoredAssets().then(assets => {
                expect(
                    assets
                        .find(asset => asset.assetName === source)!
                        .assets.map(asset => asset.assetName),
                ).to.deep.equal(['Sibling']);
                expect(
                    assets.find(asset => asset.assetName === target)!.assets[0],
                ).to.deep.equal(original);
            });
            cy.reload();
            AssetUtils.editAsset(source);
            AssetBtns.treeAsset(child).should('not.exist');
            AssetUtils.checkSubAssetExists('Sibling');
        });
    });

    it('Moves a sub-asset to the top level while retaining its asset information', () => {
        AssetUtils.addAndSaveAsset(
            AssetBuilder.create(source)
                .addSubAssetBuilder(AssetBuilder.create(child))
                .addSubAssetBuilder(AssetBuilder.create('Sibling'))
                .build(),
        );
        AssetUtils.getStoredAssets().then(before => {
            const original = before.find(asset => asset.assetName === source)!
                .assets[0];
            AssetUtils.editAsset(source);
            AssetUtils.openMoveSubAsset(child);
            AssetUtils.selectMoveToTopLevel();
            AssetUtils.saveMove();
            AssetUtils.checkAmountOfAssets(2);
            AssetUtils.getStoredAssets().then(assets => {
                const sourceAsset = assets.find(
                    asset => asset.assetName === source,
                )!;
                const promotedAsset = assets.find(
                    asset => asset.assetName === child,
                )!;

                expect(
                    sourceAsset.assets.map(asset => asset.assetName),
                ).to.deep.equal(['Sibling']);
                expect(promotedAsset).to.include({
                    assetDescription: original.assetDescription,
                    assetId: original.assetId,
                    assetName: original.assetName,
                });

                expect(promotedAsset.assetType).to.deep.equal(
                    sourceAsset.assetType,
                );

                expect(promotedAsset.additionalData).to.deep.equal(
                    original.additionalData,
                );
                expect(promotedAsset.assetLinks).to.deep.equal(
                    original.assetLinks,
                );
                expect(promotedAsset.assetSite).to.deep.equal(
                    sourceAsset.assetSite,
                );

                expect(promotedAsset.assets).to.deep.equal(original.assets);
                expect(promotedAsset.labelIds).to.deep.equal(original.labelIds);
            });
        });
    });

    it('Leaves both models unchanged when a selected move is cancelled', () => {
        createHierarchy(source, child);
        AssetUtils.addAndSaveAsset(AssetBuilder.create(target).build());
        AssetUtils.getStoredAssets().then(before => {
            AssetUtils.openMoveAsset(source);
            AssetUtils.selectMoveTarget(target);
            AssetBtns.cancelMoveBtn().click();
            AssetBtns.saveMoveBtn().should('not.exist');
            AssetUtils.getStoredAssets().should('deep.equal', before);
        });
    });

    it('Disables saving when there is no other model to move into', () => {
        AssetUtils.addAndSaveAsset(AssetBuilder.create(source).build());
        AssetUtils.openMoveAsset(source);
        AssetBtns.moveTarget(source).should('not.exist');
        AssetBtns.saveMoveBtn().should('be.disabled');
        AssetBtns.cancelMoveBtn().click();
        AssetUtils.checkAmountOfAssets(1);
    });

    it('Warns about different sites and removes the moved asset site without changing the target site', () => {
        ConfigurationUtils.goToSitesConfiguration();
        SiteUtils.createNewSite('SourceSite');
        SiteUtils.createNewSite('TargetSite');
        AssetUtils.goToAssets();
        AssetUtils.addAndSaveAsset(
            AssetBuilder.create(source).setSite('SourceSite').build(),
        );
        AssetUtils.addAndSaveAsset(
            AssetBuilder.create(target).setSite('TargetSite').build(),
        );
        AssetUtils.getStoredAssets().then(before => {
            const targetSite = before.find(
                asset => asset.assetName === target,
            )!.assetSite;
            AssetUtils.openMoveAsset(source);
            AssetUtils.selectMoveTarget(target);
            AssetBtns.moveSiteWarning()
                .should('be.visible')
                .and('have.attr', 'role', 'alert');
            AssetUtils.saveMove();
            AssetUtils.checkAmountOfAssets(1);
            AssetUtils.getStoredAssets().then(assets => {
                expect(assets[0].assetSite).to.deep.equal(targetSite);
                expect(assets[0].assets[0].assetSite).to.be.oneOf([
                    null,
                    undefined,
                ]);
            });
        });
    });
});
