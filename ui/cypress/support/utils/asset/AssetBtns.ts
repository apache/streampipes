/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

import { Isa95Type } from '../../../../projects/streampipes/platform-services/src/lib/model/gen/streampipes-model';

export class AssetBtns {
    public static createAssetBtn() {
        return cy.dataCy('create-new-asset-button', { timeout: 10000 });
    }

    public static assetNameInput() {
        return cy.dataCy('asset-name', { timeout: 10000 });
    }

    public static assetTypeSelect() {
        return cy.dataCy('asset-type-select', { timeout: 10000 });
    }

    public static assetTypeSelectOption(type: Isa95Type) {
        return cy.dataCy(`option-${type}`, { timeout: 10000 });
    }

    public static siteSelect() {
        return cy.dataCy('asset-site-select', { timeout: 10000 });
    }

    public static siteSelectOption(site: string) {
        return cy.dataCy(`option-${site}`, { timeout: 10000 });
    }

    public static labelSelect() {
        return cy.dataCy('asset-label-input', { timeout: 10000 });
    }

    public static labelSelectOption(label: string) {
        return cy.dataCy(`option-${label}`, { timeout: 10000 });
    }

    public static saveAssetBtn() {
        return cy.dataCy('save-asset', { timeout: 10000 });
    }

    public static createAssetPanelBtn() {
        return cy.dataCy('create-asset-panel', { timeout: 10000 });
    }

    public static editAssetBtn(assetName: string) {
        return cy.dataCy('edit-asset-' + assetName, { timeout: 10000 });
    }

    public static deleteAssetBtn(assetName: string) {
        return cy.dataCy('delete-asset-' + assetName, { timeout: 10000 });
    }

    public static basicTab() {
        return cy.dataCy('basic-tab', { timeout: 10000 });
    }

    public static assetLinksTab() {
        return cy.dataCy('asset-links-tab', { timeout: 10000 });
    }

    public static manageLinksBtn() {
        return cy.dataCy('assets-manage-links-button', { timeout: 10000 });
    }

    public static adapterCheckbox(adapterName: string) {
        return cy.dataCy('select-adapters-checkbox-' + adapterName, {
            timeout: 10000,
        });
    }

    public static dataStreamCheckbox(adapterName: string) {
        return cy.dataCy('select-data-stream-checkbox-' + adapterName, {
            timeout: 10000,
        });
    }

    public static updateAssetLinksBtn() {
        return cy.dataCy('assets-update-links-button', { timeout: 10000 });
    }

    public static goBackToOverviewBtn() {
        return cy.dataCy('save-data-explorer-go-back-to-overview', {
            timeout: 10000,
        });
    }
}
