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
import { ConfigurationUtils } from '../../support/utils/configuration/ConfigutationUtils';

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
        cy.dataCy('save-data-view').click();
        cy.dataCy('edit-asset-button').click();
        cy.get('.mdc-tab__text-label').contains('Asset Links').parent().click();
        cy.dataCy('assets-manage-links-button', { timeout: 5000 }).should(
            'be.enabled',
        );
        cy.dataCy('assets-manage-links-button').click();

        // Added twice, because cypress wouldn't accept single click
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

        // Export Asset
        ConfigurationUtils.goToConfigurationExport();
        cy.get('[type="checkbox"]').check();
        cy.dataCy('export-asset-button').click();
        cy.dataCy('download-export-button').click();

        // Delete Adapter and Asset
        ConnectUtils.goToConnect();
        cy.dataCy('delete-adapter').click();
        cy.dataCy('delete-adapter-confirmation').click();

        AssetUtils.goToAssets();
        cy.dataCy('delete').click();

        // Import downloaded Asset
        ConfigurationUtils.goToConfigurationExport();
        cy.dataCy('import-application-data-button').click();
        cy.get('input[type="file"]').selectFile(
            'cypress/downloads/data_export.zip',
            { force: true },
        );
        cy.dataCy('next-import-button').click();
        cy.dataCy('import-button').click();

        // Check if import was successful
        ConnectUtils.goToConnect();
        cy.dataCy('adapters-table').children().should('have.length', 1);
        AssetUtils.goToAssets();
        cy.dataCy('assets-table').should('have.length', 1);

        // Export Asset via Assets page
        cy.dataCy('download').click();

        // Delete Adapter and Asset
        ConnectUtils.goToConnect();
        cy.dataCy('delete-adapter').click();
        cy.dataCy('delete-adapter-confirmation').click();

        AssetUtils.goToAssets();
        cy.dataCy('delete').click();

        // Import downloaded Asset
        ConfigurationUtils.goToConfigurationExport();
        cy.dataCy('import-application-data-button').click();
        cy.get('input[type="file"]').selectFile(
            'cypress/downloads/assetExport.zip',
            { force: true },
        );
        cy.dataCy('next-import-button').click();
        cy.dataCy('import-button').click();

        // Check if import was successful
        ConnectUtils.goToConnect();
        cy.dataCy('adapters-table').children().should('have.length', 1);

        AssetUtils.goToAssets();
        cy.dataCy('assets-table').should('have.length', 1);
    });
});
