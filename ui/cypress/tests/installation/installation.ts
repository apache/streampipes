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

import { UserUtils } from '../../support/utils/UserUtils';


describe('Install StreamPipes', () => {
  let isSetupPage: boolean;

  it('Perform Test', () => {

    cy.visit('#/login');

    // This wait ensures that the installation site is completely loaded
    // If a better solution is found this wait can be removed
    // The test should work for both cases either when the system is already installed and when it should be installed
    cy.wait(10000);

    cy.url({ timeout: 60000 }).then(($route) => {
      isSetupPage = ($route.endsWith('setup')) ? true : false;
      if (isSetupPage) {
        cy.get('input[name="email"]').type(UserUtils.testUserName);
        cy.get('input[name="password"]').type(UserUtils.testUserPassword);

        cy.get('button').contains('Install').parent().click();

        cy.dataCy('sp-button-finish-installation', { timeout: 240000 }).should('be.visible');
        cy.dataCy('sp-button-finish-installation').click();
      }
    });
  });

});
