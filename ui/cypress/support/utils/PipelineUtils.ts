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

import { PipelineInput } from '../model/PipelineInput';
import { StaticPropertyUtils } from './userInput/StaticPropertyUtils';
import { OutputStrategyUtils } from './OutputStrategyUtils';
import { PipelineElementInput } from '../model/PipelineElementInput';

export class PipelineUtils {
    public static addPipeline(pipelineInput: PipelineInput) {
        PipelineUtils.goToPipelineEditor();

        PipelineUtils.selectDataStream(pipelineInput);

        PipelineUtils.configurePipeline(pipelineInput);

        PipelineUtils.startPipeline(pipelineInput);
    }

    public static editPipeline() {
        cy.dataCy('modify-pipeline-btn').first().click();
    }

    public static goToPipelines() {
        cy.visit('#/pipelines');
    }

    public static goToPipelineEditor() {
        // Go to StreamPipes editor
        this.goToPipelines();
        cy.dataCy('pipelines-navigate-to-editor').click();
    }

    private static selectDataStream(pipelineInput: PipelineInput) {
        // Select a stream
        cy.dataCy('sp-pipeline-element-selection', { timeout: 10000 }).should(
            'be.visible',
        );
        cy.dataCy('sp-editor-add-pipeline-element').click();
        cy.dataCy(pipelineInput.dataSource, { timeout: 10000 }).click();
    }

    public static openPossibleElementsMenu(dataSourceName: string) {
        cy.dataCy('sp-possible-elements-' + dataSourceName, {
            timeout: 10000,
        }).click();
    }

    public static selectCompatibleElement(elementName: string) {
        cy.dataCy('sp-compatible-elements-' + elementName).click();
    }

    public static configureProcessingElement(
        processingElement: PipelineElementInput,
    ) {
        this.selectCompatibleElement(processingElement.name);
        StaticPropertyUtils.input(processingElement.config);
        OutputStrategyUtils.input(processingElement.output);
    }

    private static savePipelineElementConfiguration() {
        cy.dataCy('sp-element-configuration-save').click();
    }

    private static configurePipeline(pipelineInput: PipelineInput) {
        this.openPossibleElementsMenu(pipelineInput.dataSource);

        // Select processor
        if (pipelineInput.processingElement) {
            this.configureProcessingElement(pipelineInput.processingElement);

            this.savePipelineElementConfiguration();

            // Select sink
            cy.dataCy(
                'sp-possible-elements-' + pipelineInput.processingElement.name,
                { timeout: 10000 },
            ).click();
        }

        // Configure sink
        cy.dataCy(
            'sp-compatible-elements-' + pipelineInput.dataSink.name,
        ).click();
        StaticPropertyUtils.input(pipelineInput.dataSink.config);

        // Save sink configuration
        cy.dataCy('sp-element-configuration-save').click();
    }

    public static startPipeline(pipelineInput?: PipelineInput) {
        // Save and start pipeline
        cy.dataCy('sp-editor-save-pipeline').click();
        if (pipelineInput) {
            cy.dataCy('sp-editor-pipeline-name').type(
                pipelineInput.pipelineName,
            );
        }
        PipelineUtils.finalizePipelineStart();
    }

    public static clonePipeline(newPipelineName: string) {
        cy.dataCy('pipeline-update-mode-clone').children().click();
        cy.dataCy('sp-editor-pipeline-name').type(newPipelineName);
    }

    public static finalizePipelineStart() {
        cy.dataCy('sp-editor-checkbox-navigate-to-overview').children().click();
        cy.dataCy('sp-editor-apply').click();
        cy.dataCy('sp-pipeline-started-success', { timeout: 15000 }).should(
            'be.visible',
        );
        cy.dataCy('sp-navigate-to-pipeline-overview', {
            timeout: 15000,
        }).click();
    }

    public static checkAmountOfPipelinesPipeline(amount: number) {
        cy.visit('#/pipelines');
        cy.dataCy('delete-pipeline').should('have.length', amount);
    }

    public static deletePipeline() {
        // Delete pipeline
        cy.visit('#/pipelines');
        cy.dataCy('delete-pipeline').should('have.length', 1);
        cy.dataCy('delete-pipeline').click({ force: true });

        cy.dataCy('sp-pipeline-stop-and-delete').click();

        cy.dataCy('delete-pipeline', { timeout: 10000 }).should(
            'have.length',
            0,
        );
    }
}
