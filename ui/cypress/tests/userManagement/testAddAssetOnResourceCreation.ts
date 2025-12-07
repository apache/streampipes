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
import { PipelineBtns } from '../../support/utils/pipeline/PipelineBtns';
import { AssetUtils } from '../../support/utils/asset/AssetUtils';
import { AdapterBuilder } from '../../support/builder/AdapterBuilder';
import { DataExplorerUtils } from '../../support/utils/dataExplorer/DataExplorerUtils';
import { DataExplorerBtns } from '../../support/utils/dataExplorer/DataExplorerBtns';
import { ConnectBtns } from '../../support/utils/connect/ConnectBtns';
import { AssetBuilder } from '../../support/builder/AssetBuilder';

describe('Test that resources can be added to assets on creation', () => {
    let newUser;
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
        newUser = UserUtils.createUser(
            'user',
            UserRole.ROLE_PIPELINE_ADMIN,
            UserRole.ROLE_ASSET_ADMIN,
            UserRole.ROLE_CONNECT_ADMIN,
            UserRole.ROLE_DATA_EXPLORER_ADMIN,
            UserRole.ROLE_DASHBOARD_ADMIN,
        );

        AssetUtils.goToAssets();
        const asset = AssetBuilder.create('Asset').build();
        AssetUtils.addAndSaveAsset(asset);
    });

    it('Check Role Asset Admin in Connect', () => {
        UserUtils.switchUser(newUser);

        ConnectUtils.goToConnect();

        ConnectUtils.addAdapter(
            AdapterBuilder.create('Machine_Data_Simulator')
                .setName('Machine Data Simulator Test 1')
                .addInput('input', 'wait-time-ms', '1000')
                .setStartAdapter(false)
                .build(),
        );
        ConnectBtns.assetCheckbox().should('exist');

        UserUtils.toggleUserRole(newUser, UserRole.ROLE_ASSET_ADMIN);

        UserUtils.switchUser(newUser);

        ConnectUtils.goToConnect();

        ConnectUtils.addAdapter(
            AdapterBuilder.create('Machine_Data_Simulator')
                .setName('Machine Data Simulator Test2')
                .addInput('input', 'wait-time-ms', '1000')
                .setStartAdapter(false)
                .build(),
        );
        ConnectBtns.assetCheckbox().should('not.exist');
    });

    it('Check Role Asset Admin in Pipeline', () => {
        UserUtils.switchUser(newUser);

        PipelineUtils.goToPipelines();

        PipelineUtils.addSampleAdapterAndPipeline();

        PipelineUtils.editPipeline('Pipeline Test');

        PipelineBtns.pipelineEditorSave().click();

        PipelineBtns.pipelineAssetCheckbox().should('exist');

        PipelineBtns.pipelineEditorCancel().click();

        UserUtils.toggleUserRole(newUser, UserRole.ROLE_ASSET_ADMIN);

        UserUtils.switchUser(newUser);

        PipelineUtils.goToPipelines();

        PipelineUtils.editPipeline('Pipeline Test');

        PipelineBtns.pipelineEditorSave().click();

        PipelineBtns.pipelineAssetCheckbox().should('not.exist');
    });

    it('Check Role Asset Admin in Charts', () => {
        UserUtils.switchUser(newUser);

        DataExplorerUtils.goToDatalake();
        DataExplorerUtils.createAndEditDataView();

        DataExplorerBtns.chartAssetCheckboxBtn().should('exist');

        UserUtils.toggleUserRole(newUser, UserRole.ROLE_ASSET_ADMIN);

        UserUtils.switchUser(newUser);

        DataExplorerUtils.goToDatalake();
        DataExplorerUtils.createAndEditDataView();

        DataExplorerBtns.chartAssetCheckboxBtn().should('not.exist');
    });

    it('Check Role Asset Admin in Dashboard', () => {
        UserUtils.switchUser(newUser);
        DataExplorerUtils.goToDashboard();
        DataExplorerUtils.addNewDashboard('Test');

        DataExplorerBtns.dashboardAssetCheckboxBtn().should('exist');
        DataExplorerBtns.closeDashboardCreate().click();

        UserUtils.toggleUserRole(newUser, UserRole.ROLE_ASSET_ADMIN);

        UserUtils.switchUser(newUser);

        DataExplorerUtils.goToDashboard();
        DataExplorerUtils.addNewDashboard('Test');
        DataExplorerBtns.dashboardAssetCheckboxBtn().should('not.exist');
    });
});
