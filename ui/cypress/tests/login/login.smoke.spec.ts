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

describe('Login and logout of StreamPipes', () => {
    it('Perform Test', () => {
        cy.visit('#/login');
        cy.dataCy('login-email').type(UserUtils.adminUser.email);
        cy.dataCy('login-password').type(UserUtils.adminUser.password);
        cy.dataCy('login-button').click();

        cy.dataCy('sp-user-preferences').click();
        cy.dataCy('sp-logout').click();
    });
});
