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

describe('Test User Roles for Pipelines', () => {
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
        // Add new user
        UserUtils.goToUserConfiguration();

        cy.dataCy('user-accounts-table-row', { timeout: 10000 }).should(
            'have.length',
            1,
        );

        const email = 'user@streampipes.apache.org';
        const name = 'test_user';
        const user = UserBuilder.create(email)
            .setName(name)
            .setPassword(name)
            .addRole(UserRole.ROLE_PIPELINE_USER)
            .build();

        UserUtils.addUser(user);

        // Check if user is added successfully
        cy.dataCy('user-accounts-table-row', { timeout: 10000 }).should(
            'have.length',
            2,
        );

        // Add new authorized user to pipeline
        PipelineUtils.goToPipelines();
        cy.dataCy('share').click();
        cy.get('label').contains('Authorized Users').click();
        cy.get('mat-option').contains(email).click();
        cy.dataCy('sp-element-edit-user-save').click();

        // Login as user and check if pipeline is visible to user
        cy.switchUser(user);

        cy.dataCy('navigation-icon', { timeout: 10000 }).should(
            'have.length',
            2,
        );

        PipelineUtils.goToPipelines();
        cy.dataCy('all-pipelines-table', { timeout: 10000 }).should(
            'have.length',
            1,
        );
        cy.dataCy('all-pipelines-table', { timeout: 10000 }).should(
            'contain',
            'Pipeline Test',
        );

        // Delete user
        cy.switchUser(UserUtils.adminUser);
        UserUtils.deleteUser(user);
    });
});
