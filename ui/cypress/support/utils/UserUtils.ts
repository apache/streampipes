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
import { UserBtns } from './user/UserBtns';
import { ConfigurationBtns } from './configuration/ConfigurationBtns';

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
        UserBtns.newUserBtn().should('exist');
    }

    public static addUser(user: User) {
        this.goToUserConfiguration();

        // user configuration
        UserBtns.newUserBtn().click();
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
        UserBtns.activateUserBtn().children().click();

        // Store
        UserBtns.saveEditUserBtn().click();
    }

    public static toggleUserRole(user: User, role: UserRole) {
        this.switchUser(this.adminUser);
        this.goToUserConfiguration();
        cy.get('table tbody tr', { timeout: 10000 }).should(
            'have.length.greaterThan',
            0,
        );

        UserBtns.editUserBtn(user.name);

        UserBtns.userRoleCheckbox(role).click();

        UserBtns.saveEditUserBtn().click();
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

        UserBtns.deleteUserBtn(user.name).click();
        UserBtns.confirmDeleteBtn().click();
    }

    public static createGroup(name: string, ...roles: UserRole[]) {
        this.goToUserConfiguration();

        ConfigurationBtns.newUserGroupBtn().click();
        ConfigurationBtns.inputGroupName(name);
        roles.forEach(role => {
            cy.get(`input[value="${role}"]`).check();
        });
        UserBtns.saveEditUserBtn().click();
    }

    public static addGroupToUser(groupName: string, name: string) {
        this.goToUserConfiguration();
        UserBtns.editUserBtn(name);

        cy.dataCy('group-' + groupName)
            .children()
            .click();

        UserBtns.saveEditUserBtn().click();
    }
}
