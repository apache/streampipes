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
import { DataExplorerWidget } from '../../model/DataExplorerWidget';
import { DataSetUtils } from '../DataSetUtils';
import { PrepareTestDataUtils } from '../PrepareTestDataUtils';
import { FileManagementUtils } from '../FileManagementUtils';
import { ConnectUtils } from '../connect/ConnectUtils';
import { ConnectBtns } from '../connect/ConnectBtns';
import { AdapterBuilder } from '../../builder/AdapterBuilder';
import { differenceInMonths } from 'date-fns';
import { GeneralUtils } from '../GeneralUtils';
import { DataExplorerBtns } from './DataExplorerBtns';

export class DataExplorerUtils {
    public static ADAPTER_NAME = 'datalake_configuration';

    public static goToDatalake() {
        cy.visit('#/dataexplorer');
    }

    public static goToDashboard() {
        cy.wait(1000);
        cy.visit('#/dashboard');
    }

    public static checkAmountOfCharts(amount: number) {
        DataExplorerUtils.goToDatalake();
        this.checkAmount(amount);
    }

    public static checkAmountOfDashboards(amount: number) {
        DataExplorerUtils.goToDashboard();
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
        DataExplorerBtns.editDataViewButton(chartName).should('exist');
    }

    public static checkChartCanNotBeEdited(chartName: string) {
        GeneralUtils.openMenuForRow(chartName);
        DataExplorerBtns.editDataViewButton(chartName).should('not.exist');
    }

    public static checkDashboardCanBeEdited(dashboardName: string) {
        GeneralUtils.openMenuForRow(dashboardName);
        DataExplorerBtns.editDashboardBtn(dashboardName).should('exist');
    }

    public static checkDashboardCanNotBeEdited(dashboardName: string) {
        GeneralUtils.openMenuForRow(dashboardName);
        DataExplorerBtns.editDashboardBtn(dashboardName).should('not.exist');
    }

    public static initDataLakeTests() {
        cy.initStreamPipesTest();
        DataExplorerUtils.loadRandomDataSetIntoDataLake();
    }

    public static getDataLakeTestSetAdapter(
        name: string,
        storeInDataLake: boolean = true,
        format: 'csv' | 'json_array',
    ) {
        const adapterBuilder = AdapterBuilder.create('File_Stream')
            .setName(name)
            .setTimestampProperty('timestamp')
            .addDimensionProperty('randomtext')
            .addProtocolInput(
                'radio',
                'speed',
                'fastest_\\(ignore_original_time\\)',
            )
            .setStartAdapter(true);

        if (format === 'csv') {
            adapterBuilder
                .setFormat('csv')
                .addFormatInput('input', ConnectBtns.csvDelimiter(), ';')
                .addFormatInput('checkbox', ConnectBtns.csvHeader(), 'check');
        } else {
            adapterBuilder.setFormat('json_array');
        }

        if (storeInDataLake) {
            adapterBuilder.setStoreInDataLake();
        }
        return adapterBuilder.build();
    }

    public static loadDataIntoDataLake(
        dataSet: string,
        format: 'csv' | 'json_array' = 'csv',
    ) {
        // Create adapter with dataset
        FileManagementUtils.addFile(dataSet);

        const adapter = this.getDataLakeTestSetAdapter(
            DataExplorerUtils.ADAPTER_NAME,
            true,
            format,
        );

        ConnectUtils.addAdapter(adapter);
        ConnectUtils.startAdapter(adapter);
    }

    public static addDataViewAndWidget(
        dataViewName: string,
        dataSet: string,
        widgetType: string,
        ignoreTimeSelection = false,
    ) {
        DataExplorerUtils.goToDatalake();
        DataExplorerUtils.createAndEditDataView();

        if (!ignoreTimeSelection) {
            DataExplorerUtils.selectTimeRange(
                new Date(2020, 10, 20, 22, 44),
                DataExplorerUtils.getFutureDate(),
            );
        }

        // DataExplorerUtils.addNewWidget();
        DataExplorerUtils.selectDataSet(dataSet);
        DataExplorerUtils.dataConfigSelectAllFields();

        DataExplorerUtils.selectAppearanceConfig();
        DataExplorerUtils.selectDataViewName(dataViewName);

        DataExplorerUtils.openVisualizationConfig();
        DataExplorerUtils.selectVisualizationType(widgetType);

        cy.wait(1000);
    }

    public static addAssetsToDashboard(assetNameList: string[]) {
        cy.dataCy('sp-show-dashboard-asset-checkbox')
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
        DataExplorerUtils.goToDashboard();
        DataExplorerUtils.addNewDashboard(name);
        DataExplorerUtils.saveDataView();
    }

    public static createNewDashboardWithAssetLinks(
        name: string,
        assetNameList: string[],
    ) {
        DataExplorerUtils.goToDashboard();
        DataExplorerUtils.addNewDashboard(name);
        DataExplorerUtils.addAssetsToDashboard(assetNameList);
        DataExplorerUtils.saveDataView();
    }

    public static addNewDashboard(name: string) {
        DataExplorerBtns.newDashboardDialogBtn().click();
        cy.dataCy('data-view-name').type(name);
    }

    public static createDashboardWithLinkedAssets(
        dataView,
        name,
        assetNameList,
    ) {
        DataExplorerUtils.goToDatalake();

        DataExplorerUtils.addDataViewAndTableWidget(
            dataView,
            DataExplorerUtils.ADAPTER_NAME,
        );

        DataExplorerUtils.saveDataViewConfiguration();

        DataExplorerUtils.goToDashboard();

        //ADD Assets
        DataExplorerUtils.addNewDashboard(name);
        DataExplorerUtils.addAssetsToDashboard(assetNameList);
        DataExplorerUtils.saveDataView();
    }

    public static saveDataView() {
        return DataExplorerBtns.saveDataViewBtn().click();
    }

    public static saveDashboard() {
        return DataExplorerBtns.saveDashboardBtn().click();
    }

    public static addDataViewAndTableWidget(
        dataViewName: string,
        dataSet: string,
        ignoreTimeSelection = false,
    ) {
        this.addDataViewAndWidget(
            dataViewName,
            dataSet,
            DataExplorerWidget.TABLE,
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
            DataExplorerWidget.TIME_SERIES,
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
        cy.dataCy('data-view-name').clear().type(newName);
        cy.dataCy('data-view-name').should('have.value', newName);
    }

    public static loadRandomDataSetIntoDataLake() {
        PrepareTestDataUtils.loadDataIntoDataLake('fileTest/random.csv');
    }

    public static createAndEditDashboard(name: string) {
        // Create new data view
        DataExplorerBtns.newDashboardDialogBtn().click();

        // Configure data view
        cy.dataCy('data-view-name').type(name);
        DataExplorerBtns.saveDataViewBtn().click();

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
        DataExplorerBtns.addDataViewBtn(dataViewName).click();
    }

    public static createAndEditDataView() {
        // Create new data view
        DataExplorerBtns.openNewDataViewBtn().click();
    }

    public static removeWidget(dataViewName: string) {
        DataExplorerBtns.removeWidgetBtn(dataViewName).click();
    }

    public static editDashboard(dashboardName: string) {
        GeneralUtils.openMenuForRow(dashboardName);
        DataExplorerBtns.editDashboardBtn(dashboardName).click();
    }

    public static viewDashboard(dashboardName: string) {
        GeneralUtils.openMenuForRow(dashboardName);
        DataExplorerBtns.viewDashboardBtn(dashboardName).click();
    }

    public static editDashboardSettings(dashboardName: string) {
        GeneralUtils.openMenuForRow(dashboardName);
        DataExplorerBtns.editDashboardSettingsBtn(dashboardName).click();
    }

    public static editDataView(dataViewName: string) {
        // Click edit button
        // following only works if single view is available
        GeneralUtils.openMenuForRow(dataViewName);
        DataExplorerBtns.editDataViewButton(dataViewName).click();
    }

    public static saveDataViewConfiguration(confirmSave: boolean = false) {
        DataExplorerBtns.saveDataViewButton().click({
            force: true,
        });
        if (confirmSave) {
            DataExplorerBtns.confirmSave().click();
        }
    }

    public static saveDashboardConfiguration() {
        DataExplorerBtns.saveDashboardConfigurationBtn().click();
    }

    public static getEmptyDashboardInformation() {
        return cy.dataCy('empty-dashboard');
    }

    public static addChartsToAsset(assetNameList = []) {
        DataExplorerBtns.saveChartsToAssetBtn();

        cy.dataCy('sp-show-chart-asset-checkbox').then($checkbox => {
            if (!$checkbox.is(':checked')) {
                cy.wrap($checkbox).click({ force: true });
            }
        });
        this.addToAsset(assetNameList);
        DataExplorerBtns.confirmAssetSelectionBtn();
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
        DataExplorerBtns.deleteDashboardBtn(dashboardName).click();
        DataExplorerBtns.confirmDelete().click();
    }

    public static deleteDataView(dataViewName: string) {
        GeneralUtils.openMenuForRow(dataViewName);
        DataExplorerBtns.deleteDataViewBtn(dataViewName).click();
        DataExplorerBtns.confirmDelete().click();
    }

    public static cancelDeleteDashboard(dashboardName: string) {
        GeneralUtils.openMenuForRow(dashboardName);
        DataExplorerBtns.deleteDashboardBtn(dashboardName).click();
        DataExplorerBtns.cancelDelete().click();
    }

    public static cancelDeleteDataView(dataViewName: string) {
        GeneralUtils.openMenuForRow(dataViewName);
        DataExplorerBtns.deleteDataViewBtn(dataViewName).click();
        DataExplorerBtns.cancelDelete().click();
    }

    public static editWidget(widgetName: string) {
        DataExplorerBtns.editWidget(widgetName).click();
    }

    public static startEditWidget(widgetName: string) {
        DataExplorerBtns.moreOptionsBtn(widgetName).click();
        DataExplorerBtns.startEditWidget(widgetName).click();
    }

    public static saveAndReEditWidget(dataViewName: string) {
        // Save data view configuration
        DataExplorerUtils.saveDataViewConfiguration();
        DataExplorerUtils.editDataView(dataViewName);
    }

    public static saveAndReEditDashboard(dashboardName: string) {
        // Save dashboard configuration
        DataExplorerUtils.saveDashboardConfiguration();
        DataExplorerUtils.editDashboard(dashboardName);
    }

    public static clickTab(tabName: string) {
        // Click start tab to go to overview
        cy.get('div').contains(tabName).parent().click();
    }

    public static goBackToOverview() {
        DataExplorerBtns.goBackToOverviewBtn().click();
    }

    public static addNewWidget() {
        DataExplorerBtns.addNewWidgetBtn().click();
    }

    public static selectDataSet(dataSet: string) {
        cy.dataCy('data-explorer-select-data-set')
            .click()
            .get('mat-option')
            .contains(dataSet)
            .click();
    }

    /**
     * Checks if in the widget configuration the filters are set or not
     * @param amountOfFilter the amount of filters that should be set. 0 if no filter should be visible
     */
    public static checkIfFilterIsSet(amountOfFilter: number) {
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
        cy.visit('#/configuration/datalake');
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
        DataExplorerUtils.openTimeSelectorMenu();
        const monthsBack = Math.abs(differenceInMonths(from, new Date())) + 1;
        DataExplorerUtils.navigateCalendar('previous', monthsBack);
        DataExplorerUtils.selectDay(from.getDate());

        const monthsForward = Math.abs(differenceInMonths(from, to));
        DataExplorerUtils.navigateCalendar('next', monthsForward);

        DataExplorerUtils.selectDay(to.getDate());

        DataExplorerUtils.setTimeInput('time-selector-start-time', from);
        DataExplorerUtils.setTimeInput('time-selector-end-time', to);
        DataExplorerUtils.applyCustomTimeSelection();
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
        cy.dataCy(field).type(DataExplorerUtils.makeTimeString(date));
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
        cy.get('confirmation-dialog').should('be.visible');
    }
    public static createDataViewWithAssets(assetNames) {
        DataExplorerUtils.loadDataIntoDataLake('datalake/sample.csv');

        // Create Diagram
        DataExplorerUtils.addDataViewAndTableWidget(
            'NewWidget',
            DataExplorerUtils.ADAPTER_NAME,
        );
        //Save
        DataExplorerUtils.addChartsToAsset(assetNames);
        DataExplorerUtils.saveDataViewConfiguration();
        //Necessary for the background task to finish otherwise it steps back to charts from the following task
        cy.wait(500);
    }
}
