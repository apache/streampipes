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

describe('Test create chart from existing', () => {
    const sourceChart = 'SourceChart';
    const chartCopy = 'ChartCopy';

    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
        ChartUtils.loadDataIntoDataLake('datalake/sample.csv');

        ChartUtils.goToDatalake();
        ChartUtils.createTableChart(ChartUtils.ADAPTER_NAME);
        ChartUtils.saveChartConfiguration(false, false, sourceChart);
        ChartUtils.checkRowsChartsTable(1);
    });

    it('Stores the copy next to the source chart', () => {
        ChartUtils.createChartFromExisting(sourceChart);
        // Asserts the prefilled copy title before renaming and storing
        ChartUtils.saveChartFromExisting(sourceChart, chartCopy);

        ChartUtils.checkRowsChartsTable(2);
        ChartUtils.checkChartListed(sourceChart);
        ChartUtils.checkChartListed(chartCopy);
    });

    it('Does not store the copy when the editor is left without saving', () => {
        ChartUtils.createChartFromExisting(sourceChart);
        ChartUtils.discardChartFromExisting();

        ChartUtils.checkRowsChartsTable(1);
        ChartUtils.checkChartListed(sourceChart);
    });
});
