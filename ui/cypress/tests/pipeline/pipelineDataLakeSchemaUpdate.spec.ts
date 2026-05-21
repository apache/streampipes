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

import { PipelineUtils } from '../../support/utils/pipeline/PipelineUtils';
import { PipelineBtns } from '../../support/utils/pipeline/PipelineBtns';
import { ChartUtils } from '../../support/utils/chart/ChartUtils';
import { ChartBtns } from '../../support/utils/chart/ChartBtns';
import { PipelineBuilder } from '../../support/builder/PipelineBuilder';
import { PipelineElementBuilder } from '../../support/builder/PipelineElementBuilder';
import { PipelineElementInput } from '../../support/model/PipelineElementInput';
import { CompactAdapterUtils } from '../../support/utils/connect/CompactAdapterUtils';

describe('Test pipeline updates with data lake schema changes', () => {
    const dataStreamSelector = 'test';
    const chartName = 'Chart Density';
    const pipelineName = 'Pipeline Test';
    const dataLakeMeasurement = 'demo';

    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
    });

    it('Test pipeline update with a field datatype change', () => {
        createAdapter();
        addPipelineWithProcessor(
            transformToBooleanProcessor('sensor_fault_flags'),
        );

        editPipelineProcessor();
        PipelineBtns.mappingCheckbox('density').click();
        PipelineBtns.saveElementConfigBtn().click();
        PipelineUtils.pipelineElementUpdateCompleted();
        PipelineBtns.settingsPipelineElementBtn().eq(1).click();
        PipelineBtns.saveElementConfigBtn().click();
        savePipeline();

        PipelineBtns.pipelineEditWarning().contains(dataLakeMeasurement);
        PipelineBtns.pipelineMeasurementEditWarning().contains(
            'density (http://www.w3.org/2001/XMLSchema#float -> http://www.w3.org/2001/XMLSchema#boolean)',
        );
        PipelineBtns.updateAndMigratePipeline().should('be.disabled');
    });

    it('Test pipeline update with a chart field deletion', () => {
        createAdapter();
        addPipelineWithProcessor(
            fieldMapperProcessor('sensorId', 'sensorIdHash'),
        );
        addTableChart(dataLakeMeasurement);

        PipelineUtils.goToPipelines();
        editPipelineProcessor();
        PipelineBtns.mappingCheckbox('sensorId').click();
        PipelineBtns.mappingCheckbox('density').click();
        PipelineBtns.staticPropertyInput('fieldName')
            .clear()
            .type('densityHash')
            .blur();
        PipelineBtns.saveElementConfigBtn().click();
        PipelineUtils.pipelineElementUpdateCompleted();
        savePipeline();

        PipelineBtns.pipelineEditWarning().contains(dataLakeMeasurement);
        PipelineBtns.pipelineChartEditWarning().contains(chartName);
        PipelineBtns.pipelineChartEditWarning().contains('density');
        PipelineBtns.updateAndMigratePipeline().should('not.be.disabled');
        PipelineBtns.updateAndMigratePipeline().click();

        PipelineBtns.pipelineStartedSuccess().should('be.visible');
        PipelineBtns.navigateToPipelineOverview().click();

        ChartUtils.goToDatalake();
        ChartBtns.chartSyncProblemIcon().should('be.visible');
    });

    it('Test pipeline update with an added field', () => {
        createAdapter();
        addPipelineWithProcessor(javaScriptEvalProcessor());
        addTableChart(dataLakeMeasurement);

        PipelineUtils.goToPipelines();
        editPipelineProcessor();
        PipelineBtns.outputAddField().click();
        PipelineBtns.outputRuntimeName().last().type('pipeline_added');
        PipelineBtns.outputRuntimeType()
            .last()
            .click()
            .get('mat-option')
            .contains('Float')
            .click();
        PipelineBtns.saveElementConfigBtn().click();
        PipelineUtils.pipelineElementUpdateCompleted();
        savePipeline();

        PipelineBtns.pipelineEditWarning().should('not.exist');
        PipelineBtns.pipelineStartedSuccess().should('be.visible');
        PipelineBtns.navigateToPipelineOverview().click();

        ChartUtils.goToDatalake();
        ChartBtns.chartSyncProblemIcon().should('not.exist');
    });

    function addPipelineWithProcessor(processingElement: PipelineElementInput) {
        const pipelineInput = PipelineBuilder.create(pipelineName)
            .addSource(dataStreamSelector)
            .addProcessingElement(processingElement)
            .addSink(
                PipelineElementBuilder.create('data_lake')
                    .addInput('input', 'db_measurement', dataLakeMeasurement)
                    .build(),
            )
            .build();

        PipelineUtils.addPipeline(pipelineInput);
    }

    function transformToBooleanProcessor(fieldName: string) {
        return PipelineElementBuilder.create('transform_to_boolean')
            .addInput('checkbox', fieldName, '')
            .build();
    }

    function fieldMapperProcessor(fieldName: string, newFieldName: string) {
        return PipelineElementBuilder.create('field_mapper')
            .addInput('checkbox', fieldName, '')
            .addInput('input', 'fieldName', newFieldName)
            .build();
    }

    function javaScriptEvalProcessor() {
        const processor = PipelineElementBuilder.create('javascript_eval')
            .addInput(
                'code-input',
                'jsFunction',
                'function process(event) {\n  return event;\n}',
            )
            .build();
        processor.output = {
            type: 'append',
            config: [],
        };
        return processor;
    }

    function editPipelineProcessor() {
        PipelineUtils.editPipeline(pipelineName);
        PipelineBtns.settingsPipelineElementBtn().first().click();
    }

    function savePipeline() {
        PipelineBtns.savePipelineBtn().click();
        PipelineBtns.navigateToOverviewCheckbox().children().click();
        PipelineBtns.editorApplyBtn().click();
    }

    function addTableChart(measurementName: string) {
        ChartUtils.addDataViewAndTableWidget(chartName, measurementName, true);
        ChartUtils.saveDataViewConfiguration();
        ChartUtils.checkAmount(1);
    }

    function createAdapter() {
        const compactAdapter = CompactAdapterUtils.getMachineDataSimulator()
            .withTimestampProperty('timestamp')
            .setStart()
            .build();
        CompactAdapterUtils.storeCompactAdapter(compactAdapter);
    }
});
