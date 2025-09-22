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
        cy.initStreamPipesTest();
    });

    it('Basic Paging check', () => {
        //CompactAdapterUtils.getAndSaveNMachineDataSimulator()
        ConnectUtils.goToConnect();
        cy.dataCy('table-paginator').within(() => {
            cy.get('mat-select').click();
        });
        cy.get('mat-option').contains('5').click();
        cy.get('[data-cy="adapter-name"]').should('have.length', 5);
        ConnectUtils.validateAdapterPagination();
    });

    //it('Basic Filtering CreatedAT', () => {
    // Click on filter

    // Click Again

    //                cy.get('table.mat-table')
    //  .find('tr[mat-row]')
    //  .first()
    //  .find('td.mat-cell')
    // .eq(0) // First column (0-based index)
    // .should('contain.text', 'Expected Value');
    //});

    it('Basic Filtering Name', () => {
        ConnectUtils.goToConnect();
        cy.get('th[mat-sort-header=""] .mat-sort-header-content', {
            timeout: 10000,
        })
            .contains('Name') // Make sure we're targeting the "Name" column
            .should('be.visible') // Wait until it's visible
            .click(); // Click to sort by 'Name'
        // Wait for sorting to complete (you could adjust or replace this with a more reliable method)
        cy.wait(500);

        // Get the first row's data before sorting
        cy.get('table.mat-table')
            .find('tr.mat-row')
            .first()
            .within(() => {
                cy.get('[data-cy="adapter-name"]').then($name => {
                    const firstItemNameBefore = $name.text(); // Save the name of the first item
                    cy.log('First item before sorting: ' + firstItemNameBefore);

                    // Click the sort header for 'Name' column again (if it sorts in both directions)
                    cy.get('th[mat-sort-header=""] .mat-sort-header-content')
                        .contains('Name')
                        .click();

                    // Wait for sorting to complete again
                    cy.wait(500);

                    // Get the first row again after sorting
                    cy.get('table.mat-table')
                        .find('tr.mat-row')
                        .first()
                        .within(() => {
                            cy.get('[data-cy="adapter-name"]').then(
                                $newName => {
                                    const firstItemNameAfter = $newName.text(); // Save the name of the new first item
                                    cy.log(
                                        'First item after sorting: ' +
                                            firstItemNameAfter,
                                    );

                                    // Assert that the first item name has changed
                                    expect(firstItemNameBefore).to.not.equal(
                                        firstItemNameAfter,
                                    );
                                },
                            );
                        });
                });
            });
    });

    it('Basic Filtering Running', () => {});
    it('Basic Filtering Category', () => {});
});
