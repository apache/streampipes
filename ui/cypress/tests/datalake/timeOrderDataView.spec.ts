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

import { DataLakeUtils } from '../../support/utils/datalake/DataLakeUtils';
import { DataLakeBtns } from '../../support/utils/datalake/DataLakeBtns';

describe('Test Time Order in Data Explorer', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
        DataLakeUtils.loadDataIntoDataLake('datalake/sample.csv', false);
        DataLakeUtils.goToDatalake();
        DataLakeUtils.createAndEditDataView();
    });

    it('Perform Test with ascending and descending order', () => {
        const startDate = new Date(1653871499055);
        const endDate = new Date(1653871608093);

        DataLakeUtils.clickOrderBy('descending');

        DataLakeUtils.openVisualizationConfig();
        DataLakeUtils.selectVisualizationType('Table');
        DataLakeUtils.selectTimeRange(startDate, endDate);
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
        DataLakeBtns.saveDataViewButton();
        DataLakeBtns.editDataViewButton('NewWidget');
        DataLakeUtils.clickOrderBy('ascending');
        DataLakeUtils.openVisualizationConfig();
        DataLakeUtils.selectVisualizationType('Table');
        DataLakeUtils.selectTimeRange(startDate, endDate);
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
        DataLakeUtils.goToDatalake();
        DataLakeUtils.checkIfConfirmationDialogIsShowing();
    });
});
