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

import { DataLakeUtils } from '../../../support/utils/datalake/DataLakeUtils';

const testView1 = 'TestView1';
const testView2 = 'TestView2';
const dataSet = 'Persist';

describe('Test if widget configuration is updated correctly', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
        DataLakeUtils.loadRandomDataSetIntoDataLake();

        // Create first test data view with one time series widget
        DataLakeUtils.addDataViewAndTimeSeriesWidget(testView1, dataSet);
        DataLakeUtils.saveDataExplorerWidgetConfiguration();
        // DataLakeUtils.clickStartTab();

        // Create second test data view with one time series widget
        DataLakeUtils.addDataViewAndTimeSeriesWidget(testView2, dataSet);
        DataLakeUtils.saveDataExplorerWidgetConfiguration();
    });

    // This test case has two different options. The first one selects the edit button of the data explorer on the top right
    // and the second one uses the edit button of the widget
    it('Perform Test', () => {
        runTestCase(false);
    });

    it('Perform Test', () => {
        runTestCase(true);
    });
});

const runTestCase = (editOption: boolean) => {
    // Go to test view 1 tab and then back to view 2
    DataLakeUtils.clickTab(testView1);
    DataLakeUtils.clickTab(testView2);

    // Change one setting in widget
    const widgetName = 'datalake_configuration';

    if (editOption) {
        cy.dataCy('more-options-' + widgetName).click();
        cy.dataCy('start-edit-' + widgetName).click();
    } else {
        cy.dataCy('options-data-explorer').click();
        cy.dataCy('options-edit-dashboard').click();
        cy.dataCy('edit-' + widgetName).click();
    }

    DataLakeUtils.selectVisualizationConfig();
    cy.get('div').contains('Line').click();
    cy.get('div').contains('Scatter').click();

    cy.get('path[class="scatterpts"]');
};
