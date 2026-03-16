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
import { ChartBtns } from '../../support/utils/chart/ChartBtns';
import { ChartWidgetTableUtils } from '../../support/utils/chart/ChartWidgetTableUtils';

describe('Dynamic Column Filters in Table Widget', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
        ChartUtils.loadDataIntoDataLake('datalake/sample.csv');
    });

    it('Applies a Top 10 number filter on a numeric column', () => {
        ChartUtils.addDataViewAndTableWidget(
            'DynamicColumnFilterWidget',
            ChartUtils.ADAPTER_NAME,
        );

        ChartWidgetTableUtils.checkAmountOfRows(10);

        // Open the column filter dropdown for the numeric column
        ChartBtns.columnFilterTrigger('randomnumber').click({ force: true });

        // Expand the number filters panel
        ChartBtns.columnAdvancedFilterExpandBtn().click({ force: true });

        // Select the 'Top 10' filter option
        ChartBtns.columnAdvancedFilterOptionByText('Top 10').click();

        // Apply the filter
        ChartBtns.columnAdvancedFilterApplyBtn().click();

        // Top 10 filter should return 10 or fewer rows
        ChartWidgetTableUtils.chartTableRowTimestamp().should(
            'have.length.at.most',
            10,
        );
    });
});
