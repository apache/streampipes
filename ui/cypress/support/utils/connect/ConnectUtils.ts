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

import { StaticPropertyUtils } from '../userInput/StaticPropertyUtils';
import { AdapterInput } from '../../model/AdapterInput';
import { ConnectEventSchemaUtils } from './ConnectEventSchemaUtils';
import { DataExplorerUtils } from '../dataExplorer/DataExplorerUtils';
import { ConnectBtns } from './ConnectBtns';
import { AdapterBuilder } from '../../builder/AdapterBuilder';
import { UserUtils } from '../UserUtils';
import { PipelineUtils } from '../pipeline/PipelineUtils';
import { GeneralUtils } from '../GeneralUtils';

export class ConnectUtils {
    public static goToConnect() {
        cy.visit('#/connect');
        cy.dataCy('connect-create-new-adapter-button').should('be.visible');
    }

    public static testAdapter(
        adapterConfiguration: AdapterInput,
        adapterStartFails = false,
    ) {
        ConnectUtils.goToConnect();

        ConnectUtils.goToNewAdapterPage();

        ConnectUtils.selectAdapter(adapterConfiguration.adapterType);

        ConnectUtils.configureAdapter(adapterConfiguration);

        ConnectUtils.configureSchema(adapterConfiguration);

        ConnectUtils.eventSchemaWithFieldsShouldBeVisible();

        if (adapterConfiguration.timestampProperty) {
            ConnectEventSchemaUtils.markPropertyAsTimestamp(
                adapterConfiguration.timestampProperty,
            );
        }

        ConnectEventSchemaUtils.changePropertyDataTypes(
            adapterConfiguration.dataTypeChanges,
        );

        ConnectUtils.configureDimensionProperties(adapterConfiguration);

        ConnectEventSchemaUtils.configureFieldsNextBtnEnabled();
        ConnectBtns.configureFieldsNextBtn().click();

        ConnectUtils.startAdapter(
            adapterConfiguration,
            false,
            adapterStartFails,
        );
    }

    public static addAdapter(adapterConfiguration: AdapterInput) {
        ConnectUtils.goToConnect();

        ConnectUtils.goToNewAdapterPage();

        ConnectUtils.selectAdapter(adapterConfiguration.adapterType);

        ConnectUtils.configureAdapter(adapterConfiguration);

        cy.wait(1000);

        ConnectUtils.finishEventSchemaConfiguration();

        ConnectUtils.eventSchemaWithFieldsShouldBeVisible();

        ConnectUtils.configureDimensionProperties(adapterConfiguration);

        if (adapterConfiguration.timestampProperty) {
            ConnectEventSchemaUtils.markPropertyAsTimestamp(
                adapterConfiguration.timestampProperty,
            );
        }

        ConnectUtils.finishConfigureFieldsConfiguration();
    }

    public static addAdapterWithLinkedAssets(
        adapterConfiguration: AdapterInput,
        assetNameList,
    ) {
        ConnectUtils.addAdapter(adapterConfiguration);

        ConnectUtils.startAdapter(
            adapterConfiguration,
            false,
            false,
            true,
            assetNameList,
        );
    }

    public static configureSchema(adapterConfiguration: AdapterInput) {
        ConnectBtns.configureSchemaEventPreviewOriginal().should('be.visible');
        ConnectBtns.scriptActiveToggle().click();

        ConnectBtns.configureSchemaEventPreviewOriginal().should('be.visible');
        ConnectBtns.configureSchemaEventPreviewResult().should('be.visible');

        if (adapterConfiguration.autoAddTimestamp) {
            ConnectEventSchemaUtils.addTimestampProperty();
        }

        ConnectBtns.configureSchemaNextBtn().click();
    }

    public static configureDimensionProperties(
        adapterConfiguration: AdapterInput,
    ) {
        if (adapterConfiguration.dimensionProperties.length > 0) {
            adapterConfiguration.dimensionProperties.forEach(
                dimensionPropertyName => {
                    ConnectEventSchemaUtils.markPropertyAsDimension(
                        dimensionPropertyName,
                    );
                },
            );
        }
    }

    public static renameAdapter(newName: string) {
        ConnectBtns.adapterNameInput().clear().type(newName);
        ConnectBtns.adapterNameInput().should('have.value', newName);
    }

    public static addMachineDataSimulator(
        name: string,
        persist: boolean = false,
        waitingTime: string = '1000',
    ) {
        const builder = AdapterBuilder.create('Machine_Data_Simulator')
            .setName(name)
            .setTimestampProperty('timestamp')
            .addInput('input', 'wait-time-ms', waitingTime);

        if (persist) {
            builder.setTimestampProperty('timestamp').setStoreInDataLake();
        }

        ConnectUtils.testAdapter(builder.build());
    }

    public static goToNewAdapterPage() {
        cy.dataCy('connect-create-new-adapter-button').click();
    }

    public static selectAdapter(name: string) {
        cy.get('#' + name).click();
    }

    public static configureAdapter(adapterInput: AdapterInput) {
        StaticPropertyUtils.input(adapterInput.adapterConfiguration);

        this.configureFormat(adapterInput);

        ConnectUtils.finishAdapterSettings();
    }

    /**
     * Clicks next on the adapter settings page
     */
    public static finishAdapterSettings() {
        ConnectBtns.adapterSettingsNextBtn().should('not.be.disabled');
        ConnectBtns.adapterSettingsNextBtn().click();
    }

    public static configureFormat(adapterInput: AdapterInput) {
        if (adapterInput.format) {
            cy.dataCy('format-' + adapterInput.format) // Find the element with the data-cy attribute
                .within(() => {
                    cy.get('.mdc-radio').click();
                });

            StaticPropertyUtils.input(adapterInput.formatConfiguration);
        }
    }

    public static finishEventSchemaConfiguration() {
        ConnectBtns.configureSchemaNextBtn().click();
    }

    public static finishConfigureFieldsConfiguration() {
        ConnectUtils.eventSchemaWithFieldsShouldBeVisible();
        ConnectBtns.configureFieldsNextBtn().click();
    }

    public static eventSchemaWithFieldsShouldBeVisible() {
        ConnectBtns.eventPropertyRow().should('have.length.at.least', 1);
    }

    public static startAdapter(
        adapterInput: AdapterInput,
        noLiveDataView = false,
        adapterStartFails = false,
        addToAsset = false,
        assetNameList = [],
    ) {
        // Set adapter name
        ConnectBtns.adapterNameInput().type(adapterInput.adapterName);

        if (adapterInput.storeInDataLake) {
            cy.dataCy('sp-store-in-datalake', {
                timeout: 5000,
            })
                .should('be.visible')
                .children()
                .click();
            cy.dataCy('sp-store-in-datalake-timestamp')
                .click()
                .get('mat-option')
                .contains(adapterInput.timestampProperty)
                .click();
        }

        // Deselect auto start of adapter
        if (!adapterInput.startAdapter) {
            ConnectBtns.startAdapterNowCheckbox().click();
        }

        //add the Adapter to an Asset

        if (addToAsset) {
            this.addToAsset(assetNameList);
        }

        ConnectBtns.adapterSettingsStartAdapter().click();

        if (adapterStartFails) {
            cy.dataCy('sp-exception-details', {
                timeout: 60000,
            }).should('be.visible');
        } else {
            if (adapterInput.startAdapter && !noLiveDataView) {
                cy.dataCy('sp-connect-adapter-success-live-preview', {
                    timeout: 60000,
                }).should('be.visible');
            } else {
                cy.dataCy('sp-connect-adapter-success-added', {
                    timeout: 60000,
                }).should('be.visible');
            }
        }

        this.closeAdapterPreview();
    }

    public static addToAsset(assetNameList = []) {
        ConnectBtns.showAssetCheckbox().click();
        cy.get('mat-tree.asset-tree', { timeout: 10000 }).should('exist');

        assetNameList.forEach(assetName => {
            cy.get('mat-tree.asset-tree')
                .find('.mat-tree-node')
                .contains(assetName)
                .click();
        });
    }

    public static editAsset(assetNameList = []) {
        cy.get('mat-tree.asset-tree', { timeout: 10000 }).should('exist');

        assetNameList.forEach(assetName => {
            console.log(assetName);
            cy.get('mat-tree.asset-tree')
                .find('.mat-tree-node')
                .contains(assetName)
                .click();
        });
    }

    public static closeAdapterPreview() {
        cy.dataCy('close-adapter-started-dialog-button').click();
    }

    public static deleteAdapter(adapterName: string) {
        // Delete adapter
        this.goToConnect();

        GeneralUtils.openMenuForRow(adapterName);
        ConnectBtns.deleteAdapter().should('have.length', 1);
        this.clickDelete();
        cy.dataCy('adapter-deletion-in-progress', { timeout: 10000 }).should(
            'be.visible',
        );
        ConnectBtns.deleteAdapter().should('have.length', 0);
    }

    public static deleteAdapterAndAssociatedPipelines(switchUserCheck = false) {
        // Delete adapter and associated pipelines
        this.goToConnect();
        ConnectBtns.openActionsMenu('simulator');
        cy.dataCy('delete-adapter').should('have.length', 1);
        this.clickDelete();
        ConnectBtns.deleteAdapterAndAssociatedPipelineConfirmation().should(
            'be.visible',
        );
        ConnectBtns.deleteAdapterAndAssociatedPipelineConfirmation().click();
        cy.dataCy('adapter-deletion-in-progress', { timeout: 10000 }).should(
            'be.visible',
        );
        if (switchUserCheck) {
            UserUtils.switchUser(
                UserUtils.userWithAdapterAndPipelineAdminRights,
            );
        }
        this.checkAdapterAndAssociatedPipelinesDeleted();
    }

    public static clickDelete() {
        ConnectBtns.deleteAdapter().click();
        ConnectBtns.deleteAdapterConfirmationButton().click();
    }

    public static checkAdapterAndAssociatedPipelinesDeleted() {
        this.goToConnect();
        ConnectBtns.deleteAdapter().should('have.length', 0);
        PipelineUtils.goToPipelines();
        ConnectBtns.deleteAdapter().should('have.length', 0);
    }

    public static setUpPreprocessingRuleTest(
        overwriteTimestamp: boolean,
        adapterConfigurationBuilder?: AdapterBuilder,
    ): AdapterInput {
        if (!adapterConfigurationBuilder) {
            adapterConfigurationBuilder = AdapterBuilder.create('File_Stream')
                .setStoreInDataLake()
                .setTimestampProperty('timestamp')
                .addProtocolInput(
                    'radio',
                    'speed',
                    'fastest_\\(ignore_original_time\\)',
                )
                .addProtocolInput('radio', 'replayonce', 'yes')
                .setName('Adapter to test rules')
                .setFormat('csv')
                .addFormatInput('input', ConnectBtns.csvDelimiter(), ';')
                .addFormatInput('checkbox', ConnectBtns.csvHeader(), 'check');
        }

        if (overwriteTimestamp) {
            adapterConfigurationBuilder.addProtocolInput(
                'checkbox',
                'replaceTimestamp',
                'check',
            );
        }

        const adapterConfiguration = adapterConfigurationBuilder.build();

        ConnectUtils.goToConnect();
        ConnectUtils.goToNewAdapterPage();
        ConnectUtils.selectAdapter(adapterConfiguration.adapterType);
        ConnectUtils.configureAdapter(adapterConfiguration);

        ConnectBtns.configureSchemaEventPreviewOriginal().should('be.visible');
        ConnectBtns.scriptActiveToggle().click();

        return adapterConfiguration;
    }

    public static startAndValidateAdapter(
        adapterName: string,
        amountOfProperties: number,
    ) {
        ConnectBtns.startAdapter().should('not.be.disabled');

        ConnectBtns.startAdapter().click();

        ConnectUtils.validateEventsInPreview(adapterName, amountOfProperties);
    }

    public static getLivePreviewValue(runtimeName: string) {
        return cy.dataCy(`live-preview-value-${runtimeName}`, {
            timeout: 10000,
        });
    }

    public static replaceAdapterScript(script: string) {
        // Ensure that the script is loaded
        ConnectBtns.configureSchemaScriptEditor().should(
            'contain.text',
            'out.collect(event);',
        );

        ConnectBtns.configureSchemaScriptEditor()
            .type('{backspace}'.repeat(22)) // 2. Delete the "  out.collect(event);\n}" part
            .type(script);
    }

    public static uploadSampleEvent(samplePayload: string) {
        ConnectBtns.uploadSampleBtn().click();
        ConnectBtns.uploadSampleDialogTextarea()
            .should('be.visible')
            .clear()
            .type(samplePayload, { parseSpecialCharSequences: false });
        ConnectBtns.uploadSampleDialogSubmitBtn()
            .should('not.be.disabled')
            .click();
    }

    public static addScriptAsScriptTemplate(
        templateName: string,
        script: string,
    ) {
        ConnectUtils.replaceAdapterScript(script);
        ConnectBtns.addScriptTemplateBtn().click();

        ConnectBtns.scriptTemplateName().type(templateName);
        ConnectBtns.saveScriptTemplateBtn().click();
    }

    public static useScriptTemplate(templateName: string) {
        ConnectBtns.useScriptTemplateBtn().click();
        ConnectBtns.selectScriptTemplateDropDown().click();
        cy.get('mat-option').contains('span', templateName).click();
        ConnectBtns.saveSelectScriptTemplateBtn().click();
    }

    public static deleteScriptTemplate(templateName: string) {
        ConnectBtns.useScriptTemplateBtn().click();
        ConnectBtns.selectScriptTemplateDropDown().click();
        cy.get('mat-option').contains('span', templateName).click();
        ConnectBtns.deleteScriptTemplateBtn().click();
    }

    public static validateEventsInPreview(
        adapterName: string,
        amountOfProperties: number,
    ) {
        // View data
        ConnectBtns.openActionsMenu(adapterName);
        ConnectBtns.detailsAdapter().click();

        // Validate resulting event
        cy.dataCy('sp-connect-adapter-success-live-preview', {
            timeout: 20000,
        }).should('be.visible');

        cy.get('tr.mat-mdc-row', { timeout: 10000 }).should(
            'have.length',
            amountOfProperties,
        );

        cy.dataCy('live-preview-table-no-data', { timeout: 30000 }).should(
            'not.exist',
        );
    }

    /**
     * Validates the event schema for an adapter by checking the amount of properties
     * and the runtime names of the event properties
     * @param runtimeNames runtime names of the event properties
     * @param adapterName name of the adapter
     */
    public static validateEventSchema(
        adapterName: string,
        runtimeNames: string[],
    ) {
        ConnectUtils.goToConnect();
        GeneralUtils.openMenuForRow(adapterName);
        ConnectBtns.detailsAdapter().click();

        cy.get('tr.mat-mdc-row').should('have.length', runtimeNames.length);

        runtimeNames.forEach(name => {
            cy.get('td.mat-column-runtimeName').contains(name).should('exist');
        });
    }

    public static tearDownPreprocessingRuleTest(
        adapterConfiguration: AdapterInput,
        expectedFile: string,
        ignoreTime: boolean,
        waitTime = 1000,
    ) {
        ConnectUtils.startAdapter(adapterConfiguration, true);

        // Wait till data is stored
        cy.wait(waitTime);

        DataExplorerUtils.checkResults(
            'Adapter to test rules',
            expectedFile,
            ignoreTime,
        );
    }

    public static allAdapterActionsDialog() {
        // Click next
        cy.dataCy('all-adapters-action-next-button').click();
        // Wait for the adapters to start/stop
        cy.wait(2000);
        // Close dialog
        cy.dataCy('all-adapters-action-next-button').click();
    }

    public static validateAdapterIsRunning() {
        ConnectUtils.goToConnect();
        ConnectBtns.startAdapter().should('have.length', 0);
        ConnectBtns.stopAdapter().should('have.length', 1);
    }

    public static validateAdapterIsStopped() {
        ConnectUtils.goToConnect();
        ConnectBtns.startAdapter().should('have.length', 1);
        ConnectBtns.stopAdapter().should('have.length', 0);
    }

    public static checkAmountOfAdapters(amount: number) {
        ConnectUtils.goToConnect();
        if (amount === 0) {
            // The wait is needed because the default value is the no-table-entries element.
            // It must be waited till the data is loaded. Once a better solution is found, this can be removed.
            cy.wait(1000);
            cy.dataCy('no-table-entries').should('be.visible');
        } else {
            ConnectBtns.moreOptions().should('have.length', amount);
        }
    }
}
