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

import { PipelineUtils } from '../../support/utils/pipeline/PipelineUtils';
import { DataExplorerUtils } from '../../support/utils/dataExplorer/DataExplorerUtils';
import { DataExplorerBtns } from '../../support/utils/dataExplorer/DataExplorerBtns';
import { GeneralUtils } from '../../support/utils/GeneralUtils';
import { PrepareTestDataUtils } from '../../support/utils/PrepareTestDataUtils';

describe('Test Truncate data in datalake', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
        DataExplorerUtils.loadRandomDataSetIntoDataLake();
    });

    it('Perform Test', () => {
        DataExplorerUtils.goToDatalakeConfiguration();

        // Check if amount of events is correct
        DataExplorerBtns.datalakeNumberEvents()
            .should('be.visible')
            .contains('10');

        // Truncate data
        GeneralUtils.openMenuForRow(PrepareTestDataUtils.dataName);
        DataExplorerBtns.dataLakeTruncateBtn().should('be.visible').click();
        DataExplorerBtns.confirmDataLakeTruncateBtn()
            .should('be.visible')
            .click();

        // Check if amount of events is zero. The should('have.text, '0') is required to check for text equality
        DataExplorerBtns.datalakeNumberEvents()
            .should('be.visible')
            .should($element => {
                const text = $element.text().trim();
                expect(text).to.equal('0');
            });
    });
});

describe('Delete data in datalake', () => {
    before('Setup Test', () => {
        cy.initStreamPipesTest();
        DataExplorerUtils.loadRandomDataSetIntoDataLake();
        PipelineUtils.deletePipeline('Persist prepared_data');
    });

    it('Perform Test', () => {
        DataExplorerUtils.goToDatalakeConfiguration();

        // Check if amount of events is correct
        DataExplorerBtns.datalakeNumberEvents()
            .should('be.visible')
            .contains('10');

        // Delete data
        GeneralUtils.openMenuForRow(PrepareTestDataUtils.dataName);
        DataExplorerBtns.dataLakeDeleteBtn().should('be.visible').click();
        DataExplorerBtns.confirmDataLakeDeleteBtn()
            .should('be.visible')
            .click();

        // Check if amount of events is zero
        DataExplorerBtns.datalakeNumberEvents().should('have.length', 0);
    });
});
