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

import { UserBuilder } from '../../support/builder/UserBuilder';
import { UserRole } from '../../../src/app/_enums/user-role.enum';
import { UserUtils } from '../../support/utils/UserUtils';

describe('Test User Management', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
    });

    it('Perform Test', () => {
        // Add new user
        UserUtils.goToUserConfiguration();
        cy.dataCy('navigation-icon', { timeout: 10000 }).should(
            'have.length',
            11,
        );

        cy.dataCy('user-accounts-table-row', { timeout: 10000 }).should(
            'have.length',
            1,
        );

        const email = 'user@streampipes.apache.org';
        const name = 'user';
        const user = UserBuilder.create(email)
            .setName(name)
            .setPassword(name)
            .addRole(UserRole.ROLE_DATA_EXPLORER_ADMIN)
            .build();

        UserUtils.addUser(user);

        cy.dataCy('user-accounts-table-row', { timeout: 10000 }).should(
            'have.length',
            2,
        );

        // Login as user
        cy.switchUser(user);

        cy.dataCy('navigation-icon', { timeout: 10000 }).should(
            'have.length',
            2,
        );

        cy.switchUser(UserUtils.adminUser);
        UserUtils.goToUserConfiguration();
        UserUtils.deleteUser(user);

        // Validate that user is removed
        cy.dataCy('user-accounts-table-row', { timeout: 10000 }).should(
            'have.length',
            1,
        );
    });
});
