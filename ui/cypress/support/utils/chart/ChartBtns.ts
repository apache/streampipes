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

export class ChartBtns {
    public static refreshDataLakeMeasures() {
        return cy.dataCy('refresh-data-lake-measures');
    }

    public static saveDataViewButton() {
        return cy.dataCy('save-data-view-btn', { timeout: 10000 });
    }

    public static saveDataViewBtn() {
        return cy.dataCy('save-data-view');
    }

    public static saveDashboardBtn() {
        return cy.dataCy('save-dashboard-btn');
    }

    public static discardDashboard() {
        return cy.dataCy('discard-dashboard-btn');
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

    public static saveDashboardConfigurationBtn() {
        return cy.dataCy('save-dashboard-btn', { timeout: 10000 });
    }

    public static removeWidgetBtn(dataViewName) {
        return cy.dataCy('remove-' + dataViewName);
    }

    public static createChartBtn() {
        return cy.dataCy('create-chart-button');
    }

    public static editDashboardBtn(dashboardName) {
        return cy.dataCy('edit-dashboard-' + dashboardName);
    }

    public static viewDashboardBtn(dashboardName) {
        return cy.dataCy('view-dashboard-' + dashboardName);
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
        return cy.dataCy('edit-data-view-' + dataViewName.replaceAll(' ', ''));
    }

    public static editWidget(widgetName: string) {
        return cy.dataCy('edit-' + widgetName);
    }

    public static viewWidget(widgetName: string) {
        return cy.dataCy('show-data-view-' + widgetName);
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

    public static chartDataPreview() {
        return cy.dataCy('chart-data-preview');
    }

    public static chartDataPreviewHeader() {
        return cy.dataCy('chart-data-preview-header');
    }

    public static chartDataPreviewToggle() {
        return cy.dataCy('chart-data-preview-toggle');
    }

    public static chartDataPreviewTable() {
        return cy.dataCy('chart-data-preview-table');
    }

    public static chartDataPreviewCell(columnName: string) {
        return cy.dataCy(`chart-data-preview-cell-${columnName}`);
    }

    public static chartDataPreviewEmpty() {
        return cy.dataCy('chart-data-preview-empty');
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

    public static advancedFilterBtn() {
        return cy.dataCy('design-panel-data-settings-advanced-filter');
    }

    public static advancedFilterAddConditionBtn() {
        return cy.dataCy('advanced-filter-add-condition');
    }

    public static advancedFilterAddGroupBtn() {
        return cy.dataCy('advanced-filter-add-group');
    }

    public static advancedFilterGroupOperator() {
        return cy.dataCy('advanced-filter-group-operator', {}, true);
    }

    public static advancedFilterPreviewBanner() {
        return cy.dataCy('advanced-filter-preview-banner');
    }

    public static advancedFilterApplyBtn() {
        return cy.dataCy('advanced-filter-apply');
    }

    public static filterAlertBanner() {
        return cy.dataCy('filter-alert-banner', { timeout: 2000 });
    }

    public static filterFieldSelect() {
        return cy.dataCy('design-panel-data-settings-filter-field', {}, true);
    }

    public static filterOperatorSelect() {
        return cy.dataCy(
            'design-panel-data-settings-filter-operator',
            {},
            true,
        );
    }

    public static filterValueInput() {
        return cy.dataCy('design-panel-data-settings-filter-value', {}, true);
    }

    public static matOptionByText(text: string | RegExp) {
        return cy.get('mat-option').contains(text);
    }
}
