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

const testedRoles = [
    UserRole.ROLE_PIPELINE_ADMIN,
    UserRole.ROLE_DASHBOARD_ADMIN,
    UserRole.ROLE_DATA_EXPLORER_ADMIN,
    UserRole.ROLE_CONNECT_ADMIN,
    UserRole.ROLE_ASSET_ADMIN,
];

for (var i = 0; i < testedRoles.length; i++) {
    const testRole = testedRoles[i];
    describe('Test User Role ' + testedRoles[i], () => {
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
                .addRole(testRole)
                .build();

            UserUtils.addUser(user);

            cy.dataCy('user-accounts-table-row', { timeout: 10000 }).should(
                'have.length',
                2,
            );

            // Login as user
            cy.switchUser(user);

            // Check if every role displays correct navigation menu
            if (testRole == UserRole.ROLE_PIPELINE_ADMIN) {
                cy.dataCy('navigation-icon', { timeout: 10000 }).should(
                    'have.length',
                    4,
                );
            } else if (testRole == UserRole.ROLE_DASHBOARD_ADMIN) {
                cy.dataCy('navigation-icon', { timeout: 10000 }).should(
                    'have.length',
                    2,
                );
            } else if (testRole == UserRole.ROLE_DATA_EXPLORER_ADMIN) {
                cy.dataCy('navigation-icon', { timeout: 10000 }).should(
                    'have.length',
                    2,
                );
            } else if (testRole == UserRole.ROLE_CONNECT_ADMIN) {
                cy.dataCy('navigation-icon', { timeout: 10000 }).should(
                    'have.length',
                    3,
                );
            } else if (testRole == UserRole.ROLE_ASSET_ADMIN) {
                cy.dataCy('navigation-icon', { timeout: 10000 }).should(
                    'have.length',
                    1,
                );
            }

            // Login as admin and delete user
            cy.switchUser(UserUtils.adminUser);
            UserUtils.deleteUser(user);
        });
    });
}
