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
import { AssetBtns } from '../../support/utils/asset/AssetBtns';

describe('Test Saving Pipeline with Asset Link', () => {
    const assetName = 'Test';
    const assetName2 = 'Test2';
    const assetName3 = 'Test3';
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
        AssetUtils.addAssetWithNoAdapter(assetName3);
        AssetUtils.checkAmountOfAssets(1);
        AssetUtils.addAssetWithNoAdapter(assetName2);
        AssetUtils.checkAmountOfAssets(2);
        AssetUtils.addAssetWithNoAdapter(assetName);
        AssetUtils.checkAmountOfAssets(3);

        // Generate A Pipeline
        const adapterName = 'simulator';

        ConnectUtils.addMachineDataSimulator(adapterName);

        const pipelineInput = PipelineBuilder.create('Pipeline Test')
            .addSource(adapterName)
            .addSink(
                PipelineElementBuilder.create('data_lake')
                    .addInput('input', 'db_measurement', 'demo')
                    .build(),
            )
            .build();

        PipelineUtils.addPipelineWithAssetLinks(pipelineInput, [
            assetName,
            assetName2,
        ]);
    });

    it('Add Pipeline to Asset during creation', () => {
        PipelineUtils.deletePipeline(`Pipeline Test`);

        // Go Back to Asset
        AssetUtils.goToAssets();
        cy.wait(1000);

        // CLick on Asset

        AssetUtils.editAsset(assetName);
        AssetBtns.assetLinksTab().click();
        AssetUtils.checkAmountOfLinkedResources(2);

        // Go Back to Asset
        AssetUtils.goToAssets();
        cy.wait(1000);
        AssetUtils.editAsset(assetName2);
        AssetBtns.assetLinksTab().click();
        AssetUtils.checkAmountOfLinkedResources(2);
    });

    it('Edit Pipeline to Asset during Edit', () => {
        PipelineUtils.editPipeline('Pipeline Test');
        cy.wait(1000);
        cy.dataCy('sp-editor-save-pipeline').click();
        cy.dataCy('sp-editor-pipeline-name').clear();
        PipelineUtils.updatePipeline('Renamed Pipeline');
        PipelineUtils.finalizePipelineStart([assetName, assetName3]);

        // Test Number of Asset Links
        AssetUtils.checkAmountOfLinkedResourcesByAssetName(assetName2, 2);
        AssetUtils.checkAmountOfLinkedResourcesByAssetName(assetName3, 2);

        // Test Renaming
        AssetUtils.checkResourceNamingByAssetName(
            assetName2,
            'Renamed Pipeline',
        );
    });
});
