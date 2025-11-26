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

import { AssetBtns } from './AssetBtns';
import { ConnectUtils } from '../connect/ConnectUtils';
import { GeneralUtils } from '../GeneralUtils';
import { Asset } from '../../model/Asset';
import { Isa95Type } from '../../../../projects/streampipes/platform-services/src/lib/model/gen/streampipes-model';
import { AssetBuilder } from '../../builder/AssetBuilder';

export class AssetUtils {
    public static goToAssets() {
        cy.visit('#/assets/overview');
        cy.dataCy('asset-title').should('be.visible');
    }

    public static goBackToOverview() {
        AssetBtns.goBackToOverviewBtn().click();
    }

    public static addAndSaveAsset(asset: Asset) {
        AssetUtils.addNewAsset(asset);

        AssetBtns.saveAssetBtn().click();
        AssetBtns.createAssetBtn().should('be.visible');
    }

    public static addNewAsset(asset: Asset) {
        AssetBtns.createAssetBtn().click();
        AssetBtns.assetNameInput().clear().type(asset.name);
        AssetBtns.createAssetPanelBtn().click();

        this.selectAssetType(asset.assetType);
        if (asset.site) {
            this.selectSite(asset.site);
        }
        if (asset.labels.length > 0) {
            this.addLabels(asset.labels);
        }

        for (const subAsset of asset.subAssets) {
            this.clickAddSubAssetBtn(asset.name);
            this.selectSubAsset('New\\ Asset');
            AssetBtns.assetNameInput().clear().type(subAsset.name);
            this.selectAssetType(subAsset.assetType);
            this.addLabels(subAsset.labels);
        }
    }

    public static clickAddSubAssetBtn(assetName: string) {
        cy.dataCy(`add-asset-${assetName}`).click();
    }

    public static selectSubAsset(assetName: string) {
        cy.dataCy(`select-asset-${assetName}`).click();
    }

    public static addLabels(labels: string[]) {
        AssetBtns.labelSelect().click();
        for (const label of labels) {
            AssetBtns.labelSelectOption(label).click();
        }
    }

    public static selectSite(site: string) {
        AssetBtns.siteSelect().click();
        AssetBtns.siteSelectOption(site).click();
    }

    public static selectAssetType(type: Isa95Type) {
        AssetBtns.assetTypeSelect().click();
        AssetBtns.assetTypeSelectOption(type).click();
    }

    public static openManageAssetLinks() {
        AssetBtns.manageLinksBtn().should('be.enabled');
        AssetBtns.manageLinksBtn().click();
    }

    public static selectAdapterAssetLink(adapterName: string) {
        AssetBtns.adapterCheckbox(adapterName).click();
    }

    public static selectDataStreamAssetLink(adapterName: string) {
        AssetBtns.dataStreamCheckbox(adapterName).click();
    }

    public static checkAmountOfAssets(amount: number) {
        AssetUtils.goToAssets();

        if (amount === 0) {
            // The wait is needed because the default value is the no-table-entries element.
            // It must be waited till the data is loaded. Once a better solution is found, this can be removed.
            cy.wait(1000);
            cy.dataCy('no-table-entries').should('be.visible');
        } else {
            cy.dataCy('assets-table').should('have.length', amount);
        }
    }

    public static checkAmountOfLinkedResources(amount: number) {
        cy.dataCy('linked-resources-list')
            .children()
            .should('have.length', amount);
    }

    public static checkAssetCanBeEdited(assetName: string) {
        GeneralUtils.openMenuForRow(assetName);
        AssetBtns.editAssetBtn(assetName).should('exist');
    }

    public static checkAssetCanNotBeEdited(assetName: string) {
        GeneralUtils.openMenuForRow(assetName);
        AssetBtns.editAssetBtn(assetName).should('not.exist');
    }

    public static checkAmountOfAssetsGreaterThan(amount: number) {
        cy.dataCy('assets-table', { timeout: 10000 }).should(
            'have.length.greaterThan',
            amount,
        );
    }

    public static checkAmountOfLinkedResourcesByAssetName(
        assetName: string,
        amount: number,
    ) {
        AssetUtils.goToAssets();
        cy.wait(400);
        AssetUtils.editAsset(assetName);
        cy.wait(400);
        AssetBtns.assetLinksTab().click();
        AssetUtils.checkAmountOfLinkedResources(amount);
    }

    public static checkResourceNamingByAssetName(
        assetName: string,
        name: string,
    ) {
        AssetUtils.goToAssets();
        AssetUtils.editAsset(assetName);
        AssetBtns.assetLinksTab().click();
        cy.dataCy('linked-resources-list').children().contains(name);
        //.should('have.length', amount);
    }

    public static editAsset(assetName: string) {
        GeneralUtils.openMenuForRow(assetName);
        cy.contains('button', 'Edit').click({ force: true });
        //This is the old version and there in case above does not work for all tests
        //AssetBtns.editAssetBtn(assetName).click({ force: true });
    }

    public static addAssetWithOneAdapter(assetName: string) {
        const adapterName = 'Machine_Data_Simulator';
        ConnectUtils.addMachineDataSimulator(adapterName);

        // Create new asset from adapters
        AssetUtils.goToAssets();

        AssetUtils.addNewAsset(AssetBuilder.create(assetName).build());

        AssetBtns.assetLinksTab().click();
        AssetUtils.openManageAssetLinks();

        AssetUtils.selectAdapterAssetLink(adapterName);
        AssetUtils.selectDataStreamAssetLink(adapterName);
        AssetBtns.updateAssetLinksBtn().click();

        AssetUtils.checkAmountOfLinkedResources(2);
        AssetBtns.saveAssetBtn().click();
        AssetUtils.goBackToOverview();
    }

    public static deleteAsset(assetName: string) {
        GeneralUtils.openMenuForRow(assetName);
        AssetBtns.deleteAssetBtn(assetName).click();
        cy.dataCy('confirm-delete').click();
    }
}
