/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

import { AssetBtns } from '../../../support/utils/asset/AssetBtns';
import { AssetUtils } from '../../../support/utils/asset/AssetUtils';
import { ConnectUtils } from '../../../support/utils/connect/ConnectUtils';
import { PipelineUtils } from '../../../support/utils/pipeline/PipelineUtils';
import { DataLakeUtils } from '../../../support/utils/datalake/DataLakeUtils';
import { MeasurementUtils } from '../../../support/utils/datalake/MeasurementUtils';

describe('Delete pipeline and measurements and auto remove asset links', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
    });

    it('Perform Test', () => {
        const assetName = 'TestAsset';
        ConnectUtils.addMachineDataSimulator('sample', true);

        // Add asset with pipeline
        AssetUtils.goToAssets();
        AssetUtils.addNewAsset(assetName);
        AssetBtns.assetLinksTab().click();
        AssetUtils.openManageAssetLinks();
        AssetUtils.selectPipelineAssetLink('persist_sample');
        AssetUtils.selectMeasurementAssetLink('sample');
        AssetBtns.updateAssetLinksBtn().click();
        AssetBtns.saveAssetBtn().click();

        // delete resources that should remove also asset links
        cy.wait(1000);
        PipelineUtils.deletePipeline();

        MeasurementUtils.goToDatalakeConfiguration();
        MeasurementUtils.deleteMeasurement();

        // Check that asset link is removed
        AssetUtils.goToAssets();
        AssetUtils.checkAmountOfAssets(1);
        AssetUtils.editAsset(assetName);
        AssetBtns.assetLinksTab().click();
        AssetUtils.checkAmountOfLinkedResources(0);
    });
});
