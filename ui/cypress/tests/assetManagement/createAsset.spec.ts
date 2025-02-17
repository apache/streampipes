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
import { AssetUtils } from '../../support/utils/asset/AssetUtils';
import { DashboardUtils } from '../../support/utils/DashboardUtils';
import { ConfigurationUtils } from '../../support/utils/configuration/ConfigurationUtils';

describe('Creates a new adapter, add to assets and export assets', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
        const adapterInput = AdapterBuilder.create('Machine_Data_Simulator')
            .setName('Machine Data Simulator Test')
            .addInput('input', 'wait-time-ms', '1000')
            .setStartAdapter(true)
            .build();

        ConnectUtils.testAdapter(adapterInput);
    });

    it('Perform Test', () => {
        // Create new asset from adapters
        AssetUtils.goToAssets();
        cy.dataCy('create-new-asset-button').click();
        cy.dataCy('asset-name').clear();
        cy.dataCy('asset-name').type('Test asset');
        cy.dataCy('save-asset').click();
        cy.get('.mdc-tab__text-label').contains('Asset Links').parent().click();
        cy.dataCy('assets-manage-links-button', { timeout: 5000 }).should(
            'be.enabled',
        );
        cy.dataCy('assets-manage-links-button').click();
        cy.dataCy('manage-assets-select-adapters-checkbox').click();
        cy.dataCy('manage-assets-select-data-sources-checkbox').click();
        cy.dataCy('assets-update-links-button').click();

        cy.dataCy('linked-resources-list').children().should('have.length', 2);
        cy.dataCy('save-asset-button').click();
        cy.dataCy('save-data-explorer-go-back-to-overview').click();

        // Leave and navigate back to Assets
        DashboardUtils.goToDashboard();
        AssetUtils.goToAssets();
        cy.dataCy('assets-table').should('have.length', 1);
    });
});
