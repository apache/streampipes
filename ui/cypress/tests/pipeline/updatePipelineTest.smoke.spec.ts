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

import { ConnectUtils } from '../../support/utils/connect/ConnectUtils';
import { PipelineUtils } from '../../support/utils/PipelineUtils';
import { PipelineElementBuilder } from '../../support/builder/PipelineElementBuilder';
import { PipelineBuilder } from '../../support/builder/PipelineBuilder';

const adapterName = 'simulator';

describe('Test update of running pipeline', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
        ConnectUtils.addMachineDataSimulator(adapterName);
    });

    it('Perform Test', () => {
        const pipelineInput = PipelineBuilder.create('Pipeline Test')
            .addSource(adapterName)
            .addProcessingElement(
                PipelineElementBuilder.create('field_renamer')
                    .addInput('drop-down', 'convert-property', 'timestamp')
                    .addInput('input', 'field-name', 't')
                    .build(),
            )
            .addSink(
                PipelineElementBuilder.create('data_lake')
                    .addInput('input', 'db_measurement', 'demo')
                    .build(),
            )
            .build();

        PipelineUtils.addPipeline(pipelineInput);
        PipelineUtils.editPipeline();
        cy.wait(1000);
        PipelineUtils.startPipeline();
        cy.dataCy('modify-pipeline-btn', { timeout: 10000 }).should(
            'have.length',
            1,
        );

        PipelineUtils.editPipeline();
        cy.wait(1000);
        cy.dataCy('sp-editor-save-pipeline').click();
        PipelineUtils.clonePipeline('Pipeline Test 2');
        PipelineUtils.finalizePipelineStart();
        cy.dataCy('modify-pipeline-btn', { timeout: 10000 }).should(
            'have.length',
            2,
        );
    });
});
