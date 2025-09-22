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

import { ConnectUtils } from '../../support/utils/connect/ConnectUtils';
import { ConnectBtns } from '../../support/utils/connect/ConnectBtns';
import { CompactAdapterUtils } from '../../support/utils/connect/CompactAdapterUtils';

describe('Adapter Paging Test', () => {
    beforeEach('Setup Test', () => {
        // Initialize the StreamPipes test and wait for token to be available
        cy.initStreamPipesTest();
        //TODO Add token working in here

        // Optionally, you can add one adapter running for testing purposes
        // const compactAdapter = CompactAdapterUtils.getMachineDataSimulator()
        //     .setStart()
        //     .build();
        // CompactAdapterUtils.storeCompactAdapter(compactAdapter);
    });

    it('Basic Paging check', () => {
        for (let i = 0; i < 10; i++) {
            const compactAdapter = CompactAdapterUtils.getMachineDataSimulator(
                'test_' + i,
            ).build();

            CompactAdapterUtils.storeCompactAdapter(compactAdapter);
        }

        ConnectUtils.goToConnect();
        cy.dataCy('table-paginator').within(() => {
            cy.get('mat-select').click();
        });
        cy.get('mat-option').contains('5').click();
        cy.get('[data-cy="adapter-name"]').should('have.length', 5);

        cy.get('[data-cy="adapter-name"]')
            .first()
            .invoke('text')
            .then(firstPageFirstItem => {
                cy.get('[data-cy="adapter-name"]').should('have.length', 5);
                cy.get('[data-cy="table-paginator"]')
                    .find('button[aria-label="Next"]')
                    .should('not.be.disabled')
                    .click();

                cy.wait(10000);
                cy.get('[data-cy="adapter-name"]')
                    .first()
                    .should('exist')
                    .invoke('text')
                    .should(secondPageFirstItem => {
                        expect(secondPageFirstItem.trim()).to.not.equal(
                            firstPageFirstItem.trim(),
                        );
                    });
            });

        // Click on Next

        //cy.get('[data-cy="table-paginator"]')
        //    .find('button[aria-label="Next"]')
        //    .click();

        // Wait for updated data (for example, by checking that first row is different)
        //cy.get('table.mat-table')
        // .find('tr[mat-row]')
        // .first()
        // .find('td.mat-cell')
        // .eq(0)
        // .should('not.contain.text', 'First row from previous page');

        // calculate lust of items on page 2 and validate
        //cy.get('[data-cy="adapter-name"]').should('have.length', 4);
    });

    it('Basic Filtering CreatedAT', () => {
        //                cy.get('table.mat-table')
        //  .find('tr[mat-row]')
        //  .first()
        //  .find('td.mat-cell')
        // .eq(0) // First column (0-based index)
        // .should('contain.text', 'Expected Value');
    });

    it('Basic Filtering Name', () => {});

    it('Basic Filtering Running', () => {});
    it('Basic Filtering Category', () => {});
});
