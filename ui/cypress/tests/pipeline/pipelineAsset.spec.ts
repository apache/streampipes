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

import { PipelineUtils } from '../../support/utils/pipeline/PipelineUtils';
import { AssetUtils } from '../../support/utils/asset/AssetUtils';
import { ConnectUtils } from '../../support/utils/connect/ConnectUtils';
import { PipelineBuilder } from '../../support/builder/PipelineBuilder';
import { PipelineElementBuilder } from '../../support/builder/PipelineElementBuilder';
import { AssetBuilder } from '../../support/builder/AssetBuilder';

describe('Test Saving Pipeline with Asset Link', () => {
    const assetName1 = 'Test1';
    const assetName2 = 'Test2';
    const assetName3 = 'Test3';
    const initialPipelineName = 'Pipeline Test';
    const renamedPipelineName = 'Renamed Pipeline';
    const linkedPipelineResources = 1;

    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
        AssetUtils.goToAssets();
        const asset1 = AssetBuilder.create(assetName1).build();
        const asset2 = AssetBuilder.create(assetName2).build();
        const asset3 = AssetBuilder.create(assetName3).build();
        AssetUtils.addAndSaveAsset(asset3);
        AssetUtils.addAndSaveAsset(asset2);
        AssetUtils.addAndSaveAsset(asset1);

        // Generate A Pipeline
        const adapterName = 'simulator';

        ConnectUtils.addMachineDataSimulator(adapterName);

        const pipelineInput = PipelineBuilder.create(initialPipelineName)
            .addSource(adapterName)
            .addSink(
                PipelineElementBuilder.create('data_lake')
                    .addInput('input', 'db_measurement', 'demo')
                    .build(),
            )
            .build();

        PipelineUtils.addPipelineWithAssetLinks(pipelineInput, [
            assetName1,
            assetName2,
        ]);
        PipelineUtils.goToPipelines();
    });

    it('Add Pipeline to Asset during creation', () => {
        AssetUtils.goToAssets();
        AssetUtils.checkAmountOfAssetsGreaterThan(0);

        AssetUtils.editAsset(assetName1);
        AssetUtils.checkAmountOfLinkedResources(linkedPipelineResources);

        AssetUtils.goToAssets();
        AssetUtils.checkAmountOfAssetsGreaterThan(0);
        AssetUtils.editAsset(assetName2);
        AssetUtils.checkAmountOfLinkedResources(linkedPipelineResources);
    });

    it('Edit Pipeline to Asset during Edit', () => {
        PipelineUtils.editPipeline(initialPipelineName);
        PipelineUtils.openPipelineManagementInEditor();
        PipelineUtils.renameManagedPipeline(renamedPipelineName);
        PipelineUtils.addManagedPipelineToAssets([assetName3]);
        PipelineUtils.applyPipelineManagementChanges();
        PipelineUtils.savePipelineUpdate();

        AssetUtils.checkAmountOfLinkedResourcesByAssetName(
            assetName2,
            linkedPipelineResources,
        );
        AssetUtils.checkAmountOfLinkedResourcesByAssetName(
            assetName3,
            linkedPipelineResources,
        );

        AssetUtils.checkResourceNamingByAssetName(
            assetName2,
            renamedPipelineName,
        );
    });
});
