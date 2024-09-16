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

export class DataLakeUtils {
    public static goToDatalake() {
        cy.visit('#/dataexplorer');
    }

    public static initDataLakeTests() {
        cy.initStreamPipesTest();
        DataLakeUtils.loadRandomDataSetIntoDataLake();
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
        wait = true,
        format: 'csv' | 'json_array' = 'csv',
    ) {
        // Create adapter with dataset
        FileManagementUtils.addFile(dataSet);

        const adapter = this.getDataLakeTestSetAdapter(
            'datalake_configuration',
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
    ) {
        DataLakeUtils.goToDatalake();
        DataLakeUtils.createAndEditDataView(dataViewName);

        DataLakeUtils.selectTimeRange(
            new Date(2020, 10, 20, 22, 44),
            DataLakeUtils.getFutureDate(),
        );
        // DataLakeUtils.addNewWidget();
        DataLakeUtils.selectDataSet(dataSet);
        DataLakeUtils.dataConfigSelectAllFields();

        DataLakeUtils.selectAppearanceConfig();
        DataLakeUtils.selectDataViewName(dataViewName);

        DataLakeUtils.openVisualizationConfig();
        DataLakeUtils.selectVisualizationType(widgetType);

        cy.wait(1000);
    }

    public static addDataViewAndTableWidget(
        dataViewName: string,
        dataSet: string,
    ) {
        this.addDataViewAndWidget(
            dataViewName,
            dataSet,
            DataExplorerWidget.TABLE,
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

    public static loadRandomDataSetIntoDataLake() {
        PrepareTestDataUtils.loadDataIntoDataLake('fileTest/random.csv');
    }

    public static createAndEditDashboard(name: string) {
        // Create new data view
        cy.dataCy('open-new-dashboard-dialog').click();

        // Configure data view
        cy.dataCy('data-view-name').type(name);
        cy.dataCy('save-data-view').click();

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
        cy.dataCy('add-data-view-btn-' + dataViewName).click();
    }

    public static createAndEditDataView() {
        // Create new data view
        cy.dataCy('open-new-data-view').click();
    }

    public static removeWidget(dataViewName: string) {
        cy.dataCy('remove-' + dataViewName).click();
    }

    public static editDashboard(dashboardName: string) {
        // Click edit button
        // following only works if single view is available
        cy.dataCy('edit-dashboard-' + dashboardName).click();
    }

    public static editDataView(dataViewName: string) {
        // Click edit button
        // following only works if single view is available
        cy.dataCy('edit-data-view-' + dataViewName).click();
    }

    public static saveDataViewConfiguration() {
        cy.dataCy('save-data-view-btn', { timeout: 10000 }).click();
    }

    public static saveDashboardConfiguration() {
        cy.dataCy('save-dashboard-btn', { timeout: 10000 }).click();
    }

    public static getEmptyDashboardInformation() {
        return cy.dataCy('empty-dashboard');
    }

    public static deleteDashboard(dashboardName: string) {
        cy.dataCy('delete-dashboard-' + dashboardName, {
            timeout: 10000,
        }).click();
        cy.dataCy('confirm-delete', { timeout: 10000 }).click();
    }

    public static deleteDataView(dataViewName: string) {
        cy.dataCy('delete-data-view-' + dataViewName, {
            timeout: 10000,
        }).click();
        cy.dataCy('confirm-delete', { timeout: 10000 }).click();
    }

    public static cancelDeleteDashboard(dashboardName: string) {
        cy.dataCy('delete-dashboard-' + dashboardName, {
            timeout: 10000,
        }).click();
        cy.dataCy('cancel-delete', { timeout: 10000 }).click();
    }

    public static cancelDeleteDataView(dataViewName: string) {
        cy.dataCy('delete-data-view-' + dataViewName, {
            timeout: 10000,
        }).click();
        cy.dataCy('cancel-delete', { timeout: 10000 }).click();
    }

    public static editWidget(widgetName: string) {
        cy.dataCy('edit-' + widgetName).click();
    }

    public static startEditWidget(widgetName: string) {
        cy.dataCy('more-options-' + widgetName).click();
        cy.dataCy('start-edit-' + widgetName).click();
    }

    public static saveAndReEditWidget(dataViewName: string) {
        // Save data view configuration
        DataLakeUtils.saveDataViewConfiguration();
        DataLakeUtils.editDataView(dataViewName);
    }

    public static saveAndReEditDashboard(dashboardName: string) {
        // Save dashboard configuration
        DataLakeUtils.saveDashboardConfiguration();
        DataLakeUtils.editDashboard(dashboardName);
    }

    public static clickTab(tabName: string) {
        // Click start tab to go to overview
        cy.get('div').contains(tabName).parent().click();
    }

    public static goBackToOverview() {
        cy.dataCy('save-data-explorer-go-back-to-overview').click();
    }

    public static addNewWidget() {
        cy.dataCy('add-new-widget').click();
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
        cy.dataCy('design-panel-data-settings-remove-filter').first().click();
    }

    public static clickGroupBy(propertyName: string) {
        cy.dataCy('data-explorer-group-by-' + propertyName)
            .children()
            .click();
    }

    public static clickOrderBy(order: String) {
        if (order == 'ascending') {
            cy.dataCy('ascending-radio-button').click();
        } else {
            cy.dataCy('descending-radio-button').click();
        }
    }

    /**
     * Select visualization type
     */
    public static selectVisualizationType(type: string | 'Table') {
        // Select visualization type
        cy.dataCy('data-explorer-select-visualization-type', { timeout: 10000 })
            .click()
            .get('mat-option')
            .contains(type)
            .click();
    }

    public static selectDataConfig() {
        cy.get('.mdc-tab__text-label').contains('Data').parent().click();
    }

    public static openVisualizationConfig() {
        cy.get('.mdc-tab__text-label')
            .contains('Visualization')
            .parent()
            .click();
    }

    public static selectAppearanceConfig() {
        cy.get('.mdc-tab__text-label').contains('Appearance').parent().click();
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
        dataLakeIndex: string,
        fileRoute: string,
        ignoreTime?: boolean,
    ) {
        // Validate result in datalake
        cy.request({
            method: 'GET',
            url: `/streampipes-backend/api/v4/datalake/measurements/${dataLakeIndex}/download?format=csv&delimiter=semicolon`,
            headers: {
                'content-type': 'application/octet-stream',
            },
            auth: {
                bearer: window.localStorage.getItem('auth-token'),
            },
        }).then(response => {
            const actualResultString = response.body;
            cy.readFile(fileRoute).then(expectedResultString => {
                DataSetUtils.csvEqual(
                    actualResultString,
                    expectedResultString,
                    ignoreTime,
                );
            });
        });
    }

    public static selectTimeRange(from: Date, to: Date) {
        DataLakeUtils.openTimeSelectorMenu();
        const monthsBack = Math.abs(differenceInMonths(from, new Date())) + 1;
        DataLakeUtils.navigateCalendar('previous', monthsBack);
        DataLakeUtils.selectDay(from.getDate());

        const monthsForward = Math.abs(differenceInMonths(from, to));
        DataLakeUtils.navigateCalendar('next', monthsForward);

        DataLakeUtils.selectDay(to.getDate());

        DataLakeUtils.setTimeInput('time-selector-start-time', from);
        DataLakeUtils.setTimeInput('time-selector-end-time', to);
        DataLakeUtils.applyCustomTimeSelection();
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
        cy.dataCy('time-selector-menu').click();
    }

    public static applyCustomTimeSelection() {
        cy.dataCy('apply-custom-time').click();
    }

    public static setTimeInput(field: string, date: Date) {
        cy.dataCy(field).type(DataLakeUtils.makeTimeString(date));
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
}
