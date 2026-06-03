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
import { PipelineUtils } from '../../support/utils/pipeline/PipelineUtils';
import { PipelineElementBuilder } from '../../support/builder/PipelineElementBuilder';
import { PipelineBuilder } from '../../support/builder/PipelineBuilder';
import { AdapterBuilder } from '../../support/builder/AdapterBuilder';
import { ChartUtils } from '../../support/utils/chart/ChartUtils';
import { GeneralUtils } from '../../support/utils/GeneralUtils';
import { PipelineBtns } from '../../support/utils/pipeline/PipelineBtns';
import { SharedUtils } from '../../support/utils/shared/SharedUtils';
import { SharedBtns } from '../../support/utils/shared/SharedBtns';

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
        // Edit Adapter and select pressure
        ConnectUtils.goToConnect();
        ConnectBtns.openActionsMenu('simulator');
        ConnectBtns.editAdapter().should('not.be.disabled');
        ConnectBtns.editAdapter().click();
        const newUserConfiguration = AdapterBuilder.create(
            'Machine_Data_Simulator',
        )
            .addInput('input', 'wait-time-ms', '2000')
            .addInput('radio', 'selected', 'simulator-option-pressure')
            .build();

        ConnectUtils.configureAdapter(newUserConfiguration);
        SharedUtils.confirmDialogVisible();
        SharedBtns.confirmDialogConfirmBtn().click();

        ConnectBtns.getNewSampleBtn().click();
        ConnectUtils.finishEventSchemaConfiguration();
        SharedUtils.confirmDialogVisible();
        SharedBtns.confirmDialogConfirmBtn().click();
        cy.wait(1000);
        ConnectBtns.refreshSchemaBtn().click();
        ConnectUtils.finishConfigureFieldsConfiguration();
        // Update event schema
        ConnectBtns.storeEditAdapter().click();

        // Check for warning message
        ConnectBtns.adapterEditWarning().should('be.visible');
        ConnectBtns.updateAndMigratePipelines().click();
        ConnectUtils.closeAdapterPreview();
        cy.wait(2000);

        // Go to pipelines, check for warning icon and edit pipeline
        PipelineUtils.goToPipelines();

        PipelineBtns.pipelineWarningIcon().should('be.visible');
        PipelineBtns.pipelineSyncProblemIcon().should('be.visible');

        GeneralUtils.openMenuForRow('Pipeline Test');
        PipelineBtns.modifyPipeline().click();
        PipelineBtns.settingsPipelineElementBtn().eq(0).click();
        cy.dataCy('number-mapping', { timeout: 10000 })
            .contains('pressure')
            .click({ force: true });
        PipelineBtns.saveElementConfigBtn().click({ force: true });
        PipelineBtns.savePipelineBtn().click();
        PipelineBtns.navigateToOverviewCheckbox().children().click();
        PipelineBtns.editorApplyBtn().click();
        PipelineBtns.navigateToPipelineOverview().click();

        // Visit dashboard
        cy.wait(5000);
        ChartUtils.goToDatalake();
        ChartUtils.createAndEditDataView();

        cy.dataCy('data-explorer-field-selection-panel').should(
            'include.text',
            'pressure',
        );
        cy.dataCy('data-explorer-field-selection-panel').should(
            'include.text',
            'sensorId',
        );
    });
});
