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

import { StaticPropertyUtils } from '../StaticPropertyUtils';
import { AdapterInput } from '../../model/AdapterInput';
import { ConnectEventSchemaUtils } from '../ConnectEventSchemaUtils';
import { DataLakeUtils } from '../datalake/DataLakeUtils';
import { ConnectBtns } from './ConnectBtns';
import { AdapterBuilder } from '../../builder/AdapterBuilder';

export class ConnectUtils {
    public static testAdapter(adapterConfiguration: AdapterInput) {
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

        ConnectUtils.startStreamAdapter(adapterConfiguration);
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

        ConnectUtils.startStreamAdapter(configuration);
    }

    public static goToConnect() {
        cy.visit('#/connect');
    }

    public static goToNewAdapterPage() {
        cy.dataCy('connect-create-new-adapter-button').click();
    }

    public static selectAdapter(name) {
        // Select adapter
        cy.get('#' + name).click();
    }

    public static configureAdapter(adapterInput: AdapterInput) {
        cy.wait(2000);
        StaticPropertyUtils.input(adapterInput.adapterConfiguration);

        this.configureFormat(adapterInput);

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

    public static startStreamAdapter(adapterInput: AdapterInput) {
        ConnectUtils.startAdapter(adapterInput);
    }

    public static startAdapter(
        adapterInput: AdapterInput,
        noLiveDataView = false,
    ) {
        // Set adapter name
        cy.dataCy('sp-adapter-name').type(adapterInput.adapterName);

        if (adapterInput.storeInDataLake) {
            cy.dataCy('sp-store-in-datalake').children().click();
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

        if (adapterInput.startAdapter && !noLiveDataView) {
            cy.dataCy('sp-connect-adapter-success-live-preview', {
                timeout: 60000,
            }).should('be.visible');
        } else {
            cy.dataCy('sp-connect-adapter-success-added', {
                timeout: 60000,
            }).should('be.visible');
        }

        this.closeAdapterPreview();
    }

    // Close adapter preview
    public static closeAdapterPreview() {
        cy.get('button').contains('Close').parent().click();
    }

    public static deleteAdapter() {
        // Delete adapter
        cy.visit('#/connect');

        cy.dataCy('delete-adapter').should('have.length', 1);
        cy.dataCy('delete-adapter').click();
        cy.dataCy('delete-adapter-confirmation').click();
        cy.dataCy('adapter-deletion-in-progress', { timeout: 10000 }).should(
            'be.visible',
        );
        cy.dataCy('delete-adapter', { timeout: 20000 }).should(
            'have.length',
            0,
        );
    }

    public static setUpPreprocessingRuleTest(): AdapterInput {
        const adapterConfiguration = AdapterBuilder.create('File_Stream')
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
            .addFormatInput('checkbox', ConnectBtns.csvHeader(), 'check')
            .build();

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

        // View data
        ConnectBtns.infoAdapter().click();
        cy.get('div').contains('Values').parent().click();

        // Validate resulting event
        cy.dataCy('sp-connect-adapter-success-live-preview', {
            timeout: 10000,
        }).should('be.visible');

        // validate that three event properties
        cy.get('.preview-row', { timeout: 10000 })
            .its('length')
            .should('eq', amountOfProperties);
    }

    public static tearDownPreprocessingRuleTest(
        adapterConfiguration: AdapterInput,
        expectedFile: string,
        ignoreTime: boolean,
        waitTime = 0,
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
