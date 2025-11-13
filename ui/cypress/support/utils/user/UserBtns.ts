/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

export class UserBtns {
    public static editUserBtn(username) {
        cy.get('[data-cy="security-user-config"]')
            .find('tr')
            .contains('b', username)
            .closest('tr')
            .within(() => {
                cy.get('[data-cy="user-edit-btn"]')
                    .should('be.visible')
                    .click();
            });
    }

    public static userRoleCheckbox(role) {
        return cy.dataCy('role-' + role).children();
    }

    public static saveEditUserBtn() {
        return cy.dataCy('sp-element-edit-user-save');
    }

    public static newUserBtn() {
        return cy.dataCy('add-new-user', { timeout: 10000 });
    }

    public static activateUserBtn() {
        return cy.dataCy('new-user-enabled');
    }

    public static deleteUserBtn(name) {
        return cy.dataCy('user-delete-btn-' + name);
    }

    public static confirmDeleteBtn() {
        return cy.dataCy('confirm-delete');
    }
    public static firstEditUserBtn() {
        return cy.dataCy('user-edit-btn').eq(1);
    }
    public static serviceDeleteBtn() {
        return cy.dataCy('service-delete-btn');
    }
}
