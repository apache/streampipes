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

export class AssetUtils {
    public static goToAssets() {
        cy.visit('#/assets/overview');
    }

    public static goBackToOverview() {
        AssetBtns.goBackToOverviewBtn().click();
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

    public static checkAmountOfAssets(amount: number) {
        cy.dataCy('assets-table').should('have.length', amount);
    }

    public static checkAmountOfLinkedResources(amount: number) {
        cy.dataCy('linked-resources-list')
            .children()
            .should('have.length', amount);
    }
}
