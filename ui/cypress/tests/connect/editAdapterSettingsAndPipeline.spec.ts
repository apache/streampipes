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

import { ConnectUtils } from '../../support/utils/connect/ConnectUtils';
import { ConnectBtns } from '../../support/utils/connect/ConnectBtns';
import { PipelineUtils } from '../../support/utils/PipelineUtils';
import { PipelineElementBuilder } from '../../support/builder/PipelineElementBuilder';
import { PipelineBuilder } from '../../support/builder/PipelineBuilder';
import { AdapterBuilder } from '../../support/builder/AdapterBuilder';
import { DashboardUtils } from '../../support/utils/DashboardUtils';

describe('Test Edit Adapter and Pipeline', () => {
    beforeEach('Setup Test', () => {
        // To set up test add a stream adapter that can be configured
        cy.initStreamPipesTest();
        ConnectUtils.addMachineDataSimulator('simulator');

        const adapterName = 'simulator';
        const pipelineInput = PipelineBuilder.create('Pipeline Test')
            .addSource(adapterName)
            .addProcessingElement(
                PipelineElementBuilder.create('numerical_filter')
                    .addInput('input', 'value', '10')
                    .build(),
            )
            .addSink(
                PipelineElementBuilder.create('data_lake')
                    .addInput('input', 'db_measurement', 'demo')
                    .build(),
            )
            .build();

        PipelineUtils.addPipeline(pipelineInput);
    });

    it('Edit adapter and test Pipeline behaviour', () => {
        // Edit Apater and select pressure
        ConnectUtils.goToConnect();
        ConnectBtns.editAdapter().should('not.be.disabled');
        ConnectBtns.editAdapter().click();
        const newUserConfiguration = AdapterBuilder.create(
            'Machine_Data_Simulator',
        )
            .addInput('input', 'wait-time-ms', '2000')
            .addInput('radio', 'selected', 'simulator-option-pressure')
            .build();

        ConnectUtils.configureAdapter(newUserConfiguration);

        // Update event schema
        ConnectUtils.finishEventSchemaConfiguration();
        ConnectBtns.storeEditAdapter().click();

        // Check for warning message
        cy.dataCy('sp-connect-adapter-edit-warning', {
            timeout: 60000,
        }).should('be.visible');
        ConnectBtns.updateAndMigratePipelines().click();
        ConnectUtils.closeAdapterPreview();
        cy.wait(2000);

        // Go to pipelines, check for warning icon and edit pipeline
        PipelineUtils.goToPipelines();

        cy.dataCy('pipeline-warning-icon', {
            timeout: 60000,
        }).should('be.visible');

        cy.dataCy('pipeline-sync-problem-icon', {
            timeout: 60000,
        }).should('be.visible');

        cy.dataCy('modify-pipeline-btn').click();
        cy.dataCy('settings-pipeline-element-button').eq(0).click();
        cy.dataCy('number-mapping').contains('pressure').click({ force: true });
        cy.dataCy('sp-element-configuration-save').click({ force: true });
        cy.dataCy('sp-editor-save-pipeline').click();
        cy.dataCy('sp-editor-checkbox-navigate-to-overview').children().click();
        cy.dataCy('sp-editor-apply').click();
        cy.dataCy('sp-navigate-to-pipeline-overview').click();

        // Visit dashboard
        cy.wait(1000);
        DashboardUtils.goToDashboard();

        // Add new dashboard widget and check if edited adapter appears
        const dashboardName = 'testDashboard';
        DashboardUtils.addAndEditDashboard(dashboardName);
        cy.dataCy('dashboard-add-widget').click();
        cy.dataCy('dashboard-visualize-pipeline-Pipeline_Test').click();
        cy.dataCy('dashboard-select-widget-table').click();
        cy.get('[type="checkbox"]').check();
        cy.dataCy('dashboard-new-widget-next-btn').click();
        cy.dataCy('dashboard-save-edit-mode').click();
        cy.get('.main-panel').should('include.text', 'pressure');
        cy.get('.main-panel').should('include.text', 'sensorId');
        cy.get('.main-panel').should('include.text', 'time');
    });
});
