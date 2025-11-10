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
import { NavigationUtils } from '../../support/utils/navigation/NavigationUtils';
import { AssetUtils } from '../../support/utils/asset/AssetUtils';
import { AdapterBuilder } from '../../support/builder/AdapterBuilder';
import { AssetBtns } from '../../support/utils/asset/AssetBtns';

describe('Test User Roles for Pipelines', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();

        AssetUtils.goToAssets();
        AssetUtils.addAndSaveAsset('Asset');
    });

    /**it('Connect Asset Role Check ', () => {
        const newUser = UserUtils.createUser(
            'user',
            UserRole.ROLE_PIPELINE_ADMIN,
            UserRole.ROLE_ASSET_ADMIN,
            UserRole.ROLE_CONNECT_ADMIN,
            UserRole.ROLE_DATA_EXPLORER_ADMIN,
            UserRole.ROLE_DASHBOARD_ADMIN,
        );

        UserUtils.switchUser(newUser);

        ConnectUtils.goToConnect();

        ConnectUtils.addAdapter(
            AdapterBuilder.create('Machine_Data_Simulator')
                .setName('Machine Data Simulator Test 1')
                .addInput('input', 'wait-time-ms', '1000')
                .setStartAdapter(false)
                .build(),
        );
        cy.dataCy('show-asset-checkbox').should('exist');

        UserUtils.changeUserRole(newUser, UserRole.ROLE_ASSET_ADMIN);

        UserUtils.switchUser(newUser);

        ConnectUtils.goToConnect();

        ConnectUtils.addAdapter(
            AdapterBuilder.create('Machine_Data_Simulator')
                .setName('Machine Data Simulator Test2')
                .addInput('input', 'wait-time-ms', '1000')
                .setStartAdapter(false)
                .build(),
        );
        cy.dataCy('show-asset-checkbox').should('not.exist');
    });*/

    it('Pipeline Role Check ', () => {
        const newUser = UserUtils.createUser(
            'user',
            UserRole.ROLE_PIPELINE_ADMIN,
            UserRole.ROLE_ASSET_ADMIN,
            UserRole.ROLE_CONNECT_ADMIN,
            UserRole.ROLE_DATA_EXPLORER_ADMIN,
            UserRole.ROLE_DASHBOARD_ADMIN,
        );

        UserUtils.switchUser(newUser);

        PipelineUtils.goToPipelines();

        PipelineUtils.addSampleAdapterAndPipeline();

        PipelineUtils.editPipeline('Pipeline Test');

        cy.dataCy('sp-editor-save-pipeline').click();

        cy.log('FIRST ASSES');

        cy.dataCy('sp-show-pipeline-asset-checkbox').should('exist');

        //Navigate some where else to get out of the editor

        cy.dataCy('sp-editor-cancel').click();

        UserUtils.changeUserRole(newUser, UserRole.ROLE_ASSET_ADMIN);

        UserUtils.switchUser(newUser);

        PipelineUtils.goToPipelines();

        PipelineUtils.editPipeline('Pipeline Test');

        cy.dataCy('sp-editor-save-pipeline').click();

        cy.log('SECOND ASSES');

        cy.dataCy('sp-show-pipeline-asset-checkbox').should('not.exist');
    });

    /**
    it('Asset user should see add Assets in Connect', () => {
        const newUser = UserUtils.createUser(
            'user',
            UserRole.ROLE_PIPELINE_ADMIN,
        );

        // Add new authorized user to pipeline
        PipelineUtils.goToPipelines();
        PermissionUtils.markElementAsPublic('Persist simulator');

        // Login as user and check if pipeline is visible to user
        UserUtils.switchUser(newUser);

        PipelineUtils.goToPipelines();
        PipelineUtils.checkAmountOfPipelinesPipeline(1);
    });*/

    /**it(' Pipeline admin should see shared pipelines of other users', () => {
        const newUser = UserUtils.createUser(
            'user',
            UserRole.ROLE_PIPELINE_ADMIN,
        );

        // Add new authorized user to pipeline
        PipelineUtils.goToPipelines();
        PermissionUtils.markElementAsPublic('Persist simulator');
        PermissionUtils.authorizeUser('Persist simulator', newUser.email);

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
        PermissionUtils.authorizeUser('Persist simulator', newUser.email);

        // Login as user and check if pipeline is visible to user
        UserUtils.switchUser(newUser);

        PipelineUtils.goToPipelines();
        PipelineUtils.checkAmountOfPipelinesPipeline(1);

        // A pipeline user should not be able to stop the pipeline or delete it
        PipelineBtns.deletePipeline().should('not.exist');
        PipelineBtns.stopPipeline().should('be.disabled');
    });*/
});
