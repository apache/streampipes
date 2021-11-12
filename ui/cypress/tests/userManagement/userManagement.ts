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


describe('Test User Management', () => {
  before('Setup Test', () => {
    // cy.initStreamPipesTest();
    cy.login();
  });

  it('Perform Test', () => {
    // Add new user
    cy.visit('#/configuration');
    cy.get('div').contains('Security').parent().click();


    cy.dataCy('user-accounts-table-row', { timeout: 10000 }).should('have.length', 1);

    const email = 'user@streampipes.apache.org';
    const name = 'user';
    // user configuration
    cy.dataCy('add-new-user').click();
    cy.dataCy('new-user-email').type(email);
    cy.dataCy('new-user-full-name').type(name);
    cy.dataCy('new-user-password').type(name);
    cy.dataCy('new-user-password-repeat').type(name);

    // Set role
    cy.dataCy('role-ROLE_ADMIN').children().click();
    cy.dataCy('new-user-enabled').children().click();

    // Store
    cy.dataCy('sp-element-edit-user-save').click();

    cy.dataCy('user-accounts-table-row', { timeout: 10000 }).should('have.length', 2);

    cy.dataCy('user-delete-btn-' + name).click();

    cy.dataCy('user-accounts-table-row', { timeout: 10000 }).should('have.length', 1);
    // Validate that new user is in list

    // Logout

    // Login with new user

    // Logout

    // Login to Admin

    // Delete User
  });
});
