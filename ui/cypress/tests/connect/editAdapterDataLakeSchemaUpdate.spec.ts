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
import { PipelineBtns } from '../../support/utils/pipeline/PipelineBtns';
import { ChartUtils } from '../../support/utils/chart/ChartUtils';
import { ChartBtns } from '../../support/utils/chart/ChartBtns';
import { SharedUtils } from '../../support/utils/shared/SharedUtils';
import { SharedBtns } from '../../support/utils/shared/SharedBtns';
import { CompactAdapterUtils } from '../../support/utils/connect/CompactAdapterUtils';

describe('Test adapter updates with data lake schema changes', () => {
    const adapterName = 'Test';
    const chartName = 'Chart Density';

    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
    });

    it('Test adapter update with a field datatype change', () => {
        createAdapter();

        updateAdapterTransformationScript(
            'event.density = "test";\n out.collect(event);\n',
        );

        ConnectBtns.adapterEditWarning().should('be.visible');
        ConnectBtns.adapterManualPipelineMigrationWarning().should(
            'be.visible',
        );

        ConnectBtns.adapterChartEditWarning().should('not.exist');
        ConnectBtns.updateAndMigratePipelines().click();
        ConnectBtns.connectAdapterAddedSuccessfully().should('be.visible');
        ConnectUtils.closeAdapterPreview();

        PipelineUtils.goToPipelines();
        PipelineBtns.pipelineSyncProblemIcon().should('be.visible');
        PipelineBtns.pipelineWarningIcon().should('be.visible');

        PipelineBtns.pipelineWarningIcon().click();
        PipelineBtns.pipelineNotification().contains(
            'density (http://www.w3.org/2001/XMLSchema#float -> http://www.w3.org/2001/XMLSchema#string)',
        );
        PipelineBtns.acknowledgePipelineNotification().click();

        PipelineBtns.pipelineSyncProblemIcon().click();
        PipelineBtns.pipelineMeasurementUpdateDialog().should('be.visible');
        PipelineBtns.measurementUpdateDialogEditPipelineBtn().click();

        PipelineBtns.savePipelineBtn().click();
        PipelineBtns.editorApplyBtn().click();
        PipelineBtns.pipelineStartedError();
    });

    it('Test adapter update with a chart field deletion', () => {
        createAdapter();
        addTableChart(adapterName);

        updateAdapterTransformationScript(
            'delete event.density;\n out.collect(event);\n',
        );

        ConnectBtns.adapterEditWarning().should('be.visible');
        ConnectBtns.adapterChartEditWarning().contains(chartName);
        ConnectBtns.adapterChartEditWarning().contains('density');
        ConnectBtns.updateAndMigratePipelines().click();
        ConnectBtns.connectAdapterAddedSuccessfully().should('be.visible');
        ConnectUtils.closeAdapterPreview();

        ChartUtils.goToDatalake();
        ChartBtns.chartSyncProblemIcon().should('be.visible');
        ChartBtns.chartSyncProblemIcon().click();

        ChartBtns.chartRequiresAttentionWarning().should('be.visible');
        ChartBtns.chartRequiresAttentionWarning().contains('density');
    });

    it('Test adapter update with an added field', () => {
        createAdapter();
        addTableChart(adapterName);

        updateAdapterTransformationScript(
            'event.adapter_added = 1;\n out.collect(event);\n',
        );

        ConnectBtns.adapterEditWarning().should('not.exist');
        ConnectBtns.adapterManualPipelineMigrationWarning().should('not.exist');
        ConnectBtns.adapterChartEditWarning().should('not.exist');
        ConnectUtils.closeAdapterPreview();

        PipelineUtils.goToPipelines();
        PipelineBtns.pipelineSyncProblemIcon().should('not.exist');
        PipelineBtns.pipelineWarningIcon().should('not.exist');

        ChartUtils.goToDatalake();
        ChartBtns.chartSyncProblemIcon().should('not.exist');
    });

    function updateAdapterTransformationScript(script: string) {
        ConnectUtils.goToConnect();
        ConnectBtns.openActionsMenu(adapterName);
        ConnectBtns.editAdapter().click();

        ConnectBtns.adapterSettingsNextBtn().click();
        ConnectBtns.scriptActiveToggle().click();
        ConnectUtils.replaceAdapterScript(script);
        ConnectBtns.configureSchemaRunScriptBtn().click();

        ConnectBtns.configureSchemaNextBtn().click();
        SharedUtils.confirmDialogVisible();
        SharedBtns.confirmDialogConfirmBtn().click();

        ConnectUtils.finishConfigureFieldsConfiguration();
        ConnectBtns.storeEditAdapter().click();
    }

    function addTableChart(measurementName: string) {
        ChartUtils.addDataViewAndTableWidget(chartName, measurementName, true);
        ChartUtils.saveDataViewConfiguration(false, false);
        ChartUtils.checkAmount(1);
    }

    function createAdapter() {
        const compactAdapter = CompactAdapterUtils.getMachineDataSimulator()
            .withTimestampProperty('timestamp')
            .setStart()
            .setPersist()
            .build();
        CompactAdapterUtils.storeCompactAdapter(compactAdapter);
    }
});
