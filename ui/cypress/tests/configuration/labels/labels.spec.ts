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

describe('Add and Delete Label', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
    });

    it('Perform Test', () => {
        cy.visit('#/configuration/labels');

        // Add new label
        cy.dataCy('new-label-button').click();
        cy.dataCy('label-name').type('test');
        cy.dataCy('label-description').type('test test');
        cy.dataCy('save-label-button').click();

        // Check label
        cy.dataCy('available-labels-list').should('have.length', 1);
        cy.dataCy('label-text').should('have.text', ' test\n');

        // Delete label
        cy.dataCy('delete-label-button').click();
        cy.dataCy('available-labels-list').should('have.length', 0);
    });
});
