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

export class AssetUtils {
    public static goToAssets() {
        cy.visit('#/assets/overview');
    }

    public static goBackToOverview() {
        AssetBtns.goBackToOverviewBtn()
            .should('be.visible')
            .should('not.be.disabled')
            .click();
    }

    public static addNewAsset(assetName: string) {
        AssetBtns.createAssetBtn().click();
        AssetBtns.assetNameInput().clear();
        AssetBtns.assetNameInput().type(assetName);
        AssetBtns.saveAssetBtn().click();
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

    public static selectFileAssetLink(fileName: string) {
        AssetBtns.fileCheckbox(fileName).click();
    }

    public static selectPipelineAssetLink(pipelineName: string) {
        AssetBtns.pipelineCheckbox(pipelineName).click();
    }

    public static selectMeasurementAssetLink(measurementName: string) {
        AssetBtns.measurementCheckbox(measurementName).click();
    }

    public static checkAmountOfAssets(amount: number) {
        cy.dataCy('assets-table').should('have.length', amount);
    }

    public static checkAmountOfLinkedResources(amount: number) {
        if (amount === 0) {
            cy.wait(1000);
            cy.dataCy('linked-resources-list').should('not.exist');
        } else {
            cy.dataCy('linked-resources-list')
                .children()
                .should('have.length', amount);
        }
    }

    public static editAsset(assetName: string) {
        AssetBtns.editAssetBtn(assetName).click();
    }

    public static addAssetWithOneAdapter(assetName: string) {
        const adapterName = 'Machine_Data_Simulator';
        ConnectUtils.addMachineDataSimulator(adapterName);

        // Create new asset from adapters
        AssetUtils.goToAssets();

        AssetUtils.addNewAsset(assetName);

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
        AssetBtns.deleteAssetBtn(assetName).click();
        cy.dataCy('confirm-delete').click();
    }
}
