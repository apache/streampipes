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
import { ConnectBtns } from '../../support/utils/connect/ConnectBtns';
import { SharedBtns } from '../../support/utils/shared/SharedBtns';
import { SharedUtils } from '../../support/utils/shared/SharedUtils';

describe('Upload sample event during schema configuration', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();

        const adapterConfiguration =
            buildMachineDataSimulator('Upload Sample Test');
        setupAdapter(adapterConfiguration);
    });

    it('Uses uploaded sample and refreshes fields on warning', () => {
        ConnectBtns.configureSchemaNextBtn().click();
        ConnectUtils.eventSchemaWithFieldsShouldBeVisible();
        ConnectBtns.configureFieldsBackBtn().click();

        uploadSample();

        ConnectBtns.configureSchemaEventPreviewOriginal().should(
            'contain.text',
            '"uploadedSample": true',
        );

        ConnectBtns.configureSchemaNextBtn().click();
        SharedUtils.confirmDialogVisible();
        SharedBtns.confirmDialogConfirmBtn().click();

        ConnectUtils.eventSchemaWithFieldsShouldBeVisible();
        ConnectBtns.configureFieldsEventPreviewResult().should(
            'contain.text',
            'uploadedSample',
        );
    });

    it('Uses uploaded sample with script enabled after fields roundtrip', () => {
        ConnectBtns.scriptActiveToggle().click();

        ConnectBtns.configureSchemaNextBtn().click();
        ConnectUtils.eventSchemaWithFieldsShouldBeVisible();
        ConnectBtns.configureFieldsBackBtn().click();

        uploadSample();

        ConnectBtns.configureSchemaEventPreviewOriginal().should(
            'contain.text',
            'uploadedSample',
        );
        ConnectBtns.configureSchemaEventPreviewResult().should(
            'contain.text',
            'uploadedSample',
        );

        ConnectBtns.configureSchemaNextBtn().click();
        SharedUtils.confirmDialogVisible();
        SharedBtns.confirmDialogConfirmBtn().click();

        ConnectUtils.eventSchemaWithFieldsShouldBeVisible();
        ConnectBtns.configureFieldsEventPreviewResult().should(
            'contain.text',
            'uploadedSample',
        );
    });

    it('Uses uploaded sample with script enabled without fields roundtrip', () => {
        ConnectBtns.scriptActiveToggle().click();

        uploadSample();

        ConnectBtns.configureSchemaEventPreviewOriginal().should(
            'contain.text',
            'uploadedSample',
        );
        ConnectBtns.configureSchemaEventPreviewResult().should(
            'contain.text',
            'uploadedSample',
        );

        ConnectBtns.configureSchemaNextBtn().click();

        ConnectUtils.eventSchemaWithFieldsShouldBeVisible();
        ConnectBtns.configureFieldsEventPreviewResult().should(
            'contain.text',
            'uploadedSample',
        );
    });

    const buildMachineDataSimulator = (name: string) =>
        AdapterBuilder.create('Machine_Data_Simulator')
            .setName(name)
            .setTimestampProperty('timestamp')
            .addInput('input', 'wait-time-ms', '1000')
            .build();

    const setupAdapter = (adapterConfiguration: any) => {
        ConnectUtils.goToConnect();
        ConnectUtils.goToNewAdapterPage();
        ConnectUtils.selectAdapter(adapterConfiguration.adapterType);
        ConnectUtils.configureAdapter(adapterConfiguration);
        ConnectBtns.configureSchemaEventPreviewOriginal().should('be.visible');
    };

    const uploadSample = () => {
        const uploadedSample = JSON.stringify({
            uploadedSample: true,
            temperature: 42,
        });
        ConnectUtils.uploadSampleEvent(uploadedSample);
    };
});
