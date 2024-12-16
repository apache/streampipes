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
import { UserBuilder } from '../../support/builder/UserBuilder';
import { UserRole } from '../../../src/app/_enums/user-role.enum';
import { UserUtils } from '../../support/utils/UserUtils';
import { ConnectUtils } from '../../support/utils/connect/ConnectUtils';
import { ConnectBtns } from '../../support/utils/connect/ConnectBtns';
import { GeneralUtils } from '../../support/utils/GeneralUtils';

describe('Test User Roles for Connect', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
        ConnectUtils.addMachineDataSimulator('simulator');
    });

    it('Perform Test', () => {
        // Add connect admin user
        const connect_admin = UserBuilder.create('user@streampipes.apache.org')
            .setName('connect_admin')
            .setPassword('password')
            .addRole(UserRole.ROLE_CONNECT_ADMIN)
            .build();
        UserUtils.addUser(connect_admin);

        // Login as user and check if connect is visible to user
        UserUtils.switchUser(connect_admin);

        GeneralUtils.validateAmountOfNavigationIcons(3);

        ConnectUtils.goToConnect();

        ConnectUtils.checkAmountOfAdapters(1);

        // validate that adapter can be stopped and edited
        ConnectBtns.stopAdapter().click();
        ConnectBtns.editAdapter().should('not.be.disabled');
        ConnectBtns.editAdapter().click();
    });
});
