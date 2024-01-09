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

import { UserBuilder } from '../../support/builder/UserBuilder';
import { UserRole } from '../../../src/app/_enums/user-role.enum';
import { UserUtils } from '../../support/utils/UserUtils';
import { ConnectUtils } from '../../support/utils/connect/ConnectUtils';
import { PipelineUtils } from '../../support/utils/PipelineUtils';
import { PipelineElementBuilder } from '../../support/builder/PipelineElementBuilder';
import { PipelineBuilder } from '../../support/builder/PipelineBuilder';

describe('Test Group Management for Pipelines', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
        ConnectUtils.addMachineDataSimulator('simulator');
        const pipelineInput = PipelineBuilder.create('Pipeline Test')
            .addSource('simulator')
            .addProcessingElement(
                PipelineElementBuilder.create('field_renamer')
                    .addInput('drop-down', 'convert-property', 'timestamp')
                    .addInput('input', 'field-name', 't')
                    .build(),
            )
            .addSink(
                PipelineElementBuilder.create('data_lake')
                    .addInput('input', 'db_measurement', 'demo')
                    .build(),
            )
            .build();

        PipelineUtils.addPipeline(pipelineInput);
    });

    it('Perform Test', () => {
        // Add two new user with same role
        UserUtils.goToUserConfiguration();

        cy.dataCy('user-accounts-table-row', { timeout: 10000 }).should(
            'have.length',
            1,
        );

        const email = 'user1@streampipes.apache.org';
        const name = 'test_user';
        const user = UserBuilder.create(email)
            .setName(name)
            .setPassword(name)
            .addRole(UserRole.ROLE_PIPELINE_USER)
            .build();

        UserUtils.addUser(user);

        const email2 = 'user2@streampipes.apache.org';
        const name2 = 'test_user2';
        const user2 = UserBuilder.create(email2)
            .setName(name2)
            .setPassword(name2)
            .addRole(UserRole.ROLE_PIPELINE_USER)
            .build();

        UserUtils.addUser(user2);

        // Check if users were added successfully
        cy.dataCy('user-accounts-table-row', { timeout: 10000 }).should(
            'have.length',
            3,
        );

        // Add new user group with pipeline admin role
        cy.get('button').contains('New User Group').click();
        cy.get('label').contains('Group Name').type('User_Group');
        cy.get('input[value="ROLE_PIPELINE_ADMIN"]').check();
        cy.dataCy('sp-element-edit-user-save').click();

        // Add first user to group
        cy.dataCy('user-edit-btn').eq(1).click();
        cy.get('input[type="checkbox"]').eq(0).check();
        cy.dataCy('sp-element-edit-user-save').click();

        // Add user group to pipeline
        PipelineUtils.goToPipelines();
        cy.dataCy('share').click();
        cy.get('label').contains('Authorized Groups').click();
        cy.get('mat-option').contains('User_Group').click();
        cy.dataCy('sp-element-edit-user-save').click();

        // Login as first user which belongs to user group with pipeline admin role
        cy.switchUser(user);

        cy.dataCy('navigation-icon', { timeout: 10000 }).should(
            'have.length',
            4,
        );

        // Check if pipeline is visible
        PipelineUtils.goToPipelines();
        cy.dataCy('all-pipelines-table', { timeout: 10000 }).should(
            'have.length',
            1,
        );
        cy.dataCy('all-pipelines-table', { timeout: 10000 }).should(
            'contain',
            'Pipeline Test',
        );

        // Login as user2
        cy.switchUser(user2);

        cy.dataCy('navigation-icon', { timeout: 10000 }).should(
            'have.length',
            2,
        );

        // Check if pipeline is invisible to user2
        PipelineUtils.goToPipelines();
        cy.get('sp-pipeline-overview', { timeout: 10000 }).should(
            'contain',
            '(no pipelines available)',
        );

        // Log in as admin and delete users
        cy.switchUser(UserUtils.adminUser);
        UserUtils.deleteUser(user);
        UserUtils.deleteUser(user2);

        // Delete group
        cy.dataCy('service-delete-btn').eq(1).click();
        cy.dataCy('confirm-delete').click();
    });
});
