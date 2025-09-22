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
        cy.wait(1000);

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
        // TODO Add One Item as running
        ConnectUtils.goToConnect();

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
    it('Basic Filtering Category', () => {});
});
