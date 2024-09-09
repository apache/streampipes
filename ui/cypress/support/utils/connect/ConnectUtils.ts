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
import { DataLakeUtils } from '../datalake/DataLakeUtils';
import { ConnectBtns } from './ConnectBtns';
import { AdapterBuilder } from '../../builder/AdapterBuilder';
import { UserUtils } from '../UserUtils';
import { PipelineUtils } from '../PipelineUtils';

export class ConnectUtils {
    public static testAdapter(
        adapterConfiguration: AdapterInput,
        adapterStartFails = false,
    ) {
        ConnectUtils.goToConnect();

        ConnectUtils.goToNewAdapterPage();

        ConnectUtils.selectAdapter(adapterConfiguration.adapterType);

        ConnectUtils.configureAdapter(adapterConfiguration);

        if (adapterConfiguration.timestampProperty) {
            ConnectEventSchemaUtils.markPropertyAsTimestamp(
                adapterConfiguration.timestampProperty,
            );
        }

        if (adapterConfiguration.autoAddTimestamp) {
            ConnectEventSchemaUtils.addTimestampProperty();
        }

        ConnectEventSchemaUtils.finishEventSchemaConfiguration();

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

        if (adapterConfiguration.dimensionProperties.length > 0) {
            adapterConfiguration.dimensionProperties.forEach(
                dimensionPropertyName => {
                    ConnectEventSchemaUtils.markPropertyAsDimension(
                        dimensionPropertyName,
                    );
                },
            );
        }

        ConnectEventSchemaUtils.markPropertyAsTimestamp(
            adapterConfiguration.timestampProperty,
        );

        ConnectEventSchemaUtils.finishEventSchemaConfiguration();
    }

    public static addMachineDataSimulator(
        name: string,
        persist: boolean = false,
    ) {
        const builder = AdapterBuilder.create('Machine_Data_Simulator')
            .setName(name)
            .addInput('input', 'wait-time-ms', '1000');

        if (persist) {
            builder.setTimestampProperty('timestamp').setStoreInDataLake();
        }

        const configuration = builder.build();

        ConnectUtils.goToConnect();

        ConnectUtils.goToNewAdapterPage();

        ConnectUtils.selectAdapter(configuration.adapterType);

        ConnectUtils.configureAdapter(configuration);

        ConnectEventSchemaUtils.finishEventSchemaConfiguration();

        ConnectUtils.startAdapter(configuration);
    }

    public static goToConnect() {
        cy.visit('#/connect');
    }

    public static goToNewAdapterPage() {
        cy.dataCy('connect-create-new-adapter-button').click();
    }

    public static selectAdapter(name: string) {
        // Select adapter
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
        // Next Button should not be disabled
        cy.get('button').contains('Next').parent().should('not.be.disabled');

        // Click next
        cy.get('button').contains('Next').parent().click();
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
        // Click next
        cy.dataCy('sp-connect-schema-editor', { timeout: 10000 }).should(
            'be.visible',
        );
        cy.get('#event-schema-next-button').click();
    }

    public static startAdapter(
        adapterInput: AdapterInput,
        noLiveDataView = false,
        adapterStartFails = false,
    ) {
        // Set adapter name
        cy.dataCy('sp-adapter-name').type(adapterInput.adapterName);

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
            ConnectBtns.startAdapterNowCheckbox().parent().click();
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

    // Close adapter preview
    public static closeAdapterPreview() {
        cy.get('button').contains('Close').parent().click();
    }

    public static deleteAdapter() {
        // Delete adapter
        this.goToConnect();

        cy.dataCy('delete-adapter').should('have.length', 1);
        this.clickDelete();
        cy.dataCy('adapter-deletion-in-progress', { timeout: 10000 }).should(
            'be.visible',
        );
        cy.dataCy('delete-adapter', { timeout: 20000 }).should(
            'have.length',
            0,
        );
    }

    public static storeAndStartEditedAdapter() {
        ConnectUtils.finishEventSchemaConfiguration();
        ConnectBtns.storeEditAdapter().click();
        ConnectBtns.updateAndMigratePipelines().click();
        ConnectUtils.closeAdapterPreview();
        ConnectBtns.startAdapter().click();
    }

    public static deleteAdapterAndAssociatedPipelines(switchUserCheck = false) {
        // Delete adapter and associated pipelines
        this.goToConnect();
        cy.dataCy('delete-adapter').should('have.length', 1);
        this.clickDelete();
        cy.dataCy('delete-adapter-and-associated-pipelines-confirmation', {
            timeout: 10000,
        }).should('be.visible');
        cy.dataCy(
            'delete-adapter-and-associated-pipelines-confirmation',
        ).click();
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

    // NOTE: this function will leave the adapter and associated pipelines running,
    // please make sure to clean up after calling this function
    public static deleteAdapterAndAssociatedPipelinesPermissionDenied() {
        // Associated pipelines not owned by the user (unless admin) should not be deleted during adapter deletion
        this.goToConnect();
        cy.dataCy('delete-adapter').should('have.length', 1);
        this.clickDelete();
        cy.dataCy('delete-adapter-and-associated-pipelines-confirmation', {
            timeout: 10000,
        }).should('be.visible');
        cy.dataCy(
            'delete-adapter-and-associated-pipelines-confirmation',
        ).click();
        cy.dataCy('adapter-deletion-permission-denied', {
            timeout: 10000,
        }).should('be.visible');
        cy.get('.sp-dialog-actions').click();
        this.checkAdapterNotDeleted();
    }

    public static clickDelete() {
        cy.dataCy('delete-adapter').click();
        cy.dataCy('delete-adapter-confirmation').click();
    }

    public static checkAdapterNotDeleted() {
        this.goToConnect();
        cy.dataCy('delete-adapter', { timeout: 20000 }).should(
            'have.length',
            1,
        );
    }

    public static checkAdapterAndAssociatedPipelinesDeleted() {
        this.goToConnect();
        cy.dataCy('delete-adapter', { timeout: 20000 }).should(
            'have.length',
            0,
        );
        PipelineUtils.goToPipelines();
        cy.dataCy('delete-pipeline', { timeout: 10000 }).should(
            'have.length',
            0,
        );
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

        // wait till schema is shown
        cy.dataCy('sp-connect-schema-editor', { timeout: 60000 }).should(
            'be.visible',
        );

        return adapterConfiguration;
    }

    public static startAndValidateAdapter(amountOfProperties: number) {
        ConnectBtns.startAdapter().should('not.be.disabled');

        ConnectBtns.startAdapter().click();

        ConnectUtils.validateEventsInPreview(amountOfProperties);
    }

    public static validateEventsInPreview(amountOfProperties: number) {
        // View data
        ConnectBtns.detailsAdapter().click();

        // Validate resulting event
        cy.dataCy('sp-connect-adapter-success-live-preview', {
            timeout: 20000,
        }).should('be.visible');

        cy.get('tr.mat-mdc-row', { timeout: 10000 }).should(
            'have.length',
            amountOfProperties,
        );

        cy.wait(1000);

        cy.dataCy('live-preview-table-value')
            .invoke('text')
            .then(text => expect(text).not.to.include('no data'));
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

        DataLakeUtils.checkResults(
            'Adapter to test rules',
            expectedFile,
            ignoreTime,
        );
    }

    public static allAdapterActionsDialog() {
        // Click next
        cy.get('button').contains('Next').parent().click();
        // Wait for the adapters to start/stop
        cy.wait(2000);
        // Close dialog
        cy.get('button').contains('Close').parent().click();
    }
}
