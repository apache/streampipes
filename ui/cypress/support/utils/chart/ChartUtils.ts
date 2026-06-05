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

import { DataLakeFilterConfig } from '../../model/DataLakeFilterConfig';
import { ChartWidget } from '../../model/ChartWidget';
import { DataSetUtils } from '../DataSetUtils';
import { PrepareTestDataUtils } from '../PrepareTestDataUtils';
import { GeneralUtils } from '../GeneralUtils';
import { ChartBtns } from './ChartBtns';
import { SharedBtns } from '../shared/SharedBtns';
import { DataLakeSeedUtils } from '../dataset/DataLakeSeedUtils';
import { ConnectBtns } from '../connect/ConnectBtns';

export class ChartUtils {
    public static ADAPTER_NAME = 'datalake_configuration';

    public static goToDatalake() {
        cy.visit('#/chart');
    }

    public static goToDashboard() {
        cy.wait(1000);
        cy.visit('#/dashboard');
    }

    public static checkAmountOfCharts(amount: number) {
        ChartUtils.goToDatalake();
        this.checkAmount(amount);
    }

    public static checkAmountOfDashboards(amount: number) {
        ChartUtils.goToDashboard();
        this.checkAmount(amount);
    }

    public static checkAmount(amount: number) {
        if (amount === 0) {
            // The wait is needed because the default value is the no-table-entries element.
            // It must be waited till the data is loaded. Once a better solution is found, this can be removed.
            cy.wait(1000);
            cy.dataCy('no-table-entries').should('be.visible');
        } else {
            ConnectBtns.moreOptions().should('have.length', amount);
        }
    }

    public static checkChartCanBeEdited(chartName: string) {
        GeneralUtils.openMenuForRow(chartName);
        GeneralUtils.visibleMaterialMenu().within(() => {
            ChartBtns.editDataViewButton(chartName).should('exist');
        });
    }

    public static checkChartCanNotBeEdited(chartName: string) {
        GeneralUtils.openMenuForRow(chartName);
        GeneralUtils.visibleMaterialMenu().within(() => {
            ChartBtns.editDataViewButton(chartName).should('not.exist');
        });
    }

    public static checkDashboardCanBeEdited(dashboardName: string) {
        GeneralUtils.openMenuForRow(dashboardName);
        GeneralUtils.visibleMaterialMenu().within(() => {
            ChartBtns.editDashboardBtn(dashboardName).should('exist');
        });
    }

    public static checkDashboardCanNotBeEdited(dashboardName: string) {
        GeneralUtils.openMenuForRow(dashboardName);
        GeneralUtils.visibleMaterialMenu().within(() => {
            ChartBtns.editDashboardBtn(dashboardName).should('not.exist');
        });
    }

    public static initDataLakeTests() {
        cy.initStreamPipesTest();
        ChartUtils.loadRandomDataSetIntoDataLake();
    }

    public static loadDataIntoDataLake(
        dataSet: string,
        format: 'csv' | 'json_array' = 'csv',
    ) {
        if (format === 'csv') {
            return DataLakeSeedUtils.importCsvFixture({
                fixture: dataSet,
                measurementName: ChartUtils.ADAPTER_NAME,
                delimiter: ';',
                timestampColumn: 'timestamp',
                columnOverrides: {
                    randomtext: {
                        propertyScope: 'DIMENSION_PROPERTY',
                    },
                },
            });
        } else {
            return DataLakeSeedUtils.importJsonArrayFixture({
                fixture: dataSet,
                measurementName: ChartUtils.ADAPTER_NAME,
                timestampColumn: 'timestamp',
            });
        }
    }

    public static addDataViewAndWidget(
        dataViewName: string,
        dataSet: string,
        widgetType: string,
        ignoreTimeSelection = false,
    ) {
        ChartUtils.goToDatalake();
        ChartUtils.createAndEditDataView();

        if (!ignoreTimeSelection) {
            ChartUtils.selectTimeRange(
                new Date(2020, 10, 20, 22, 44),
                ChartUtils.getFutureDate(),
            );
        }

        // ChartUtils.addNewWidget();
        ChartUtils.selectDataSet(dataSet);
        ChartUtils.dataConfigSelectAllFields();

        ChartUtils.selectAppearanceConfig();
        ChartUtils.selectDataViewName(dataViewName);

        ChartUtils.openVisualizationConfig();
        ChartUtils.selectVisualizationType(widgetType);

        cy.wait(1000);
    }

    public static addAssetsToDashboard(assetNameList: string[]) {
        cy.dataCy('sp-show-asset-checkbox')
            .find('input[type="checkbox"]')
            .then($checkbox => {
                if (!$checkbox.prop('checked')) {
                    cy.wrap($checkbox).click();
                }
            });

        cy.get('mat-tree.asset-tree', { timeout: 10000 }).should('exist');
        assetNameList.forEach(assetName => {
            const assetHierarchy = assetName.split('.');
            const lastElement = assetHierarchy[assetHierarchy.length - 1];
            const firstElements = assetHierarchy.slice(0, -1);

            firstElements.forEach(el => {
                cy.dataCy(`toggle-${el}`).click();
            });

            cy.get('mat-tree.asset-tree')
                .find('.mat-tree-node')
                .contains(lastElement)
                .click();
        });
    }

    public static createNewDashboard(name: string) {
        ChartUtils.goToDashboard();
        ChartUtils.addNewDashboard(name);
        ChartUtils.saveDataView();
        ChartUtils.waitForDashboardInOverview(name);
    }

    public static createNewDashboardWithAssetLinks(
        name: string,
        assetNameList: string[],
    ) {
        ChartUtils.goToDashboard();
        ChartUtils.addNewDashboard(name);
        ChartUtils.addAssetsToDashboard(assetNameList);
        ChartUtils.saveDataView();
        ChartUtils.waitForDashboardInOverview(name);
    }

    public static addNewDashboard(name: string) {
        ChartBtns.newDashboardDialogBtn().click();
        ChartBtns.saveDashboardBtn().click();
        cy.dataCy('managed-resource-name').type(name);
        ChartBtns.saveDataViewBtn().click();
    }

    public static addNewDashboardwithAssets(name: string, assetNameList) {
        ChartBtns.newDashboardDialogBtn().click();
        ChartBtns.saveDashboardBtn().click();
        cy.dataCy('managed-resource-name').type(name);
        ChartUtils.addAssetsToDashboard(assetNameList);
        ChartBtns.saveDataViewBtn().click();
    }

    public static createDashboardWithLinkedAssets(
        dataView,
        name,
        assetNameList,
    ) {
        ChartUtils.goToDatalake();

        ChartUtils.addDataViewAndTableWidget(dataView, ChartUtils.ADAPTER_NAME);

        ChartUtils.saveDataViewConfiguration(false, false);

        ChartUtils.goToDashboard();

        //ADD Assets
        ChartUtils.addNewDashboardwithAssets(name, assetNameList);
        ChartUtils.waitForDashboardInOverview(name);
    }

    public static saveDataView() {
        return ChartBtns.saveDataViewBtn().click();
    }

    public static saveDashboard() {
        return ChartBtns.saveDashboardBtn().click();
    }

    public static waitForDashboardInOverview(name: string) {
        cy.dataCy('sp-manage-save', { timeout: 10000 }).should('not.exist');
        ChartUtils.goToDashboard();
        cy.contains('[role="row"], tr, mat-row', name, {
            timeout: 10000,
        }).should('be.visible');
    }

    public static addDataViewAndTableWidget(
        dataViewName: string,
        dataSet: string,
        ignoreTimeSelection = false,
    ) {
        this.addDataViewAndWidget(
            dataViewName,
            dataSet,
            ChartWidget.TABLE,
            ignoreTimeSelection,
        );
    }

    public static addDataViewAndTimeSeriesWidget(
        dataViewName: string,
        dataSet: string,
    ) {
        this.addDataViewAndWidget(
            dataViewName,
            dataSet,
            ChartWidget.TIME_SERIES,
        );
    }
    public static renameWidget(newName: string) {
        cy.dataCy('appearance-config-widget-title').clear().type(newName);
        cy.dataCy('appearance-config-widget-title').should(
            'have.value',
            newName,
        );
    }

    public static renameDashboard(newName: string) {
        cy.dataCy('managed-resource-name').clear().type(newName);
        cy.dataCy('managed-resource-name').should('have.value', newName);
    }

    public static loadRandomDataSetIntoDataLake() {
        PrepareTestDataUtils.loadDataIntoDataLake('fileTest/random.csv');
    }

    public static createAndEditDashboard(name: string) {
        // Create new data view
        ChartBtns.newDashboardDialogBtn().click();
        ChartBtns.saveDashboardBtn().click();
        // Configure data view
        cy.dataCy('managed-resource-name').type(name);
        ChartBtns.saveDataViewBtn().click();
        cy.contains('[role="row"], tr, mat-row', name, {
            timeout: 10000,
        }).should('be.visible');

        this.editDashboard(name);
    }

    public static addDataViewToDashboard(
        dataViewName: string,
        ignoreTimeRange = false,
    ) {
        if (!ignoreTimeRange) {
            this.selectTimeRange(
                new Date(2020, 10, 20, 22, 44),
                this.getFutureDate(),
            );
        }
        ChartBtns.addDataViewBtn(dataViewName).click();
    }

    public static createAndEditDataView() {
        // Create new data view
        ChartBtns.openNewDataViewBtn().click();
        cy.location('hash').should('include', '/chart/create');
        cy.location('hash').should('include', 'editMode=true');
    }

    public static removeWidget(dataViewName: string) {
        ChartBtns.removeWidgetBtn(dataViewName).click();
    }

    public static editDashboard(dashboardName: string) {
        GeneralUtils.openMenuForRow(dashboardName);
        GeneralUtils.visibleMaterialMenu().within(() => {
            ChartBtns.editDashboardBtn(dashboardName).click();
        });
    }

    public static viewDashboard(dashboardName: string) {
        GeneralUtils.openMenuForRow(dashboardName);
        GeneralUtils.visibleMaterialMenu().within(() => {
            ChartBtns.viewDashboardBtn(dashboardName).click();
        });
    }

    public static editDashboardSettings(dashboardName: string) {
        GeneralUtils.openMenuForRow(dashboardName);
        GeneralUtils.visibleMaterialMenu().within(() => {
            ChartBtns.editDashboardSettingsBtn(dashboardName).click();
        });
    }

    public static editDataView(dataViewName: string) {
        // Click edit button
        // following only works if single view is available
        GeneralUtils.openMenuForRow(dataViewName);
        GeneralUtils.visibleMaterialMenu().within(() => {
            ChartBtns.editDataViewButton(dataViewName).click();
        });
    }

    public static saveDataViewConfiguration(
        confirmSave: boolean = false,
        withoutConfig: boolean = true,
    ) {
        if (withoutConfig) {
            ChartBtns.saveDataViewButton().click({
                force: true,
            });
        } else {
            ChartBtns.saveDataViewButton().click({
                force: true,
            });
            ChartBtns.saveDataViewBtn().should('be.visible');
            ChartBtns.saveDataViewBtn().click();
        }
        if (confirmSave) {
            SharedBtns.confirmDialogConfirmBtn().click();
        }
        ChartBtns.openNewDataViewBtn().should('be.visible');
    }

    public static saveDashboardConfiguration() {
        ChartBtns.saveDashboardConfigurationBtn().click();
    }

    public static getEmptyDashboardInformation() {
        return cy.dataCy('empty-dashboard');
    }

    public static addChartsToAsset(assetNameList = []) {
        ChartBtns.chartOptionsBtn().click();
        GeneralUtils.visibleMaterialMenu().within(() => {
            ChartBtns.manageChartBtn().click();
        });
        ChartUtils.addDashboardToAsset(assetNameList);
        ChartBtns.saveDataViewBtn().click();
    }

    public static addChartDialogAssets(assetNameList = []) {
        ChartBtns.chartAssetDialogCheckbox().then($checkbox => {
            if (!$checkbox.prop('checked')) {
                cy.wrap($checkbox).check({ force: true });
            }
        });
        this.addToAsset(assetNameList);
        ChartBtns.confirmAssetSelectionBtn();
    }

    public static addDashboardToAsset(assetNameList = []) {
        ChartBtns.objectManageAssetCheckbox().then($checkbox => {
            if (!$checkbox.prop('checked')) {
                cy.wrap($checkbox).check({ force: true });
            }
        });
        this.addToAsset(assetNameList);
    }

    public static addToAsset(assetNameList = []) {
        cy.get('mat-tree.asset-tree', { timeout: 10000 }).should('exist');

        assetNameList.forEach(assetName => {
            cy.get('mat-tree.asset-tree')
                .find('.mat-tree-node')
                .contains(assetName)
                .click();
        });
    }

    public static deleteDashboard(dashboardName: string) {
        GeneralUtils.openMenuForRow(dashboardName);
        GeneralUtils.visibleMaterialMenu().within(() => {
            ChartBtns.deleteDashboardBtn(dashboardName).click();
        });
        SharedBtns.confirmDialogConfirmBtn().click();
    }

    public static deleteDataView(dataViewName: string) {
        GeneralUtils.openMenuForRow(dataViewName);
        GeneralUtils.visibleMaterialMenu().within(() => {
            ChartBtns.deleteDataViewBtn(dataViewName).click();
        });
        SharedBtns.confirmDialogConfirmBtn().click();
    }

    public static cancelDeleteDashboard(dashboardName: string) {
        GeneralUtils.openMenuForRow(dashboardName);
        GeneralUtils.visibleMaterialMenu().within(() => {
            ChartBtns.deleteDashboardBtn(dashboardName).click();
        });
        SharedBtns.confirmDialogCancelBtn().click();
    }

    public static cancelDeleteDataView(dataViewName: string) {
        GeneralUtils.openMenuForRow(dataViewName);
        GeneralUtils.visibleMaterialMenu().within(() => {
            ChartBtns.deleteDataViewBtn(dataViewName).click();
        });
        SharedBtns.confirmDialogCancelBtn().click();
    }

    public static editWidget(widgetName: string) {
        ChartBtns.editWidget(widgetName).click();
    }

    public static startEditWidget(widgetName: string) {
        ChartBtns.moreOptionsBtn(widgetName).click();
        ChartBtns.startEditWidget(widgetName).click();
    }

    public static saveAndReEditWidget(
        dataViewName: string,
        edit: boolean = true,
    ) {
        // Save data view configuration
        ChartUtils.saveDataViewConfiguration(false, edit);
        ChartUtils.editDataView(dataViewName);
    }

    public static saveAndReEditDashboard(dashboardName: string) {
        // Save dashboard configuration
        ChartUtils.saveDashboardConfiguration();
        ChartUtils.editDashboard(dashboardName);
    }

    public static clickTab(tabName: string) {
        // Click start tab to go to overview
        cy.get('div').contains(tabName).parent().click();
    }

    public static goBackToOverview() {
        ChartBtns.goBackToOverviewBtn().click();
    }

    public static addNewWidget() {
        ChartBtns.addNewWidgetBtn().click();
    }

    public static selectDataSet(dataSet: string) {
        cy.get('body').then($body => {
            if (
                $body.find('[data-cy="data-explorer-select-data-set"]').length
            ) {
                cy.dataCy('data-explorer-select-data-set')
                    .click()
                    .get('mat-option')
                    .contains(dataSet)
                    .click();
            }
        });
    }

    public static assertSelectDataSet(dataSet: string) {
        cy.dataCy('data-explorer-select-data-set')
            .click()
            .get('mat-option')
            .should('contain.text', dataSet);
    }

    /**
     * Checks if in the widget configuration the filters are set or not
     * @param amountOfFilter the amount of filters that should be set. 0 if no filter should be visible
     */
    public static checkIfFilterIsSet(amountOfFilter: number) {
        cy.wait(1000);
        if (amountOfFilter === 0) {
            cy.dataCy('design-panel-data-settings-filter-field').should(
                'not.exist',
            );
        } else {
            cy.dataCy('design-panel-data-settings-filter-field', {
                timeout: 20000,
            }).should('be.visible');
        }
    }

    /**
     * This method validates that the defined filter options are available in the UI
     * @param expectedFilterOptions
     */
    public static validateFilterOptions(
        expectedFilterOptions: ('=' | '<' | '<=' | '>=' | '>' | '!=')[],
    ) {
        cy.dataCy('design-panel-data-settings-filter-operator')
            .click()
            .dataCy('operator-', {}, true)
            .should('have.length', expectedFilterOptions.length);

        expectedFilterOptions.forEach(option => {
            const escapedOption = option.replace(/([=<>!])/g, '\\$1');
            cy.dataCy('operator-' + escapedOption).should('be.visible');
        });

        cy.dataCy('design-panel-data-settings-filter-operator').click({
            force: true,
        });
    }

    public static validateAutoCompleteOptions(options: string[]) {
        cy.dataCy('design-panel-data-settings-filter-value')
            .click({ force: true })
            .dataCy('autocomplete-value-', {}, true)
            .should('have.length', options.length);

        options.forEach(option => {
            cy.dataCy('autocomplete-value-' + option).should('be.visible');
        });

        cy.dataCy('design-panel-data-settings-filter-value').click({
            force: true,
        });
    }

    /**
     * In the data set panel select all property fields
     */
    public static dataConfigSelectAllFields() {
        cy.dataCy('data-explorer-data-set-field-select-all').click();
    }

    public static dataConfigAddFilter(filterConfig: DataLakeFilterConfig) {
        cy.dataCy('design-panel-data-settings-add-filter').click();

        // Select field
        cy.dataCy('design-panel-data-settings-filter-field')
            .click()
            .get('mat-option')
            .contains(filterConfig.field)
            .click();

        // Select value
        cy.dataCy('design-panel-data-settings-filter-value').type(
            filterConfig.value,
        );

        // Select operator
        cy.dataCy('design-panel-data-settings-filter-operator')
            .click()
            .get('mat-option')
            .contains(filterConfig.operator)
            .click();
    }

    public static dataConfigRemoveFilter() {
        cy.dataCy('design-panel-data-settings-remove-filter')
            .first()
            .click({ force: true });
    }

    public static clickGroupBy(propertyName: string) {
        cy.dataCy('data-explorer-group-by-' + propertyName)
            .children()
            .click();
    }

    public static clickOrderBy(order: string) {
        if (order == 'ascending') {
            cy.dataCy('ascending-radio-button').click();
        } else {
            cy.dataCy('descending-radio-button').click();
        }
    }

    public static selectAggregatedQueryType() {
        ChartBtns.aggregatedQueryTypeButton()
            .find('input[type="radio"]')
            .first()
            .check({ force: true });
    }

    public static enableAutoAggregate() {
        ChartBtns.autoAggregateCheckbox()
            .find('input[type="checkbox"]')
            .first()
            .then($checkbox => {
                if (!$checkbox.prop('checked')) {
                    cy.wrap($checkbox).check({ force: true });
                }
            });
    }

    /**
     * Select visualization type
     */
    public static selectVisualizationType(type: string | 'table') {
        // Select visualization type
        cy.dataCy('data-explorer-select-visualization-type', { timeout: 10000 })
            .click()
            .dataCy(`select-widget-${type}`)
            .click();
    }

    public static selectDataConfig() {
        this.selectDataViewConfigTab(0);
    }

    public static openVisualizationConfig() {
        this.selectDataViewConfigTab(1);
    }

    public static selectAppearanceConfig() {
        this.selectDataViewConfigTab(2);
    }

    // Workaround: mat-tab does not render the data-cy attribute, so we select tabs by index.
    // Using the label is not reliable due to multi-language support.
    private static selectDataViewConfigTab(tabNumber: number) {
        cy.get('div[role=tab]').eq(tabNumber).click();
    }

    public static selectDataViewName(dataViewName: string) {
        cy.dataCy('appearance-config-widget-title').clear().type(dataViewName);
    }

    public static clickCreateButton() {
        // Create widget
        cy.dataCy('data-explorer-select-data-set-create-btn').click();
    }

    public static goToDatalakeConfiguration() {
        cy.visit('#/datasets');
    }

    public static checkResults(
        measurementName: string,
        fileRoute: string,
        ignoreTime: boolean,
    ) {
        const fileType = this.getFileType(fileRoute);

        this.fetchDataLakeResults(measurementName, fileType).then(
            actualResultString =>
                this.compareResults(
                    actualResultString,
                    fileRoute,
                    fileType,
                    ignoreTime,
                ),
        );
    }

    public static clearMeasurementData(measurementName: string) {
        const token = window.localStorage.getItem('auth-token');
        return cy
            .request({
                method: 'DELETE',
                url: `/streampipes-backend/api/v4/datalake/measurements/${measurementName}`,
                failOnStatusCode: false,
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            })
            .then(response => {
                expect(response.status).to.be.oneOf([200, 403, 404]);
            });
    }

    private static getFileType(fileRoute: string): 'csv' | 'json' {
        return fileRoute.endsWith('.csv') ? 'csv' : 'json';
    }

    private static fetchDataLakeResults(
        measurementName: string,
        fileType: 'csv' | 'json',
    ): Cypress.Chainable<string> {
        return cy
            .request({
                method: 'GET',
                url: `/streampipes-backend/api/v4/datalake/measurements/${measurementName}/download?format=${fileType}&delimiter=semicolon`,
                headers: {
                    'content-type': 'application/octet-stream',
                },
                auth: {
                    bearer: window.localStorage.getItem('auth-token'),
                },
            })
            .then(response => response.body);
    }

    private static compareResults(
        actualResultString: string,
        fileRoute: string,
        fileType: 'csv' | 'json',
        ignoreTime?: boolean,
    ) {
        cy.readFile(fileRoute).then(expectedResult => {
            if (fileType === 'csv') {
                DataSetUtils.csvEqual(
                    actualResultString,
                    expectedResult,
                    ignoreTime,
                );
            } else if (fileType === 'json') {
                DataSetUtils.jsonFilesEqual(
                    actualResultString,
                    expectedResult,
                    ignoreTime,
                );
            }
        });
    }

    public static selectTimeRange(from: Date, to: Date) {
        cy.location('hash').then(hash => {
            const [route, queryString] = hash.split('?');
            const searchParams = new URLSearchParams(queryString ?? '');

            searchParams.set('startDate', from.getTime().toString());
            searchParams.set('endDate', to.getTime().toString());

            const updatedHash = `${route}?${searchParams.toString()}`;
            cy.window().then(win => {
                win.location.hash = updatedHash;
            });
        });

        cy.location('hash').should('contain', `startDate=${from.getTime()}`);
        cy.location('hash').should('contain', `endDate=${to.getTime()}`);
    }

    public static navigateCalendar(direction: string, numberOfMonths: number) {
        for (let i = 0; i < numberOfMonths; i++) {
            cy.get(`button.mat-calendar-${direction}-button`).click();
        }
    }

    public static selectDay(day: number) {
        cy.get(
            `button:has(span.mat-calendar-body-cell-content:contains("${day}"))`,
        )
            .first()
            .click();
    }

    public static openTimeSelectorMenu() {
        cy.dataCy('time-selector-menu', { timeout: 10000 }).click();
    }

    public static applyCustomTimeSelection() {
        cy.dataCy('apply-custom-time').click();
    }

    public static setTimeInput(field: string, date: Date) {
        cy.dataCy(field).type(ChartUtils.makeTimeString(date));
    }

    public static makeTimeString(date: Date) {
        return date.toTimeString().slice(0, 5);
    }

    public static getFutureDate() {
        const currentDate = new Date();
        currentDate.setMonth(currentDate.getMonth() + 1);

        return currentDate;
    }

    public static waitForCountingResults() {
        cy.dataCy('datalake-total-count-button').click();
        cy.dataCy('datalake-number-of-events-spinner', {
            timeout: 10000,
        }).should('exist');
        cy.dataCy('datalake-number-of-events-spinner', {
            timeout: 10000,
        }).should('not.exist');
    }

    public static getDatalakeNumberOfEvents(): Cypress.Chainable<string> {
        return cy
            .dataCy('datalake-number-of-events', { timeout: 10000 })
            .should('be.visible')
            .invoke('text')
            .then(text => text.trim());
    }

    public static checkRowsDashboardTable(amount: number) {
        cy.dataCy('dashboard-table-overview', {
            timeout: 10000,
        }).should('have.length', amount);
    }

    public static checkRowsViewsTable(amount: number) {
        cy.dataCy('data-views-table-overview', {
            timeout: 10000,
        }).should('have.length', amount);
    }

    public static checkIfConfirmationDialogIsShowing(): void {
        cy.dataCy('confirm-dialog').should('be.visible');
    }
    public static createDataViewWithAssets(assetNames) {
        ChartUtils.loadDataIntoDataLake('datalake/sample.csv');

        // Create Diagram
        ChartUtils.addDataViewAndTableWidget(
            'NewWidget',
            ChartUtils.ADAPTER_NAME,
        );
        //Save
        ChartBtns.saveDataViewButton().click();
        ChartUtils.addDashboardToAsset(assetNames);
        ChartBtns.saveDataViewBtn().click();
        ChartBtns.openNewDataViewBtn().should('be.visible');
    }
}
