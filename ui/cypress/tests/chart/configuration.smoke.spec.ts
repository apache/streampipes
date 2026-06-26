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
import { GeneralUtils } from '../../support/utils/GeneralUtils';
import { PrepareTestDataUtils } from '../../support/utils/PrepareTestDataUtils';
import { DatasetUtils } from '../../support/utils/dataset/DatasetUtils';
import { DatasetBtns } from '../../support/utils/dataset/DatasetBtns';

describe('Test Truncate data in datalake', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
        ChartUtils.loadRandomDataSetIntoDataLake();
    });

    it('Perform Test', () => {
        DatasetUtils.goToDatalakeConfiguration();

        // Check if the last event is shown
        DatasetUtils.expectDatasetNotEmpty(PrepareTestDataUtils.dataName);

        // Truncate data
        GeneralUtils.openMenuForRow(PrepareTestDataUtils.dataName);
        DatasetBtns.dataLakeTruncateBtn().should('be.visible').click();
        DatasetBtns.confirmDataLakeTruncateBtn().should('be.visible').click();

        // Check if there are no events left
        DatasetUtils.expectDatasetEmpty(PrepareTestDataUtils.dataName);
    });
});

describe('Delete data in datalake', () => {
    before('Setup Test', () => {
        cy.initStreamPipesTest();
        ChartUtils.loadRandomDataSetIntoDataLake();
    });

    it('Perform Test', () => {
        DatasetUtils.goToDatalakeConfiguration();

        // Check if the last event is shown
        DatasetUtils.expectDatasetNotEmpty(PrepareTestDataUtils.dataName);

        // Delete data
        GeneralUtils.openMenuForRow(PrepareTestDataUtils.dataName);
        DatasetBtns.dataLakeDeleteBtn().should('be.visible').click();
        DatasetBtns.confirmDataLakeDeleteBtn().should('be.visible').click();

        // Check if the dataset row is gone
        DatasetUtils.expectDatasetDeleted(PrepareTestDataUtils.dataName);
    });
});
