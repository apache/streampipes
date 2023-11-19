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

import { AdapterBuilder } from '../../support/builder/AdapterBuilder';
import { ConnectUtils } from '../../support/utils/connect/ConnectUtils';
import { PipelineBuilder } from '../../support/builder/PipelineBuilder';
import { PipelineElementBuilder } from '../../support/builder/PipelineElementBuilder';
import { UserUtils } from '../../support/utils/UserUtils';
import { PipelineUtils } from '../../support/utils/PipelineUtils';

const adapterName = 'simulator';

const adapterInput = AdapterBuilder.create('Machine_Data_Simulator')
    .setName(adapterName)
    .addInput('input', 'wait-time-ms', '1000')
    .build();

const pipelineInput = PipelineBuilder.create('Pipeline Test')
    .addSource(adapterName)
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

describe('Test Enhanced Adapter Deletion', () => {
    // In the beginning, create the non-admin user
    before(() => {
        cy.initStreamPipesTest();
        UserUtils.addUser(UserUtils.adapterAndPipelineAdminUser);
        cy.logout();
    });

    // After the last test, let admin user clean up (delete the adapter, associated pipelines, and the non-admin user)
    after(() => {
        cy.switchUser(UserUtils.adminUser);
        ConnectUtils.goToConnect();
        cy.dataCy('delete-adapter', { timeout: 10000 }).should(
            'have.length',
            1,
        );
        ConnectUtils.clickDelete();
        cy.dataCy('delete-adapter-and-associated-pipelines-confirmation', {
            timeout: 10000,
        }).should('be.visible');
        cy.dataCy(
            'delete-adapter-and-associated-pipelines-confirmation',
        ).click();
        cy.dataCy('adapter-deletion-in-progress', { timeout: 10000 }).should(
            'be.visible',
        );
        ConnectUtils.checkAdapterAndAssociatedPipelinesDeleted();
        UserUtils.deleteUser(UserUtils.adapterAndPipelineAdminUser);
        // Verify that the user is removed
        cy.dataCy('user-accounts-table-row', { timeout: 10000 }).should(
            'have.length',
            1,
        );
    });

    beforeEach('Setup Test', () => {
        cy.visit('#/login');
        cy.dataCy('login-email').type(
            UserUtils.adapterAndPipelineAdminUser.email,
        );
        cy.dataCy('login-password').type(
            UserUtils.adapterAndPipelineAdminUser.password,
        );
        cy.dataCy('login-button').click();
        cy.wait(1000);
    });

    it('Test Delete Adapter and Associated Pipelines', () => {
        ConnectUtils.testAdapter(adapterInput);
        PipelineUtils.addPipeline(pipelineInput);
        PipelineUtils.addPipeline(pipelineInput);
        ConnectUtils.deleteAdapterAndAssociatedPipelines();
    });

    it('Test Admin Should Be Able to Delete Adapter and Not Owned Associated Pipelines', () => {
        // Let the user create the adapter and the pipeline
        ConnectUtils.testAdapter(adapterInput);
        PipelineUtils.addPipeline(pipelineInput);
        PipelineUtils.addPipeline(pipelineInput);
        // Then let the admin delete them
        cy.switchUser(UserUtils.adminUser);
        ConnectUtils.deleteAdapterAndAssociatedPipelines(true);
    });

    it('Test Delete Adapter and Associated Pipelines Permission Denied', () => {
        // Let the admin create the adapter and the pipeline
        cy.switchUser(UserUtils.adminUser);
        ConnectUtils.testAdapter(adapterInput);
        PipelineUtils.addPipeline(pipelineInput);
        PipelineUtils.addPipeline(pipelineInput);
        // Then the user shouldn't be able to delete them
        cy.switchUser(UserUtils.adapterAndPipelineAdminUser);
        ConnectUtils.deleteAdapterAndAssociatedPipelinesPermissionDenied();
    });
});
