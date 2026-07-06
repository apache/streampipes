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

export class Inspector {
    public static getDashboardIdByName(name: string) {
        return cy
            .request({
                method: 'GET',
                url: '/streampipes-backend/api/v3/datalake/dashboard/summary',
                auth: {
                    bearer: window.localStorage.getItem('auth-token'),
                },
            })
            .then(response => {
                const dashboard = response.body.resources.find(
                    resource => resource.name === name,
                );

                expect(dashboard, `dashboard ${name}`).to.not.equal(undefined);
                return dashboard.elementId;
            });
    }

    public static openDashboardKioskAsAnonymous(dashboardId: string) {
        cy.clearLocalStorage();
        cy.clearCookies();
        cy.visit(`#/dashboard-kiosk/${dashboardId}`);
    }

    public static openDashboardKioskAsLoggedOutUser(dashboardId: string) {
        cy.clearLocalStorage();
        cy.clearCookies();
        cy.visit(`#/dashboard-kiosk/${dashboardId}`, {
            onBeforeLoad: win => {
                win.localStorage.clear();
                win.sessionStorage.clear();
                expect(win.localStorage.getItem('auth-token')).to.equal(null);
            },
        });
    }

    public static validateDashboardKioskWithTableChart(
        dashboardName: string,
        expectedColumns: string[] = [],
    ) {
        cy.contains('.dashboard-title', dashboardName, {
            timeout: 10000,
        }).should('be.visible');
        cy.dataCy('login-button').should('not.exist');
        cy.get('sp-data-explorer-table-widget', { timeout: 10000 }).should(
            'be.visible',
        );
        cy.get(
            '[data-cy="data-explorer-table"], [data-cy="data-explorer-no-data-in-date-range"]',
            { timeout: 10000 },
        )
            .filter(':visible')
            .should('have.length.at.least', 1);

        expectedColumns.forEach(column => {
            if (column === 'time') {
                cy.dataCy('data-explorer-table-row-timestamp', {
                    timeout: 10000,
                })
                    .filter(':visible')
                    .should('have.length.at.least', 1);
            } else {
                cy.dataCy(`data-explorer-table-header-${column}`, {
                    timeout: 10000,
                }).should('be.visible');
                cy.dataCy(`data-explorer-table-row-${column}`, {
                    timeout: 10000,
                })
                    .filter(':visible')
                    .should('have.length.at.least', 1);
            }
        });
    }
}
