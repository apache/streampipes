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

import { UserUtils } from '../../../support/utils/UserUtils';
import { UserRole } from '../../../../src/app/_enums/user-role.enum';
import { PipelineUtils } from '../../../support/utils/pipeline/PipelineUtils';

describe('Test Pipeline Multi User support', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
    });

    it('Test with pipeline admin account', () => {
        testPipelineNotShownForOtherUsers(UserRole.ROLE_PIPELINE_ADMIN);
    });

    it('Test with pipeline user account', () => {
        testPipelineNotShownForOtherUsers(UserRole.ROLE_PIPELINE_USER);
    });

    /**
     * This function validates that the pipeline is only shown to
     * the user who created it
     */
    function testPipelineNotShownForOtherUsers(userRole: UserRole) {
        const pipelineAdminUser = UserUtils.createUser('user1', userRole);

        PipelineUtils.addSamplePipeline();

        UserUtils.switchUser(pipelineAdminUser);

        PipelineUtils.checkAmountOfPipelinesPipeline(0);
    }
});
