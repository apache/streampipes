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
import { PrepareTestDataUtils } from '../../support/utils/PrepareTestDataUtils';
import { ChartWidgetTableUtils } from '../../support/utils/chart/ChartWidgetTableUtils';
import { DataLakeSeedUtils } from '../../support/utils/dataset/DataLakeSeedUtils';

describe('Test missing properties in data lake', () => {
    const headers = ['timestamp', 'v1', 'v2', 'v3', 'v4'];
    const rows = [
        ['1667904471000', '4.1', 'abc', 'true', '1'],
        ['1667904472000', '4.2', 'abc', 'false', '2'],
        ['1667904473000', '4.3', '', '', ''],
        ['1667904474000', '4.4', 'abc', 'true', '4'],
        ['1667904475000', '4.5', '', '', '5'],
    ];

    before('Setup Test', () => {
        cy.initStreamPipesTest();
        DataLakeSeedUtils.importCsvData({
            headers,
            rows,
            measurementName: PrepareTestDataUtils.dataName,
            delimiter: ';',
            timestampColumn: 'timestamp',
        });
    });

    it('Test table with missing properties', () => {
        ChartUtils.addDataViewAndTableWidget(PrepareTestDataUtils.dataName);

        ChartWidgetTableUtils.checkAmountOfRows(5);

        ChartUtils.selectDataConfig();
        cy.dataCy('data-explorer-ignore-missing-values-checkbox')
            .children()
            .click();

        ChartWidgetTableUtils.checkAmountOfRows(3);
    });
});
