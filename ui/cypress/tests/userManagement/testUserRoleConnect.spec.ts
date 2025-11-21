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
import { UserRole } from '../../../src/app/_enums/user-role.enum';
import { UserUtils } from '../../support/utils/UserUtils';
import { ConnectUtils } from '../../support/utils/connect/ConnectUtils';
import { PermissionUtils } from '../../support/utils/user/PermissionUtils';
import { User } from '../../support/model/User';
import { PipelineUtils } from '../../support/utils/pipeline/PipelineUtils';

describe('Test User Roles for Connect', () => {
    const adapterName = 'simulator';
    let user1: User;
    let user2: User;

    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
        user1 = UserUtils.createUser(
            'user1',
            UserRole.ROLE_CONNECT_ADMIN,
            UserRole.ROLE_PIPELINE_ADMIN,
        );

        user2 = UserUtils.createUser(
            'user2',
            UserRole.ROLE_CONNECT_ADMIN,
            UserRole.ROLE_PIPELINE_ADMIN,
        );
    });

    it('Adapter is not shared with other users', () => {
        // set up
        UserUtils.switchUser(user1);
        ConnectUtils.addMachineDataSimulator(adapterName);

        // check admin
        UserUtils.switchUser(UserUtils.adminUser);
        validateAdapterIsVisible();
        PermissionUtils.validateUserCanChangePermissions(adapterName);

        // check other users
        UserUtils.switchUser(user2);
        ConnectUtils.checkAmountOfAdapters(0);
    });

    it('Make adapter public', () => {
        // set up
        UserUtils.switchUser(user1);
        ConnectUtils.addMachineDataSimulator(adapterName);
        PermissionUtils.markElementAsPublic(adapterName);

        // check admin
        UserUtils.switchUser(UserUtils.adminUser);
        validateAdapterIsVisible();
        PermissionUtils.validateUserCanChangePermissions(adapterName);

        // check other users
        UserUtils.switchUser(user2);
        validateAdapterIsVisible();
        PermissionUtils.validateUserCanNotChangePermissions(adapterName);
    });

    it('Share adapter with other user and change ownership', () => {
        const user3 = UserUtils.createUser(
            'user3',
            UserRole.ROLE_CONNECT_ADMIN,
            UserRole.ROLE_PIPELINE_ADMIN,
        );

        // set up
        UserUtils.switchUser(user1);
        ConnectUtils.addMachineDataSimulator(adapterName);
        PermissionUtils.authorizeUser(adapterName, user2.email);

        // check admin
        UserUtils.switchUser(UserUtils.adminUser);
        validateAdapterIsVisible();
        PermissionUtils.validateUserCanChangePermissions(adapterName);

        // check authorized user
        UserUtils.switchUser(user2);
        validateAdapterIsVisible();
        PermissionUtils.validateUserCanNotChangePermissions(adapterName);

        UserUtils.switchUser(user3);
        ConnectUtils.checkAmountOfAdapters(0);

        // change ownership to user3
        UserUtils.switchUser(user1);
        ConnectUtils.goToConnect();
        PermissionUtils.changeOwnership(adapterName, user3.email);
        ConnectUtils.checkAmountOfAdapters(0);

        UserUtils.switchUser(UserUtils.adminUser);
        validateAdapterIsVisible();
        PermissionUtils.validateUserCanChangePermissions(adapterName);

        // check authorized user
        UserUtils.switchUser(user2);
        validateAdapterIsVisible();
        PermissionUtils.validateUserCanNotChangePermissions(adapterName);

        // validate that user3 is owner now
        UserUtils.switchUser(user3);
        validateAdapterIsVisible();
        PermissionUtils.validateUserCanChangePermissions(adapterName);
    });

    it('Adapter is shared with group for user 2', () => {
        // Add group with connect admin rights
        UserUtils.createGroup(
            'connect_admin_group',
            UserRole.ROLE_CONNECT_ADMIN,
        );
        UserUtils.addGroupToUser('connect_admin_group', user2.name);

        // set up
        UserUtils.switchUser(user1);
        ConnectUtils.addMachineDataSimulator(adapterName);

        PermissionUtils.authorizeGroup(adapterName, 'connect_admin_group');

        // check admin
        UserUtils.switchUser(UserUtils.adminUser);
        validateAdapterIsVisible();
        PermissionUtils.validateUserCanChangePermissions(adapterName);

        // check other users
        UserUtils.switchUser(user2);
        ConnectUtils.checkAmountOfAdapters(1);
    });

    function validateAdapterIsVisible() {
        ConnectUtils.checkAmountOfAdapters(1);

        ConnectUtils.validateEventsInPreview(adapterName, 7);

        PipelineUtils.goToPipelineEditor();
        PipelineUtils.checkDataStreamExists(adapterName);

        ConnectUtils.goToConnect();
    }
});
