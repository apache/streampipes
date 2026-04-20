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

import { ChartUtils } from '../../support/utils/chart/ChartUtils';
import { ChartWidgetTableUtils } from '../../support/utils/chart/ChartWidgetTableUtils';
import { DataLakeFilterConfig } from '../../support/model/DataLakeFilterConfig';
import { ChartWidget } from '../../support/model/ChartWidget';
import { DataLakeSeedUtils } from '../../support/utils/dataset/DataLakeSeedUtils';

describe('Validate that filter works for numerical dimension property', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
        DataLakeSeedUtils.importCsvFixture({
            fixture: 'datalake/filterNumericalStringProperties.csv',
            measurementName: 'Test Adapter',
            delimiter: ';',
            timestampColumn: 'timestamp',
            columnOverrides: {
                dimensionKey: {
                    runtimeType: 'FLOAT',
                    propertyScope: 'DIMENSION_PROPERTY',
                },
            },
        });
    });

    it('Perform Test', () => {
        ChartUtils.goToDatalake();
        ChartUtils.createAndEditDataView();

        // create table widget and select time range
        const startDate = new Date(1737029442000);
        const endDate = new Date(1742220659000);

        ChartUtils.clickOrderBy('descending');

        ChartUtils.openVisualizationConfig();
        ChartUtils.selectVisualizationType(ChartWidget.TABLE);
        ChartUtils.selectTimeRange(startDate, endDate);
        cy.wait(1000);

        // validate data in table
        ChartWidgetTableUtils.checkAmountOfRows(2);

        // select filter for tag
        ChartUtils.selectDataConfig();
        let filterConfig = new DataLakeFilterConfig('dimensionKey', '1.0', '=');
        ChartUtils.dataConfigAddFilter(filterConfig);

        // validate data in table is filtered
        ChartWidgetTableUtils.checkAmountOfRows(1);

        // remove filter
        ChartUtils.dataConfigRemoveFilter();

        ChartUtils.selectDataConfig();

        filterConfig = new DataLakeFilterConfig('v1', '20', '=');
        ChartUtils.dataConfigAddFilter(filterConfig);

        // validate data in table is filtered
        ChartWidgetTableUtils.checkAmountOfRows(1);

        // remove filter
        ChartUtils.dataConfigRemoveFilter();

        // validate data again
        ChartWidgetTableUtils.checkAmountOfRows(2);
    });
});
