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

import { DataLakeFilterConfig } from '../../support/model/DataLakeFilterConfig';
import { DataLakeUtils } from '../../support/utils/datalake/DataLakeUtils';
import { DataLakeWidgetTableUtils } from '../../support/utils/datalake/DataLakeWidgetTableUtils';

describe('Test Table View in Data Explorer', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
        DataLakeUtils.loadDataIntoDataLake('datalake/sample.csv');
    });

    it('Perform Test', () => {
        /**
         * Prepare tests
         */
        DataLakeUtils.addDataViewAndTableWidget('NewWidget', 'Persist');

        // Validate that X lines are available
        DataLakeWidgetTableUtils.checkRows(10);

        // Go back to data configuration
        DataLakeUtils.selectDataConfig();

        /**
         * Test filter configuration
         */
        // Test number
        let filterConfig = new DataLakeFilterConfig('randomnumber', '22', '=');
        DataLakeUtils.dataConfigAddFilter(filterConfig);
        DataLakeWidgetTableUtils.checkRows(2);
        DataLakeUtils.dataConfigRemoveFilter();
        DataLakeWidgetTableUtils.checkRows(10);

        // Test number greater then
        filterConfig = new DataLakeFilterConfig('randomnumber', '50', '>');
        DataLakeUtils.dataConfigAddFilter(filterConfig);
        DataLakeWidgetTableUtils.checkRows(5);
        DataLakeUtils.dataConfigRemoveFilter();

        // Test number smaller then
        filterConfig = new DataLakeFilterConfig('randomnumber', '50', '<');
        DataLakeUtils.dataConfigAddFilter(filterConfig);
        DataLakeWidgetTableUtils.checkRows(5);
        DataLakeUtils.dataConfigRemoveFilter();

        // Test boolean
        filterConfig = new DataLakeFilterConfig('randombool', 'true', '=');
        DataLakeUtils.dataConfigAddFilter(filterConfig);
        DataLakeWidgetTableUtils.checkRows(6);
        DataLakeUtils.dataConfigRemoveFilter();

        // Test string & if filter is persisted correctly
        filterConfig = new DataLakeFilterConfig('randomtext', 'a', '=');
        DataLakeUtils.checkIfFilterIsSet(0);
        DataLakeUtils.dataConfigAddFilter(filterConfig);
        DataLakeUtils.checkIfFilterIsSet(1);
        DataLakeWidgetTableUtils.checkRows(4);
        DataLakeUtils.saveAndReEditWidget('NewWidget');
        DataLakeUtils.checkIfFilterIsSet(1);
        DataLakeWidgetTableUtils.checkRows(4);
        DataLakeUtils.dataConfigRemoveFilter();

        /**
         * Test groupBy configuration and if it is persisted correctly
         */
        cy.wait(1000);
        DataLakeUtils.clickGroupBy('randomtext');
        cy.wait(1000);
        cy.dataCy('data-explorer-table-row-randomtext', { timeout: 10000 })
            .first({ timeout: 10000 })
            .contains('a', { timeout: 10000 });
        cy.dataCy('data-explorer-table-row-randomtext', { timeout: 10000 })
            .last({ timeout: 10000 })
            .contains('c', { timeout: 10000 });
        DataLakeWidgetTableUtils.checkRows(10);
        DataLakeUtils.saveAndReEditWidget('NewWidget');
        cy.dataCy('data-explorer-group-by-randomtext')
            .find('input')
            .should('be.checked');
        DataLakeUtils.clickGroupBy('randomtext');
    });
});
