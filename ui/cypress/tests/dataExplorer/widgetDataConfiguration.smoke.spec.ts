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
import { DataExplorerUtils } from '../../support/utils/dataExplorer/DataExplorerUtils';
import { DataExplorerWidgetTableUtils } from '../../support/utils/dataExplorer/DataExplorerWidgetTableUtils';

describe('Test Table View in Data Explorer', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
        DataExplorerUtils.loadDataIntoDataLake('datalake/sample.csv');
    });

    it('Perform Test', () => {
        /**
         * Prepare tests
         */
        DataExplorerUtils.addDataViewAndTableWidget(
            'NewWidget',
            DataExplorerUtils.ADAPTER_NAME,
        );

        // Validate that X lines are available
        DataExplorerWidgetTableUtils.checkAmountOfRows(10);

        // Go back to data configuration
        DataExplorerUtils.selectDataConfig();

        /**
         * Test filter configuration
         */
        // Test number
        let filterConfig = new DataLakeFilterConfig('randomnumber', '22', '=');
        DataExplorerUtils.dataConfigAddFilter(filterConfig);
        DataExplorerWidgetTableUtils.checkAmountOfRows(2);
        DataExplorerUtils.validateFilterOptions([
            '=',
            '<',
            '<=',
            '>=',
            '>',
            '!=',
        ]);
        DataExplorerUtils.dataConfigRemoveFilter();
        DataExplorerWidgetTableUtils.checkAmountOfRows(10);

        // Test number greater then
        filterConfig = new DataLakeFilterConfig('randomnumber', '50', '>');
        DataExplorerUtils.dataConfigAddFilter(filterConfig);
        DataExplorerWidgetTableUtils.checkAmountOfRows(5);
        DataExplorerUtils.validateFilterOptions([
            '=',
            '<',
            '<=',
            '>=',
            '>',
            '!=',
        ]);
        DataExplorerUtils.dataConfigRemoveFilter();

        // Test number smaller then
        filterConfig = new DataLakeFilterConfig('randomnumber', '50', '<');
        DataExplorerUtils.dataConfigAddFilter(filterConfig);
        DataExplorerWidgetTableUtils.checkAmountOfRows(5);
        DataExplorerUtils.dataConfigRemoveFilter();

        // Test boolean
        filterConfig = new DataLakeFilterConfig('randombool', 'true', '=');
        DataExplorerUtils.dataConfigAddFilter(filterConfig);
        DataExplorerWidgetTableUtils.checkAmountOfRows(6);
        DataExplorerUtils.validateFilterOptions(['=', '!=']);
        DataExplorerUtils.validateAutoCompleteOptions(['true', 'false']);
        DataExplorerUtils.dataConfigRemoveFilter();

        // Test string & if filter is persisted correctly
        filterConfig = new DataLakeFilterConfig('randomtext', 'a', '=');
        DataExplorerUtils.checkIfFilterIsSet(0);
        DataExplorerUtils.dataConfigAddFilter(filterConfig);
        DataExplorerUtils.checkIfFilterIsSet(1);
        DataExplorerWidgetTableUtils.checkAmountOfRows(4);
        DataExplorerUtils.validateFilterOptions(['=', '!=']);
        DataExplorerUtils.validateAutoCompleteOptions(['a', 'b', 'c']);
        DataExplorerUtils.saveAndReEditWidget('NewWidget');
        DataExplorerUtils.checkIfFilterIsSet(1);
        DataExplorerWidgetTableUtils.checkAmountOfRows(4);
        DataExplorerUtils.dataConfigRemoveFilter();

        /**
         * Test groupBy configuration and if it is persisted correctly
         */
        cy.wait(1000);
        DataExplorerUtils.clickGroupBy('randomtext');
        cy.wait(1000);
        cy.dataCy('data-explorer-table-row-randomtext', { timeout: 10000 })
            .last({ timeout: 10000 })
            .contains('a', { timeout: 10000 });
        cy.dataCy('data-explorer-table-row-randomtext', { timeout: 10000 })
            .first({ timeout: 10000 })
            .contains('c', { timeout: 10000 });
        DataExplorerWidgetTableUtils.checkAmountOfRows(10);
        DataExplorerUtils.saveAndReEditWidget('NewWidget');
        cy.dataCy('data-explorer-group-by-randomtext')
            .find('input')
            .should('be.checked');
        DataExplorerUtils.clickGroupBy('randomtext');
    });
});
