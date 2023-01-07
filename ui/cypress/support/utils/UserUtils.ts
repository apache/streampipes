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

    public static goToUserConfiguration() {
        cy.visit('#/configuration/security');
    }

    public static addUser(user: User) {
        this.goToUserConfiguration();

        // user configuration
        cy.dataCy('add-new-user').click();
        cy.dataCy('new-user-email').type(user.email);
        cy.dataCy('new-user-full-name').type(user.name);
        cy.dataCy('new-user-password').type(user.password);
        cy.dataCy('new-user-password-repeat').type(user.password);

        // Set role
        cy.dataCy('role-' + user.role[0])
            .children()
            .click();
        cy.dataCy('new-user-enabled').children().click();

        // Store
        cy.dataCy('sp-element-edit-user-save').click();
    }

    public static deleteUser(user: User) {
        this.goToUserConfiguration();

        cy.dataCy('user-delete-btn-' + user.name).click();
    }
}
