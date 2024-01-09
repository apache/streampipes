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

import { ConnectUtils } from '../../support/utils/connect/ConnectUtils';
import { DashboardUtils } from '../../support/utils/DashboardUtils';

describe('Test edit dashboard', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
        ConnectUtils.addMachineDataSimulator('simulator', true);
    });

    it('Perform Test', () => {
        DashboardUtils.goToDashboard();

        // Add new dashboard
        const dashboardName = 'testDashboard';
        DashboardUtils.addAndEditDashboard(dashboardName);
        DashboardUtils.addWidget('Persist_simulator', 'raw');
        cy.dataCy('save-data-explorer-go-back-to-overview').click();

        // Change dashboard name
        cy.dataCy('change-dashboard-settings-button').click();
        cy.dataCy('dashboard-name-input')
            .clear()
            .type(dashboardName + 'New')
            .wait(1000);
        cy.dataCy('dashboard-save-btn').should('not.be.disabled');
        cy.dataCy('dashboard-save-btn').click();

        // Verify that new dashboard was renamed correctly
        cy.contains('testDashboardNew').should('exist');

        // Edit dashboard and change widget to line chart
        cy.dataCy('edit-dashboard-testDashboardNew').click().wait(2000);
        cy.dataCy('widget-settings-button').click();
        cy.dataCy('edit-widget-back-btn').click();
        cy.dataCy('dashboard-select-widget-line').click();
        cy.dataCy('min-y-axis-key').type('40');
        cy.dataCy('max-y-axis-key').type('50');
        cy.dataCy('dashboard-new-widget-next-btn').should('not.be.disabled');
        cy.dataCy('dashboard-new-widget-next-btn').click();

        // Verify that new dashboard displays correct chart
        cy.get('g').should('have.class', 'line-chart chart');

        // Add second chart, single value chart
        DashboardUtils.addWidget('Persist_simulator', 'number');

        // Go back and check if two newly added charts are displayed correctly in standalone dashboard
        cy.dataCy('save-data-explorer-go-back-to-overview').click();
        cy.dataCy('show-dashboard-testDashboardNew').click();
        cy.get('g').should('have.class', 'line-chart chart');
        cy.get('div').should('have.class', 'circleNumber');
    });
});
