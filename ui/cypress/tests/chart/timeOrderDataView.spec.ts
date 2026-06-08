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

import { ChartUtils } from '../../support/utils/chart/ChartUtils';
import { ChartWidget } from '../../support/model/ChartWidget';

describe('Test Time Order in Charts', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
        ChartUtils.loadDataIntoDataLake('datalake/sample.csv');
        ChartUtils.goToDatalake();
        ChartUtils.createAndEditDataView();
    });

    it('Perform Test with ascending and descending order', () => {
        const chartName = 'Time order chart';
        const startDate = new Date('2022-04-01T00:00:00Z');
        const endDate = new Date('2022-07-01T23:59:59Z');

        ChartUtils.clickOrderBy('descending');

        ChartUtils.openVisualizationConfig();
        ChartUtils.selectVisualizationType(ChartWidget.TABLE);
        ChartUtils.selectTimeRange(startDate, endDate);
        cy.wait(1000);

        cy.dataCy('data-explorer-table').then($cells => {
            const strings = $cells.map((index, cell) => cell.innerText).get();

            // Check for date strings if order is descending
            const dateStrings = strings.filter((_, index) => index % 4 === 0);
            const dates = dateStrings.map(dateStr => new Date(dateStr));
            const timestamps = dates.map(date => date.getTime());
            for (let i = 0; i < timestamps.length - 1; i++) {
                expect(timestamps[i]).to.be.at.least(timestamps[i + 1]);
            }
        });

        // Save and leave view, edit view again and check ascending order
        ChartUtils.selectAppearanceConfig();
        ChartUtils.selectDataViewName(chartName);
        ChartUtils.saveDataViewConfiguration(false, false);
        ChartUtils.editDataView(chartName);
        ChartUtils.clickOrderBy('ascending');
        ChartUtils.openVisualizationConfig();
        ChartUtils.selectVisualizationType(ChartWidget.TABLE);
        ChartUtils.selectTimeRange(startDate, endDate);
        cy.wait(1000);

        cy.dataCy('data-explorer-table').then($cells => {
            const strings = $cells.map((index, cell) => cell.innerText).get();

            // Check for date strings if order is ascending
            const dateStrings = strings.filter((_, index) => index % 4 === 0);
            const dates = dateStrings.map(dateStr => new Date(dateStr));
            const timestamps = dates.map(date => date.getTime());
            for (let i = 0; i < timestamps.length - 1; i++) {
                expect(timestamps[i]).to.be.at.most(timestamps[i + 1]);
            }
        });

        // Check if dialog window is showing after applying changes to time settings
        ChartUtils.goToDatalake();
        ChartUtils.checkIfConfirmationDialogIsShowing();
    });
});
