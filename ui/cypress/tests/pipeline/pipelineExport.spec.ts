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

describe('Test Export and Import Functionality of Pipelines', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
        ConnectUtils.addMachineDataSimulator(adapterName);
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
    });

    it('Perform Test', () => {
        PipelineUtils.goToPipelines();

        // Check if pipeline exists
        cy.dataCy('all-pipelines-table').should('have.length', 1);

        // Export Pipeline
        cy.dataCy('export-pipelines-btn').click();

        // Delete created pipeline
        PipelineUtils.deletePipeline();
        cy.dataCy('all-pipelines-table').should('have.length', 0);

        // Import pipeline
        cy.dataCy('import-pipelines-btn').click();
        cy.get('input[type="file"]').selectFile(
            'cypress/downloads/pipelines.json',
            { force: true },
        );
        // Select pipeline checkbox
        cy.get('[type="checkbox"]').check();
        cy.dataCy('import-selected-pipelines-btn').click();

        // Check if pipeline import was succesful
        cy.dataCy('all-pipelines-table').should('have.length', 1);

        // Start pipeline and check if pipeline has started correctly
        cy.dataCy('start-pipeline-button').click();
        cy.get('[class="success-message"]').should('be.visible');
        cy.dataCy('sp-pipeline-dialog-close').click();
    });
});
