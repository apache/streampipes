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
        cy.wait(1000);
        cy.dataCy('table-paginator').within(() => {
            cy.get('mat-select').click();
        });
        cy.get('mat-option').contains('5').click();
        cy.wait(2000);

        cy.get('[data-cy="adapter-name"]').should('have.length', 5);
        ConnectUtils.validateAdapterPagination();
    });

    it('Basic Filtering Name', () => {
        ConnectUtils.goToConnect();
        cy.wait(1000);

        cy.get('th[mat-sort-header=""] .mat-sort-header-content', {
            timeout: 10000,
        })
            .contains('Name')
            .should('be.visible')
            .click();
        cy.wait(1000);
        ConnectUtils.filterAdapterPagination('Name');
    });

    it('Basic Filtering CreatedAT', () => {
        ConnectUtils.goToConnect();
        cy.wait(1000);
        ConnectUtils.filterAdapterPagination('Created');
    });

    it('Basic Filtering Running', () => {
        ConnectUtils.goToConnect();

        // Add one Adapter running
        const compactAdapter = CompactAdapterUtils.getMachineDataSimulator()
            .setStart()
            .build();
        0;
        CompactAdapterUtils.storeCompactAdapter(compactAdapter).then(() => {
            cy.wait(1000);

            cy.get('th[mat-sort-header=""] .mat-sort-header-content', {
                timeout: 10000,
            })
                .contains('Status')
                .should('be.visible')
                .click();
            cy.wait(1000);
            ConnectUtils.filterAdapterPagination('Status');
        });
    });
    it('Basic Filtering Category', () => {
        ConnectUtils.goToConnect();
        // Select a Invalid Category // Check for 0
        //cy.get('mat-select[formcontrolname="selectedCategory"]')  // Target the mat-select element (adjust selector as needed)
        //.click();
        cy.wait(1000);

        cy.get('[data-cy="category-select"]').click();

        cy.wait(500);

        // Select the desired category (assuming you want the category with label 'Category 1')
        cy.get('mat-option')
            .contains('Finance') // Replace 'Category 1' with the exact category name
            .click();
        cy.wait(1000);
        // Check that nothing is there
        cy.get('[data-cy="no-table-entries"]').should('be.visible');
        cy.get('[data-cy="no-table-entries"]')
            .should('be.visible')
            .contains('No entries available.');
        cy.wait(500);
        cy.get('[data-cy="category-select"]').click();

        // Select a Debugging Category // CHeck that smth there
        cy.get('mat-option')
            .contains('Debugging') // Replace 'Category 1' with the exact category name
            .click();
        cy.wait(1000);
        cy.get('[data-cy="no-table-entries"]').should('not.exist');
    });
});
