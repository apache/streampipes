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

import { StaticPropertyUtils } from '../userInput/StaticPropertyUtils';
import { GeneralUtils } from '../GeneralUtils';

export class PermissionUtils {
    public static openManagePermissions(resourceName: string) {
        GeneralUtils.openMenuForRow(resourceName);
        cy.dataCy('open-manage-permissions').click();
    }

    public static changeOwnership(resourceName: string, email: string) {
        PermissionUtils.openManagePermissions(resourceName);
        cy.dataCy('owner-select').click();
        cy.get(`[data-cy="owner-option-${email}"]`, { timeout: 10000 }).click();
        PermissionUtils.save();
    }

    public static markElementAsPublic(resourceName: string) {
        PermissionUtils.openManagePermissions(resourceName);
        StaticPropertyUtils.clickCheckbox('permission-public-element');
        PermissionUtils.save();
    }

    public static authorizeUser(resourceName: string, email: string) {
        PermissionUtils.openManagePermissions(resourceName);

        cy.dataCy('authorized-user').type(email);
        cy.get(`[data-cy="user-option-${email}"]`).click();

        PermissionUtils.save();
    }

    public static authorizeGroup(resourceName: string, groupName: string) {
        PermissionUtils.openManagePermissions(resourceName);
        cy.dataCy('authorized-group').type(groupName);
        cy.get(`[data-cy="group-option-${groupName}"]`).click();

        PermissionUtils.save();
    }

    public static save() {
        cy.dataCy('sp-manage-permissions-save').click();
    }

    public static cancel() {
        cy.dataCy('sp-manage-permissions-cancel').click();
    }

    public static validateUserCanNotChangePermissions(resourceName: string) {
        PermissionUtils.openManagePermissions(resourceName);
        cy.dataCy('warning-permissions-managed-by-owner').should('exist');
        PermissionUtils.cancel();
    }

    public static validateUserCanChangePermissions(resourceName: string) {
        PermissionUtils.openManagePermissions(resourceName);
        cy.dataCy('permission-public-element').should('exist');
        PermissionUtils.cancel();
    }
}
