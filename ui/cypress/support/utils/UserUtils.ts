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

import { User } from '../model/User';
import { UserBuilder } from '../builder/UserBuilder';
import { UserRole } from '../../../src/app/_enums/user-role.enum';

export class UserUtils {
    public static adminUser = UserBuilder.create('admin@streampipes.apache.org')
        .setName('admin')
        .setPassword('admin')
        .addRole(UserRole.ROLE_ADMIN)
        .build();

    public static userWithAdapterAndPipelineAdminRights = UserBuilder.create(
        'anpadmin@streampipes.apache.org',
    )
        .setName('anpadmin')
        .setPassword('anpadmin')
        .addRole(UserRole.ROLE_PIPELINE_ADMIN)
        .addRole(UserRole.ROLE_CONNECT_ADMIN)
        .build();

    public static goToLogin() {
        cy.visit('#/login');
    }

    public static goToUserConfiguration() {
        cy.visit('#/configuration/security');
    }

    public static addUser(user: User) {
        this.goToUserConfiguration();

        // user configuration
        cy.dataCy('add-new-user', { timeout: 10000 }).click();
        cy.dataCy('new-user-email').type(user.email);
        cy.dataCy('new-user-full-name').type(user.name);
        cy.dataCy('new-user-password').type(user.password);
        cy.dataCy('new-user-password-repeat').type(user.password);

        // Set role
        for (var i = 0; i < user.role.length; i++) {
            cy.dataCy('role-' + user.role[i])
                .children()
                .click();
        }
        cy.dataCy('new-user-enabled').children().click();

        // Store
        cy.dataCy('sp-element-edit-user-save').click();
    }

    /**
     * Create a new user with the specified roles and a default password to the system.
     *
     * @param name - The name of the user to be added.
     * @param roles - The roles to be assigned to the new user.
     */
    public static createUser(name: string, ...roles: UserRole[]): User {
        const userBuilder = UserBuilder.create(`${name}@streampipes.apache.org`)
            .setName(name)
            .setPassword('default');

        roles.forEach(role => userBuilder.addRole(role));

        const user = userBuilder.build();

        this.addUser(user);
        return user;
    }

    public static switchUser(user: User) {
        cy.logout();
        UserUtils.goToLogin();
        cy.dataCy('login-email').type(user.email);
        cy.dataCy('login-password').type(user.password);
        cy.dataCy('login-button').click();
        cy.wait(1000);
    }

    public static deleteUser(user: User) {
        this.goToUserConfiguration();

        cy.dataCy('user-delete-btn-' + user.name).click();
        cy.dataCy('confirm-delete').click();
    }
}
