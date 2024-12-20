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
import { GeneralUtils } from '../../support/utils/GeneralUtils';
import { PermissionUtils } from '../../support/utils/user/PermissionUtils';
import { PipelineBtns } from '../../support/utils/pipeline/PipelineBtns';

describe('Test User Roles for Pipelines', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
        // Create a machine data simulator with a sample pipeline for the tests
        ConnectUtils.addMachineDataSimulator('simulator', true);
    });

    it('Pipeline admin should not see pipelines of other users', () => {
        const newUser = UserUtils.createUser(
            'user',
            UserRole.ROLE_PIPELINE_ADMIN,
        );

        // Login as user and check if pipeline is visible to user
        UserUtils.switchUser(newUser);

        GeneralUtils.validateAmountOfNavigationIcons(4);

        PipelineUtils.goToPipelines();
        PipelineUtils.checkAmountOfPipelinesPipeline(0);
    });

    it('Pipeline admin should see public pipelines of other users', () => {
        const newUser = UserUtils.createUser(
            'user',
            UserRole.ROLE_PIPELINE_ADMIN,
        );

        // Add new authorized user to pipeline
        PipelineUtils.goToPipelines();
        PermissionUtils.markElementAsPublic();

        // Login as user and check if pipeline is visible to user
        UserUtils.switchUser(newUser);

        PipelineUtils.goToPipelines();
        PipelineUtils.checkAmountOfPipelinesPipeline(1);
    });

    it(' Pipeline admin should see shared pipelines of other users', () => {
        const newUser = UserUtils.createUser(
            'user',
            UserRole.ROLE_PIPELINE_ADMIN,
        );

        // Add new authorized user to pipeline
        PipelineUtils.goToPipelines();
        PermissionUtils.markElementAsPublic();
        PermissionUtils.authorizeUser(newUser.email);

        // Login as user and check if pipeline is visible to user
        UserUtils.switchUser(newUser);

        PipelineUtils.goToPipelines();
        PipelineUtils.checkAmountOfPipelinesPipeline(1);
    });

    it(' Pipeline user should see shared pipelines of other users but not be able to edit them', () => {
        const newUser = UserUtils.createUser(
            'user',
            UserRole.ROLE_PIPELINE_USER,
        );

        // Add new authorized user to pipeline
        PipelineUtils.goToPipelines();
        // PermissionUtils.markElementAsPublic();
        PermissionUtils.authorizeUser(newUser.email);

        // Login as user and check if pipeline is visible to user
        UserUtils.switchUser(newUser);

        PipelineUtils.goToPipelines();
        PipelineUtils.checkAmountOfPipelinesPipeline(1);

        // A pipeline user should not be able to stop the pipeline or delete it
        PipelineBtns.deletePipeline().should('not.exist');
        PipelineBtns.stopPipeline().should('be.disabled');
    });
});
