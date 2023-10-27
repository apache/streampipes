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

describe('Change basic settings', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
    });

    it('Perform Test', () => {
        cy.visit('#/configuration/general');

        // Rename app, change localhost and port
        cy.dataCy('general-config-app-name').clear();
        cy.dataCy('general-config-app-name').type('TEST APP');
        cy.dataCy('general-config-hostname').clear();
        cy.dataCy('general-config-hostname').type('testhost');
        cy.dataCy('general-config-port').clear();
        cy.dataCy('general-config-port').type('123');
        cy.dataCy('sp-element-general-config-save').click();

        // Leave, Re-visit configuration and check values
        cy.visit('#/dashboard');
        cy.visit('#/configuration/general');
        cy.dataCy('general-config-app-name').should('have.value', 'TEST APP');
        cy.dataCy('general-config-hostname').should('have.value', 'testhost');
        cy.dataCy('general-config-port').should('have.value', '123');
    });
});
