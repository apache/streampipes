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

export class PipelineBtns {
    public static statusPipeline() {
        return cy.dataCy('status-pipeline-green', { timeout: 10000 });
    }

    public static stopPipeline() {
        return cy.dataCy('stop-pipeline-button', { timeout: 10000 });
    }

    public static deletePipeline() {
        return cy.dataCy('delete-pipeline', { timeout: 10000 });
    }
    public static modifyPipeline() {
        return cy.dataCy('modify-pipeline-btn');
    }

    public static pipelinesToEditor() {
        return cy.dataCy('pipelines-navigate-to-editor');
    }

    public static spPipelineElementSelection() {
        return cy.dataCy('sp-pipeline-element-selection', { timeout: 10000 });
    }

    public static editorAddPipelineElement() {
        return cy.dataCy('sp-editor-add-pipeline-element', { timeout: 10000 });
    }

    public static possibleElementsBtns(dataSourceName) {
        return cy.dataCy('sp-possible-elements-' + dataSourceName, {
            timeout: 10000,
        });
    }

    public static selectCompatibleElementBtn(elementName) {
        return cy.dataCy('sp-compatible-elements-' + elementName);
    }

    public static saveElementConfigBtn() {
        return cy.dataCy('sp-element-configuration-save');
    }
    public static savePipelineBtn() {
        return cy.dataCy('sp-editor-save-pipeline');
    }

    public static pipelineCloneModeBtn() {
        return cy.dataCy('pipeline-update-mode-clone');
    }

    public static navigateToOverviewCheckbox() {
        return cy.dataCy('sp-editor-checkbox-navigate-to-overview');
    }

    public static editorApplyBtn() {
        return cy.dataCy('sp-editor-apply');
    }

    public static navigateToPipelineOverview() {
        return cy.dataCy('sp-navigate-to-pipeline-overview', {
            timeout: 15000,
        });
    }

    public static settingsPipelineElementBtn() {
        return cy.dataCy('settings-pipeline-element-button');
    }
    public static pipelineEditorSave() {
        return cy.dataCy('sp-editor-save-pipeline');
    }

    public static pipelineAssetCheckbox() {
        return cy.dataCy('sp-show-pipeline-asset-checkbox');
    }

    public static pipelineEditorCancel() {
        return cy.dataCy('sp-editor-cancel');
    }

    public static pipelineIconStandRow() {
        return cy.dataCy('pipeline-element-icon-stand-row');
    }

    public static pipelineHelpBtn() {
        return cy.dataCy('help-button-icon-stand');
    }
}
