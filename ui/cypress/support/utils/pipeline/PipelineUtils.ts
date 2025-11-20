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

import { PipelineInput } from '../../model/PipelineInput';
import { StaticPropertyUtils } from '../userInput/StaticPropertyUtils';
import { OutputStrategyUtils } from '../OutputStrategyUtils';
import { PipelineElementInput } from '../../model/PipelineElementInput';
import { PipelineBtns } from './PipelineBtns';
import { ConnectUtils } from '../connect/ConnectUtils';
import { PipelineBuilder } from '../../builder/PipelineBuilder';
import { PipelineElementBuilder } from '../../builder/PipelineElementBuilder';
import { GeneralUtils } from '../GeneralUtils';

export class PipelineUtils {
    public static addPipeline(pipelineInput: PipelineInput) {
        PipelineUtils.goToPipelineEditor();

        PipelineUtils.selectDataStream(pipelineInput);

        PipelineUtils.configurePipeline(pipelineInput);

        PipelineUtils.startPipeline(pipelineInput);
    }

    public static addPipelineWithAssetLinks(
        pipelineInput: PipelineInput,
        assetNameList: String[],
    ) {
        PipelineUtils.goToPipelineEditor();

        PipelineUtils.selectDataStream(pipelineInput);

        PipelineUtils.configurePipeline(pipelineInput);

        PipelineUtils.startPipelineWithAssetLinkage(
            pipelineInput,
            assetNameList,
        );
    }

    /**
     * This method adds a sample adapter and pipeline
     */
    public static addSampleAdapterAndPipeline() {
        const adapterName = 'simulator';

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
    }

    public static editPipeline(pipelineName: string) {
        GeneralUtils.openMenuForRow(pipelineName);
        PipelineBtns.modifyPipeline().first().click();
    }

    public static goToPipelines() {
        cy.visit('#/pipelines');
    }

    public static goToPipelineEditor() {
        // Go to StreamPipes editor
        this.goToPipelines();
        PipelineBtns.pipelinesToEditor().click();
    }

    public static checkDataStreamExists(dataSourceName: string) {
        PipelineBtns.spPipelineElementSelection().should('be.visible');
        PipelineBtns.editorAddPipelineElement().click();
        cy.dataCy(dataSourceName).should('exist');
        cy.dataCy('cancel-pipeline-element-discovery').click();
    }

    public static selectDataStream(pipelineInput: PipelineInput) {
        // Select a stream
        PipelineBtns.spPipelineElementSelection().should('be.visible');
        PipelineBtns.editorAddPipelineElement().click();
        cy.dataCy(pipelineInput.dataSource, { timeout: 10000 }).click();
    }

    public static openPossibleElementsMenu(dataSourceName: string) {
        PipelineBtns.possibleElementsBtns(dataSourceName).click();
    }

    public static selectCompatibleElement(elementName: string) {
        PipelineBtns.selectCompatibleElementBtn(elementName).click();
    }

    public static configureProcessingElement(
        processingElement: PipelineElementInput,
    ) {
        this.selectCompatibleElement(processingElement.name);
        StaticPropertyUtils.input(processingElement.config);
        OutputStrategyUtils.input(processingElement.output);
    }

    private static savePipelineElementConfiguration() {
        PipelineBtns.saveElementConfigBtn().click();
    }

    private static configurePipeline(pipelineInput: PipelineInput) {
        this.openPossibleElementsMenu(pipelineInput.dataSource);

        // Select processor
        if (pipelineInput.processingElement) {
            this.configureProcessingElement(pipelineInput.processingElement);

            this.savePipelineElementConfiguration();

            // Select sink
            PipelineBtns.possibleElementsBtns(
                pipelineInput.processingElement.name,
            ).click();
        }

        // Configure sink
        PipelineBtns.selectCompatibleElementBtn(
            pipelineInput.dataSink.name,
        ).click();
        StaticPropertyUtils.input(pipelineInput.dataSink.config);

        // Save sink configuration
        PipelineBtns.saveElementConfigBtn().click();
    }

    public static startPipeline(pipelineInput?: PipelineInput) {
        // Save and start pipeline
        PipelineBtns.savePipelineBtn().click();
        if (pipelineInput) {
            cy.dataCy('sp-editor-pipeline-name').type(
                pipelineInput.pipelineName,
            );
        }
        PipelineUtils.finalizePipelineStart();
    }

    public static startPipelineWithAssetLinkage(
        pipelineInput?: PipelineInput,
        assetNameList?: String[],
    ) {
        // Save and start pipeline
        PipelineBtns.savePipelineBtn().click();
        if (pipelineInput) {
            cy.dataCy('sp-editor-pipeline-name').type(
                pipelineInput.pipelineName,
            );
        }
        PipelineUtils.finalizePipelineStart(assetNameList);
    }

    private static addToAsset(assetNameList) {
        cy.dataCy('sp-show-pipeline-asset-checkbox')
            .find('input[type="checkbox"]')
            .then($checkbox => {
                if (!$checkbox.prop('checked')) {
                    cy.wrap($checkbox).click();
                }
            });

        cy.get('mat-tree.asset-tree', { timeout: 10000 }).should('exist');
        assetNameList.forEach(assetName => {
            console.log(assetName);
            cy.get('mat-tree.asset-tree')
                .find('.mat-tree-node')
                .contains(assetName)
                .click();
        });
    }

    public static clonePipeline(newPipelineName: string) {
        PipelineBtns.pipelineCloneModeBtn().children().click();
        cy.dataCy('sp-editor-pipeline-name').type(newPipelineName);
    }

    public static updatePipeline(newPipelineName: string) {
        //PipelineBtns.pipelineCloneModeBtn().children().click();
        cy.dataCy('sp-editor-pipeline-name').type(newPipelineName);
    }

    public static finalizePipelineStart(assetNameList?: String[]) {
        PipelineBtns.navigateToOverviewCheckbox().children().click();
        if (assetNameList) {
            PipelineUtils.addToAsset(assetNameList);
        }
        PipelineBtns.editorApplyBtn().click();

        cy.dataCy('sp-pipeline-started-success', { timeout: 15000 }).should(
            'be.visible',
        );
        PipelineBtns.navigateToPipelineOverview().click();
    }

    public static checkAmountOfPipelinesPipeline(amount: number) {
        PipelineUtils.goToPipelines();

        if (amount === 0) {
            // The wait is needed because the default value is the no-table-entries element.
            // It must be waited till the data is loaded. Once a better solution is found, this can be removed.
            cy.wait(1000);
            cy.dataCy('no-table-entries').should('have.length', 2);
        } else {
            PipelineBtns.statusPipeline().should('have.length', amount);
        }
    }

    public static deletePipeline(pipelineName: string) {
        // Delete pipeline
        PipelineUtils.goToPipelines();
        GeneralUtils.openMenuForRow(pipelineName);
        PipelineBtns.deletePipeline().should('have.length', 1);
        PipelineBtns.deletePipeline().click({ force: true });

        cy.dataCy('sp-pipeline-stop-and-delete').click();
        cy.wait(2000);

        PipelineBtns.deletePipeline().should('have.length', 0);
    }

    public static verifyPipelineName(expectedName: string) {
        cy.dataCy('all-pipelines-table', { timeout: 10000 })
            .first()
            .within(() => {
                cy.get('td').eq(2).should('contain', expectedName);
            });
    }

    public static verifyPipelineCount(expectedCount: number) {
        cy.dataCy('all-pipelines-table', { timeout: 10000 })
            .find('tr')
            .should('have.length', expectedCount + 1);
    }
}
