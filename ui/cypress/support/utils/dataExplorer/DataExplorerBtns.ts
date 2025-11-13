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

export class DataExplorerBtns {
    public static refreshDataLakeMeasures() {
        return cy.dataCy('refresh-data-lake-measures');
    }

    public static saveDataViewButton() {
        return cy.dataCy('save-data-view-btn', { timeout: 10000 });
    }

    public static saveDashboard() {
        return cy.dataCy('save-data-view');
    }
    public static saveChartsToAssetBtn() {
        return cy
            .dataCy('add-to-Asset-data-view-btn', { timeout: 10000 })
            .should('exist')
            .click();
    }

    public static deleteDashboardBtn(dashboardName) {
        return cy.dataCy('delete-dashboard-' + dashboardName, {
            timeout: 10000,
        });
    }

    public static deleteDataViewBtn(dataViewName) {
        return cy.dataCy('delete-data-view-' + dataViewName, {
            timeout: 10000,
        });
    }

    public static confirmDelete() {
        return cy.dataCy('confirm-delete', { timeout: 10000 });
    }

    public static cancelDelete() {
        return cy.dataCy('cancel-delete', { timeout: 10000 });
    }

    public static saveDashboardConfigurationBtn() {
        return cy.dataCy('save-dashboard-btn', { timeout: 10000 });
    }

    public static removeWidgetBtn(dataViewName) {
        return cy.dataCy('remove-' + dataViewName);
    }

    public static editDashboardBtn(dashboardName) {
        return cy.dataCy('edit-dashboard-' + dashboardName);
    }

    public static editDashboardSettingsBtn(dashboardName) {
        return cy.dataCy('edit-dashboard-settings-' + dashboardName);
    }

    public static openNewDataViewBtn() {
        return cy.dataCy('open-new-data-view', { timeout: 10000 });
    }

    public static addDataViewBtn(dataViewName) {
        return cy.dataCy('add-data-view-btn-' + dataViewName);
    }

    public static newDashboardDialogBtn() {
        return cy.dataCy('open-new-dashboard-dialog');
    }

    public static chartAssetCheckboxBtn() {
        return cy.dataCy('add-to-Asset-data-view-btn');
    }
    public static confirmAssetSelectionBtn() {
        return cy
            .dataCy('asset-dialog-confirm-delete', { timeout: 10000 })
            .click();
    }
    public static editDataViewButton(dataViewName: string) {
        return cy.dataCy('edit-data-view-' + dataViewName);
    }

    public static editWidget(widgetName: string) {
        return cy.dataCy('edit-' + widgetName);
    }

    public static moreOptionsBtn(widgetName) {
        return cy.dataCy('more-options-' + widgetName);
    }

    public static startEditWidget(widgetName) {
        return cy.dataCy('start-edit-' + widgetName);
    }

    public static goBackToOverviewBtn() {
        return cy.dataCy('save-data-explorer-go-back-to-overview');
    }

    public static addNewWidgetBtn() {
        return cy.dataCy('add-new-widget');
    }

    public static dataLakeTruncateBtn() {
        return cy.dataCy('datalake-truncate-btn');
    }

    public static dataLakeDeleteBtn() {
        return cy.dataCy('datalake-delete-btn');
    }

    public static confirmDataLakeTruncateBtn() {
        return cy.dataCy('confirm-truncate-data-btn', { timeout: 10000 });
    }
    public static confirmDataLakeDeleteBtn() {
        return cy.dataCy('confirm-delete-data-btn', { timeout: 10000 });
    }

    public static datalakeNumberEvents() {
        return cy.dataCy('datalake-number-of-events', { timeout: 10000 });
    }

    public static dashboardAssetCheckboxBtn() {
        return cy.dataCy('sp-show-dashboard-asset-checkbox');
    }

    public static closeDashboardCreate() {
        return cy.dataCy('close-data-view');
    }
}
