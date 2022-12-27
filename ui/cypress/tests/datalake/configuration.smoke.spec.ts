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

import { PipelineUtils } from '../../support/utils/PipelineUtils';
import { DataLakeUtils } from '../../support/utils/datalake/DataLakeUtils';

describe('Test Truncate data in datalake', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
        DataLakeUtils.loadRandomDataSetIntoDataLake();
    });

    it('Perform Test', () => {
        DataLakeUtils.goToDatalakeConfiguration();

        // Check if amount of events is correct
        cy.dataCy('datalake-number-of-events', { timeout: 10000 })
            .should('be.visible')
            .contains('10');

        // Truncate data
        cy.dataCy('datalake-truncate-btn').should('be.visible').click();
        cy.dataCy('confirm-truncate-data-btn', { timeout: 10000 })
            .should('be.visible')
            .click();

        // Check if amount of events is zero
        cy.dataCy('datalake-number-of-events', { timeout: 10000 })
            .should('be.visible')
            .contains('0');
    });
});

describe('Delete data in datalake', () => {
    before('Setup Test', () => {
        cy.initStreamPipesTest();
        DataLakeUtils.loadRandomDataSetIntoDataLake();
        PipelineUtils.deletePipeline();
    });

    it('Perform Test', () => {
        DataLakeUtils.goToDatalakeConfiguration();

        // Check if amount of events is correct
        cy.dataCy('datalake-number-of-events', { timeout: 10000 })
            .should('be.visible')
            .contains('10');

        // Delete data
        cy.dataCy('datalake-delete-btn').should('be.visible').click();
        cy.dataCy('confirm-delete-data-btn', { timeout: 10000 })
            .should('be.visible')
            .click();

        // Check if amount of events is zero
        cy.dataCy('datalake-number-of-events', { timeout: 10000 }).should(
            'have.length',
            0,
        );
    });
});
