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

export class MeasurementUtils {
    public static goToDatalakeConfiguration() {
        cy.visit('#/configuration/datalake');
    }

    public static waitForCountingResults() {
        cy.dataCy('datalake-number-of-events-spinner', {
            timeout: 10000,
        }).should('exist');
        cy.dataCy('datalake-number-of-events-spinner', {
            timeout: 10000,
        }).should('not.exist');
    }

    public static getDatalakeNumberOfEvents(): Cypress.Chainable<string> {
        return cy
            .dataCy('datalake-number-of-events', { timeout: 10000 })
            .should('be.visible')
            .invoke('text')
            .then(text => text.trim());
    }

    public static deleteMeasurement() {
        cy.dataCy('datalake-delete-btn').should('be.visible').click();
        cy.dataCy('confirm-delete-data-btn', { timeout: 10000 })
            .should('be.visible')
            .click();
    }
}
