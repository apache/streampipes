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

export class DataLakeUtils {
    public static goToDatalake() {
        cy.visit('#/dataexplorer');
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
            new Date(2015, 10, 20, 22, 44),
            DataLakeUtils.getFutureDate(),
        );
        // DataLakeUtils.addNewWidget();
        DataLakeUtils.selectDataSet(dataSet);
        DataLakeUtils.dataConfigSelectAllFields();
        DataLakeUtils.selectVisualizationConfig();
        DataLakeUtils.selectVisualizationType(widgetType);
        DataLakeUtils.clickCreateButton();

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

    public static createAndEditDataView(name: string) {
        // Create new data view
        cy.dataCy('open-new-data-view-dialog').click();

        // Configure data view
        cy.dataCy('data-view-name').type(name);
        cy.dataCy('save-data-view').click();

        this.editDataView(name);
    }

    public static removeWidget(widgetName: string) {
        cy.dataCy('remove-' + widgetName).click();
    }

    public static editDataView(dataViewName: string) {
        // Click edit button
        // following only works if single view is available
        cy.dataCy('edit-dashboard-' + dataViewName).click();
    }

    public static saveDataExplorerWidgetConfiguration() {
        cy.dataCy('save-data-explorer-widget-btn', { timeout: 10000 }).click();
    }

    public static editWidget(widgetName: string) {
        cy.dataCy('edit-' + widgetName).click();
    }

    public static startEditWidget(widgetName: string) {
        cy.dataCy('more-options-' + widgetName).click();
        cy.dataCy('start-edit-' + widgetName).click();
    }

    public static saveAndReEditWidget(dataViewName: string) {
        // Save configuration
        DataLakeUtils.saveDataExplorerWidgetConfiguration();
        DataLakeUtils.goBackToOverview();
        DataLakeUtils.editDataView(dataViewName);
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

    /**
     * Select visualization type
     */
    public static selectVisualizationType(type: string | 'Table') {
        // Select visualization type
        cy.dataCy('data-explorer-select-visualization-type')
            .click()
            .get('mat-option')
            .contains(type)
            .click();
    }

    public static selectDataConfig() {
        cy.get('.mdc-tab__text-label').contains('Data').parent().click();
    }

    public static selectVisualizationConfig() {
        // Click Next button
        cy.get('.mdc-tab__text-label')
            .contains('Visualization')
            .parent()
            .click();
    }

    public static selectAppearanceConfig() {
        cy.get('.mdc-tab__text-label').contains('Appearance').parent().click();
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
        DataLakeUtils.setTimeInput('time-range-from', from);
        DataLakeUtils.clickSetTime();
        DataLakeUtils.setTimeInput('time-range-to', to);
        DataLakeUtils.clickSetTime();
    }

    public static setTimeInput(field: string, date: Date) {
        cy.dataCy(field)
            .clear({ force: true })
            .type(DataLakeUtils.makeTimeString(date), { force: true });
    }

    public static clickSetTime() {
        cy.get('.owl-dt-container-buttons > button:nth-child(2)').click();
    }

    public static makeTimeString(date: Date) {
        return date.toLocaleString('en-US', {
            month: 'numeric',
            day: 'numeric',
            year: 'numeric',
            hour: 'numeric',
            minute: 'numeric',
        });
    }

    public static getFutureDate() {
        const currentDate = new Date();
        currentDate.setMonth(currentDate.getMonth() + 1);

        return currentDate;
    }
}
