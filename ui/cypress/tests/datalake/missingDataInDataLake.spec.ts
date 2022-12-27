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
import { PrepareTestDataUtils } from '../../support/utils/PrepareTestDataUtils';
import { DataLakeWidgetTableUtils } from '../../support/utils/datalake/DataLakeWidgetTableUtils';

describe('Test missing properties in data lake', () => {
    const dataViewName = 'TestView';

    before('Setup Test', () => {
        cy.initStreamPipesTest();
        PrepareTestDataUtils.loadDataIntoDataLake(
            'datalake/missingData.json',
            'json_array',
        );
    });

    it('Test table with missing properties', () => {
        DataLakeUtils.addDataViewAndTableWidget(dataViewName, 'Persist');

        DataLakeWidgetTableUtils.checkRows(5);

        DataLakeUtils.selectDataConfig();
        cy.dataCy('data-explorer-ignore-missing-values-checkbox')
            .children()
            .click();

        DataLakeWidgetTableUtils.checkRows(3);
    });
});
