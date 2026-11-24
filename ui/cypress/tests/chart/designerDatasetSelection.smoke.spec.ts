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

import { AssetBuilder } from '../../support/builder/AssetBuilder';
import { AssetBtns } from '../../support/utils/asset/AssetBtns';
import { AssetUtils } from '../../support/utils/asset/AssetUtils';
import { ChartBtns } from '../../support/utils/chart/ChartBtns';
import { ChartUtils } from '../../support/utils/chart/ChartUtils';
import { DatasetSeedUtils } from '../../support/utils/dataset/DatasetSeedUtils';

describe('Chart designer dataset selection', () => {
    it('keeps selected fields when switching through an asset and guides the user back to missing fields', () => {
        cy.initStreamPipesTest();
        const source = 'compressor_a';
        const target = 'compressor_b';
        for (const measurementName of [source, target]) {
            DatasetSeedUtils.importCsvFixture({
                fixture: 'datalake/sample.csv',
                measurementName,
                delimiter: ';',
                timestampColumn: 'timestamp',
            });
        }

        AssetUtils.goToAssets();
        AssetUtils.addNewAsset(
            AssetBuilder.create('Line')
                .addSubAssetBuilder(AssetBuilder.create('Press'))
                .build(),
        );
        AssetUtils.selectSubAsset('Press');
        AssetUtils.openManageAssetLinks();
        AssetBtns.datasetCheckbox(target).check();
        AssetBtns.updateAssetLinksBtn().click();
        AssetBtns.saveAssetBtn().click();
        AssetBtns.createAssetBtn().should('be.visible');

        ChartUtils.goToDataset();
        ChartUtils.createAndEditChart();
        ChartUtils.selectDataSet(source);
        ChartUtils.openSettingsSection('fields');
        ChartBtns.deselectFields().click();
        ChartBtns.field('randomnumber').check();
        ChartUtils.selectDataSet(source);
        ChartBtns.settingsSection('fields')
            .children('summary')
            .should('contain.text', '1 selected');

        ChartUtils.browseAsset('Line');
        ChartBtns.linkedDataset(target)
            .should('contain.text', 'Line / Press')
            .click();
        ChartBtns.datasetInput().should('have.value', target);
        ChartBtns.field('randomnumber').should('be.checked');
        ChartBtns.field('randomtext').should('not.be.checked');

        ChartBtns.deselectFields().click();
        ChartBtns.settingsSection('fields').children('summary').click();
        ChartUtils.openVisualizationConfig();
        ChartUtils.selectVisualizationType('gauge');
        ChartBtns.chooseFields().click();
        ChartBtns.settingsSection('fields')
            .children('summary')
            .should('have.focus');
        ChartBtns.field('randomnumber').check();
        ChartUtils.openVisualizationConfig();
        ChartBtns.chooseFields().should('not.exist');
    });
});
