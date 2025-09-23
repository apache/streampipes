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
        CompactAdapterUtils.getAndSaveNMachineDataSimulator();
        ConnectUtils.goToConnect();

        cy.dataCy('table-paginator', { timeout: 10000 }).within(() => {
            cy.get('mat-select').click();
        });
        cy.get('mat-option').contains('5').click();

        cy.dataCy('adapter-name', { timeout: 10000 }).should('have.length', 5);
        ConnectUtils.validateAdapterPagination();
    });

    it('Basic Filtering Name', () => {
        CompactAdapterUtils.getAndSaveNMachineDataSimulator();
        ConnectUtils.goToConnect();

        ConnectUtils.waitingForExistingAdapters();

        ConnectBtns.sortingHeader('Name').click();

        ConnectUtils.filterAdapterPagination('Name');
    });

    it('Basic Filtering CreatedAT', () => {
        CompactAdapterUtils.getAndSaveNMachineDataSimulator();
        ConnectUtils.goToConnect();
        ConnectUtils.waitingForExistingAdapters();
        ConnectUtils.filterAdapterPagination('Created');
    });

    it('Basic Filtering Running', () => {
        CompactAdapterUtils.getAndSaveNMachineDataSimulator();
        ConnectUtils.goToConnect();

        ConnectUtils.waitingForExistingAdapters();

        // Add one Adapter running
        const compactAdapter = CompactAdapterUtils.getMachineDataSimulator()
            .setStart()
            .build();
        0;
        CompactAdapterUtils.storeCompactAdapter(compactAdapter).then(() => {
            cy.wait(1000);

            ConnectBtns.sortingHeader('Status').should('be.visible').click();

            ConnectUtils.filterAdapterPagination('Status');
        });
    });
    it('Basic Filtering Category', () => {
        CompactAdapterUtils.getAndSaveNMachineDataSimulator();
        ConnectUtils.goToConnect();
        ConnectUtils.waitingForExistingAdapters();

        ConnectUtils.filterAdapterForCategory('Finance');

        cy.dataCy('no-table-entries').should('be.visible');
        cy.dataCy('no-table-entries')
            .should('be.visible')
            .contains('No entries available.');
        ConnectUtils.filterAdapterForCategory('Debugging');
        cy.dataCy('no-table-entries').should('not.exist');
    });
});
