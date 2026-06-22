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
        return cy.dataCy('sp-manage-save');
    }

    public static manageChartButton(title) {
        return cy.dataCy('open-manage-permissions-' + title);
    }

    public static saveDashboardBtn() {
        return cy.dataCy('save-dashboard-btn');
    }

    public static discardDashboard() {
        return cy.dataCy('save-data-explorer-go-back-to-overview');
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
        return cy.dataCy('open-manage-permissions-' + dashboardName);
    }

    public static openNewDataViewBtn() {
        return cy.dataCy('open-new-data-view', { timeout: 10000 });
    }

    public static addDataViewBtn(dataViewName: string) {
        return cy.dataCy(
            'add-data-view-btn-' + dataViewName.replaceAll(' ', ''),
            { timeout: 10000 },
        );
    }

    public static refreshChartSelectionBtn() {
        return cy.dataCy('refresh-chart-button');
    }

    public static newDashboardDialogBtn() {
        return cy.dataCy('open-new-dashboard-dialog');
    }

    public static chartAssetCheckboxBtn() {
        return cy.dataCy('add-to-Asset-data-view-btn');
    }

    public static chartOptionsBtn() {
        return cy.dataCy('options-chart');
    }

    public static manageChartBtn() {
        return cy.dataCy('manage-chart-btn');
    }

    public static confirmAssetSelectionBtn() {
        return cy
            .dataCy('asset-dialog-confirm-delete', { timeout: 10000 })
            .click();
    }

    public static chartAssetDialogCheckbox() {
        return cy
            .dataCy('sp-show-chart-asset-checkbox')
            .find('input[type="checkbox"]');
    }

    public static objectManageAssetCheckbox() {
        return cy
            .dataCy('sp-show-asset-checkbox')
            .find('input[type="checkbox"]');
    }

    public static editDataViewButton(dataViewName: string) {
        return cy.dataCy('edit-data-view-' + dataViewName.replace(/ /g, ''));
    }

    public static chartSyncProblemIcon() {
        return cy.dataCy('chart-sync-problem-icon', { timeout: 60000 });
    }

    public static chartRequiresAttentionWarning() {
        return cy.dataCy('chart-requires-attention-warning');
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

    public static discardDataExplorerWidgetBtn() {
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

    public static indicatorChart() {
        return cy.dataCy('indicator-chart');
    }

    public static indicatorChartValue() {
        return cy.dataCy('indicator-chart-value');
    }

    public static indicatorChartDelta() {
        return cy.dataCy('indicator-chart-delta');
    }

    public static indicatorChartTitle() {
        return cy.dataCy('indicator-chart-title');
    }

    public static indicatorChartDescription() {
        return cy.dataCy('indicator-chart-description');
    }

    public static indicatorChartTitleInput() {
        return cy.dataCy('data-explorer-indicator-title-input');
    }

    public static indicatorChartDescriptionInput() {
        return cy.dataCy('data-explorer-indicator-description-input');
    }

    public static indicatorChartDeltaCheckbox() {
        return cy
            .dataCy('data-explorer-select-delta-checkbox')
            .find('input[type="checkbox"]');
    }

    public static valueCardWidget() {
        return cy.dataCy('value-card-widget');
    }

    public static valueCardTitleInput() {
        return cy.dataCy('data-explorer-value-card-title-input');
    }

    public static valueCardDescriptionInput() {
        return cy.dataCy('data-explorer-value-card-description-input');
    }

    public static valueCardShowTimestampCheckbox() {
        return cy
            .dataCy('data-explorer-value-card-show-timestamp')
            .find('input[type="checkbox"]');
    }

    public static valueCardTitle() {
        return cy.dataCy('value-card-title');
    }

    public static valueCardDescription() {
        return cy.dataCy('value-card-description');
    }

    public static valueCardTimestamp() {
        return cy.dataCy('value-card-timestamp');
    }

    public static valueCardItems() {
        return cy.dataCy('value-card-item', {}, true);
    }

    public static valueCardItemLabels() {
        return cy.dataCy('value-card-item-label', {}, true);
    }

    public static valueCardItemValues() {
        return cy.dataCy('value-card-item-value', {}, true);
    }

    public static progressBarWidget() {
        return cy.dataCy('progress-bar-widget');
    }

    public static progressBarTitleInput() {
        return cy.dataCy('data-explorer-progress-title-input');
    }

    public static progressBarDescriptionInput() {
        return cy.dataCy('data-explorer-progress-description-input');
    }

    public static progressBarTargetSource() {
        return cy.dataCy('data-explorer-progress-target-source');
    }

    public static progressBarTargetValueInput() {
        return cy.dataCy('data-explorer-progress-target-value');
    }

    public static progressBarDisplayMode() {
        return cy.dataCy('data-explorer-progress-display-mode');
    }

    public static progressBarInvertCheckbox() {
        return cy
            .dataCy('data-explorer-progress-invert')
            .find('input[type="checkbox"]');
    }

    public static progressBarShowLabelCheckbox() {
        return cy
            .dataCy('data-explorer-progress-show-label')
            .find('input[type="checkbox"]');
    }

    public static progressBarTitle() {
        return cy.dataCy('progress-bar-title');
    }

    public static progressBarDescription() {
        return cy.dataCy('progress-bar-description');
    }

    public static progressBarStatus() {
        return cy.dataCy('progress-bar-status');
    }

    public static progressBarPercent() {
        return cy.dataCy('progress-bar-percent');
    }

    public static progressBarFill() {
        return cy.dataCy('progress-bar-fill');
    }

    public static progressBarPrimaryLabel() {
        return cy.dataCy('progress-bar-primary-label');
    }

    public static progressBarSecondaryLabel() {
        return cy.dataCy('progress-bar-secondary-label');
    }

    public static addNewWidgetBtn() {
        return cy.dataCy('add-new-widget', { timeout: 10000 });
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

    public static datalakeTotalCountBtn() {
        return cy.dataCy('datalake-total-count-button', { timeout: 10000 });
    }

    public static datalakeNumberOfEventsSpinner() {
        return cy.dataCy('datalake-number-of-events-spinner', {
            timeout: 30000,
        });
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

    public static aggregatedQueryTypeButton() {
        return cy.dataCy('data-explorer-query-type-aggregated');
    }

    public static autoAggregateCheckbox() {
        return cy.dataCy('data-explorer-auto-aggregate-checkbox');
    }

    public static ignoreTooMuchDataWarningCheckbox() {
        return cy.dataCy('data-explorer-ignore-too-much-data-warning-checkbox');
    }

    public static dataExplorerTablePaginator() {
        return cy.dataCy('data-explorer-table-paginator');
    }

    public static resultLabelInput(fieldName: string) {
        return cy.get(
            `[data-cy="data-explorer-result-label-input-${fieldName}"]`,
        );
    }

    public static tableHeader(fieldName: string) {
        return cy.get(`[data-cy="data-explorer-table-header-${fieldName}"]`);
    }

    public static matOptionByText(text: string | RegExp) {
        return cy.get('mat-option').contains(text);
    }

    public static columnFilterTrigger(column: string) {
        return cy.get(`[data-cy="column-filter-trigger-${column}"]`);
    }

    public static columnAdvancedFilterExpandBtn() {
        return cy.get('[data-cy="column-advanced-filter-expand-btn"]');
    }

    public static columnAdvancedFilterOptionByText(text: string) {
        return cy.get('.advanced-filter-options').contains(text);
    }

    public static columnAdvancedFilterApplyBtn() {
        return cy.dataCy('column-advanced-filter-apply-btn');
    }
}
