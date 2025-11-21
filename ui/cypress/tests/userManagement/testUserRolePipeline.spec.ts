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

import { UserRole } from '../../../src/app/_enums/user-role.enum';
import { UserUtils } from '../../support/utils/UserUtils';
import { ConnectUtils } from '../../support/utils/connect/ConnectUtils';
import { PipelineUtils } from '../../support/utils/pipeline/PipelineUtils';
import { PermissionUtils } from '../../support/utils/user/PermissionUtils';
import { PipelineBtns } from '../../support/utils/pipeline/PipelineBtns';
import { User } from '../../support/model/User';

describe('Test User Roles for Pipelines', () => {
    const pipelineName = 'Persist simulator';
    let pipelineUser1: User;
    let pipelineAdmin1: User;
    let pipelineAdmin2: User;

    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();

        pipelineUser1 = UserUtils.createUser(
            'pipelineUser1',
            UserRole.ROLE_PIPELINE_USER,
        );

        pipelineAdmin1 = UserUtils.createUser(
            'pipelineAdmin1',
            UserRole.ROLE_CONNECT_ADMIN,
            UserRole.ROLE_PIPELINE_ADMIN,
        );

        pipelineAdmin2 = UserUtils.createUser(
            'pipelineAdmin2',
            UserRole.ROLE_PIPELINE_ADMIN,
        );
    });

    it('Pipeline is not shared with other users', () => {
        UserUtils.switchUser(pipelineAdmin1);
        ConnectUtils.addMachineDataSimulator('simulator', true);

        assertPipelineIsVisibleAndEditableCanChangePermissions(
            UserUtils.adminUser,
        );

        assertPipelineIsNotVisible(pipelineUser1);

        UserUtils.switchUser(pipelineUser1);

        assertPipelineIsNotVisible(pipelineAdmin2);
    });

    it('Make pipeline public', () => {
        UserUtils.switchUser(pipelineAdmin1);
        ConnectUtils.addMachineDataSimulator('simulator', true);

        PipelineUtils.goToPipelines();
        PermissionUtils.markElementAsPublic(pipelineName);

        assertPipelineIsVisibleAndEditableCanChangePermissions(
            UserUtils.adminUser,
        );

        assertPipelineIsVisibleButNotEditable(pipelineUser1);

        assertPipelineIsVisibleAndEditableCannotChangePermissions(
            pipelineAdmin2,
        );
    });

    it('Share pipeline with other user and change ownership', () => {
        UserUtils.switchUser(pipelineAdmin1);
        ConnectUtils.addMachineDataSimulator('simulator', true);

        PipelineUtils.goToPipelines();
        PermissionUtils.authorizeUser(pipelineName, pipelineAdmin2.email);

        assertPipelineIsVisibleAndEditableCanChangePermissions(
            UserUtils.adminUser,
        );

        assertPipelineIsVisibleAndEditableCannotChangePermissions(
            pipelineAdmin2,
        );

        assertPipelineIsNotVisible(pipelineUser1);

        UserUtils.switchUser(pipelineAdmin1);
        PipelineUtils.goToPipelines();
        PermissionUtils.changeOwnership(pipelineName, pipelineAdmin2.email);

        assertPipelineIsNotVisible(pipelineAdmin1);

        assertPipelineIsVisibleAndEditableCanChangePermissions(
            UserUtils.adminUser,
        );

        assertPipelineIsVisibleAndEditableCanChangePermissions(pipelineAdmin2);

        assertPipelineIsNotVisible(pipelineUser1);
    });

    it('Pipeline is shared with group for user 2', () => {
        UserUtils.createGroup(
            'pipeline_admin_group',
            UserRole.ROLE_PIPELINE_ADMIN,
        );
        UserUtils.addGroupToUser('pipeline_admin_group', pipelineAdmin2.name);

        // set up
        UserUtils.switchUser(pipelineAdmin1);
        ConnectUtils.addMachineDataSimulator('simulator', true);

        PipelineUtils.goToPipelines();
        PermissionUtils.authorizeGroup(pipelineName, 'pipeline_admin_group');

        assertPipelineIsVisibleAndEditableCanChangePermissions(
            UserUtils.adminUser,
        );

        assertPipelineIsNotVisible(pipelineUser1);

        assertPipelineIsVisibleAndEditableCannotChangePermissions(
            pipelineAdmin2,
        );
    });

    function assertPipelineIsVisibleAndEditableCanChangePermissions(
        user: User,
    ) {
        UserUtils.switchUser(user);
        PipelineUtils.goToPipelines();
        PipelineUtils.checkAmountOfPipelinesPipeline(1);
        PipelineBtns.stopPipeline().should('not.be.disabled');
        PermissionUtils.validateUserCanChangePermissions(pipelineName);
    }

    function assertPipelineIsVisibleAndEditableCannotChangePermissions(
        user: User,
    ) {
        UserUtils.switchUser(user);
        PipelineUtils.goToPipelines();
        PipelineUtils.checkAmountOfPipelinesPipeline(1);
        PipelineBtns.stopPipeline().should('not.be.disabled');
        PermissionUtils.validateUserCanNotChangePermissions(pipelineName);
    }

    function assertPipelineIsVisibleButNotEditable(user: User) {
        UserUtils.switchUser(user);
        PipelineUtils.goToPipelines();
        PipelineUtils.checkAmountOfPipelinesPipeline(1);
        PipelineBtns.stopPipeline().should('be.disabled');
        PermissionUtils.validateUserCanNotChangePermissions(pipelineName);
    }

    function assertPipelineIsNotVisible(user: User) {
        UserUtils.switchUser(user);
        PipelineUtils.goToPipelines();
        PipelineUtils.checkAmountOfPipelinesPipeline(0);
    }
});
