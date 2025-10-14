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

import { ConnectUtils } from '../../support/utils/connect/ConnectUtils';
import { AdapterBuilder } from '../../support/builder/AdapterBuilder';
import { AssetUtils } from '../../support/utils/asset/AssetUtils';
import { ConnectEventSchemaUtils } from '../../support/utils/connect/ConnectEventSchemaUtils';

describe('Creates a new adapter with a linked asset', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
        const assetName = 'TestAsset';
        AssetUtils.addAssetWithNoAdapter(assetName);
    });

    it('Perform Test', () => {
        // Create

        const adapterConfiguration = AdapterBuilder.create(
            'Machine_Data_Simulator',
        )
            .setName('Machine Data Simulator Test')
            .addInput('input', 'wait-time-ms', '1000')
            .setStartAdapter(false)
            .build();

        ConnectUtils.goToConnect();
        cy.wait(10000);

        ConnectUtils.goToNewAdapterPage();

        ConnectUtils.selectAdapter(adapterConfiguration.adapterType);

        ConnectUtils.configureAdapter(adapterConfiguration);

        ConnectEventSchemaUtils.finishEventSchemaConfiguration();

        ConnectUtils.startAdapter(adapterConfiguration, false, false, true);

        // Go Back to Asset

        // CLick on Asset

        //Check if Link is there

        //ConnectUtils.addToAsset(adapterConfiguration);
        // Relevant for the Edit Case
        //const adapterInput = AdapterBuilder.create('Machine_Data_Simulator')
        //    .setName('Machine Data Simulator Test')
        //    .addInput('input', 'wait-time-ms', '1000')
        //    .setStartAdapter(false)
        //    .build();

        //ConnectUtils.testAdapter(adapterInput);

        //ConnectUtils.startAndValidateAdapter(7);
    });
});
