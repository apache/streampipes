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
import { ChartUtils } from '../../support/utils/chart/ChartUtils';
import { ChartWidgetTableUtils } from '../../support/utils/chart/ChartWidgetTableUtils';

describe('Test Table View in Charts', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
        ChartUtils.loadDataIntoDataLake('datalake/sample.csv');
    });

    it('Perform Test', () => {
        /**
         * Prepare tests
         */
        ChartUtils.addDataViewAndTableWidget(
            'NewWidget',
            ChartUtils.ADAPTER_NAME,
        );

        // Validate that X lines are available
        ChartWidgetTableUtils.checkAmountOfRows(10);

        // Go back to data configuration
        ChartUtils.selectDataConfig();

        /**
         * Test filter configuration
         */
        // Test number
        let filterConfig = new DataLakeFilterConfig('randomnumber', '22', '=');
        ChartUtils.dataConfigAddFilter(filterConfig);
        ChartWidgetTableUtils.checkAmountOfRows(2);
        ChartUtils.validateFilterOptions(['=', '<', '<=', '>=', '>', '!=']);
        ChartUtils.dataConfigRemoveFilter();
        ChartWidgetTableUtils.checkAmountOfRows(10);

        // Test number greater then
        filterConfig = new DataLakeFilterConfig('randomnumber', '50', '>');
        ChartUtils.dataConfigAddFilter(filterConfig);
        ChartWidgetTableUtils.checkAmountOfRows(5);
        ChartUtils.validateFilterOptions(['=', '<', '<=', '>=', '>', '!=']);
        ChartUtils.dataConfigRemoveFilter();

        // Test number smaller then
        filterConfig = new DataLakeFilterConfig('randomnumber', '50', '<');
        ChartUtils.dataConfigAddFilter(filterConfig);
        ChartWidgetTableUtils.checkAmountOfRows(5);
        ChartUtils.dataConfigRemoveFilter();

        // Test boolean
        filterConfig = new DataLakeFilterConfig('randombool', 'true', '=');
        ChartUtils.dataConfigAddFilter(filterConfig);
        ChartWidgetTableUtils.checkAmountOfRows(6);
        ChartUtils.validateFilterOptions(['=', '!=']);
        ChartUtils.validateAutoCompleteOptions(['true', 'false']);
        ChartUtils.dataConfigRemoveFilter();

        // Test string & if filter is persisted correctly
        filterConfig = new DataLakeFilterConfig('randomtext', 'a', '=');
        ChartUtils.checkIfFilterIsSet(0);
        ChartUtils.dataConfigAddFilter(filterConfig);
        ChartUtils.checkIfFilterIsSet(1);
        ChartWidgetTableUtils.checkAmountOfRows(4);
        ChartUtils.validateFilterOptions(['=', '!=']);
        ChartUtils.validateAutoCompleteOptions(['a', 'b', 'c']);
        ChartUtils.saveAndReEditWidget('NewWidget');
        ChartUtils.checkIfFilterIsSet(1);
        ChartWidgetTableUtils.checkAmountOfRows(4);
        ChartUtils.dataConfigRemoveFilter();

        /**
         * Test groupBy configuration and if it is persisted correctly
         */
        cy.wait(1000);
        ChartUtils.clickGroupBy('randomtext');
        cy.wait(1000);
        cy.dataCy('data-explorer-table-row-randomtext', { timeout: 10000 })
            .last({ timeout: 10000 })
            .contains('a', { timeout: 10000 });
        cy.dataCy('data-explorer-table-row-randomtext', { timeout: 10000 })
            .first({ timeout: 10000 })
            .contains('c', { timeout: 10000 });
        ChartWidgetTableUtils.checkAmountOfRows(10);
        ChartUtils.saveAndReEditWidget('NewWidget');
        cy.dataCy('data-explorer-group-by-randomtext')
            .find('input')
            .should('be.checked');
        ChartUtils.clickGroupBy('randomtext');
    });
});
