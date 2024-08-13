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
        DataLakeUtils.initDataLakeTests();

        // Create first test data view with one time series widget
        DataLakeUtils.addDataViewAndTimeSeriesWidget(testView1, dataSet);
        DataLakeUtils.saveDataViewConfiguration();

        cy.wait(1000);
        // Create second test data view with one time series widget
        DataLakeUtils.addDataViewAndTimeSeriesWidget(testView2, dataSet);
        DataLakeUtils.saveDataViewConfiguration();
    });

    it('Perform Test', () => {
        runTestCase();
    });
});

const runTestCase = () => {
    // Visit settings of widget
    DataLakeUtils.editDataView(testView1);

    // Change first field from line plot to scatter plot
    DataLakeUtils.openVisualizationConfig();

    // select type scatter
    cy.dataCy('time-series-item-config-toggle').first().click();
    cy.dataCy('time-series-item-config-type')
        .click()
        .get('mat-option')
        .contains('Scatter')
        .click();

    // Check if scatter plot is displayed
    cy.dataCy('time-series-chart').should('be.visible');

    // Change second field from line plot to bar plot
    cy.dataCy('time-series-item-config-type')
        .click()
        .get('mat-option')
        .contains('Line')
        .click();

    // Check if bar plot is displayed
    cy.dataCy('time-series-chart').should('be.visible');
};
