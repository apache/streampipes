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
import { GeneralUtils } from '../GeneralUtils';

export class ConnectBtns {
    public static detailsAdapter() {
        return cy.dataCy('details-adapter', { timeout: 10000 });
    }

    public static deleteAdapter() {
        return cy.dataCy('delete-adapter', { timeout: 20000 });
    }

    public static moreOptions() {
        return cy.dataCy('more-options', { timeout: 10000 });
    }

    public static editAdapter() {
        return cy.dataCy('edit-adapter', { timeout: 10000 });
    }

    public static stopAdapter() {
        return cy.dataCy('stop-adapter');
    }

    public static startAdapter() {
        return cy.dataCy('start-adapter');
    }

    public static adapterNameInput() {
        return cy.dataCy('sp-adapter-name');
    }

    public static adapterOperationInProgressSpinner() {
        return cy.dataCy('adapter-operation-in-progress-spinner', {
            timeout: 10000,
        });
    }

    public static openActionsMenu(adapterName: string) {
        GeneralUtils.openMenuForRow(adapterName);
    }

    public static refreshSchema() {
        return cy.dataCy('refresh-schema');
    }

    public static storeEditAdapter() {
        return cy.dataCy('store-edit-adapter');
    }

    public static changeRuntimeType() {
        return cy.dataCy('connect-change-runtime-type', { timeout: 10000 });
    }

    public static updateAndMigratePipelines() {
        return cy.dataCy('btn-update-adapter-migrate-pipelines');
    }

    public static adapterEditWarning() {
        return cy.dataCy('sp-connect-adapter-edit-warning', { timeout: 10000 });
    }

    public static adapterManualPipelineMigrationWarning() {
        return cy.dataCy('adapter-manual-pipeline-migration-warning', {
            timeout: 10000,
        });
    }

    public static adapterChartEditWarning() {
        return cy.dataCy('sp-connect-adapter-chart-edit-warning', {
            timeout: 10000,
        });
    }

    public static nextBtn() {
        return cy.get('button').contains('Next').parent();
    }

    public static deleteAdapterConfirmationButton() {
        return cy.dataCy('delete-adapter-confirmation');
    }

    public static connectNewAdapterCancel() {
        return cy.dataCy('connect-new-adapter-cancel');
    }

    public static getNewSampleBtn() {
        return cy.dataCy('connect-get-new-sample-button');
    }

    public static refreshSchemaBtn() {
        return cy.dataCy('connect-refresh-schema-button', { timeout: 10000 });
    }

    public static connectAdapterAddedSuccessfully() {
        return cy.dataCy('sp-connect-adapter-success-added', {
            timeout: 60000,
        });
    }

    // =====================  Adapter settings btns  ==========================
    public static adapterSettingsStartAdapter() {
        return cy.dataCy('adapter-settings-start-adapter-btn');
    }

    public static startAdapterNowCheckbox() {
        return cy.dataCy('start-adapter-now-checkbox');
    }

    public static startAllAdapters() {
        return cy.dataCy('start-all-adapters-btn');
    }

    public static stopAllAdapters() {
        return cy.dataCy('stop-all-adapters-btn');
    }

    public static showCodeCheckbox() {
        return cy.dataCy('show-code-checkbox');
    }

    public static deleteAdapterAndAssociatedPipelineConfirmation() {
        return cy.dataCy(
            'delete-adapter-and-associated-pipelines-confirmation',
            {
                timeout: 10000,
            },
        );
    }

    public static showAssetCheckbox() {
        return cy.dataCy('show-asset-checkbox');
    }

    public static connectRemoveDuplicateBox() {
        return cy.dataCy('connect-remove-duplicates-box');
    }

    public static connectReduceEventRate() {
        return cy.dataCy('connect-reduce-event-rate-box');
    }

    public static assetCheckbox() {
        return cy.dataCy('show-asset-checkbox');
    }

    public static adapterSettingsNextBtn() {
        return cy.dataCy('adapter-settings-next-button');
    }

    public static fileInputSelected() {
        return cy.dataCy('file-input-selected', {
            timeout: 10000,
        });
    }

    // ========================================================================

    // =====================  Event Schema buttons  ==========================

    public static schemaUnitFromDropdown() {
        return cy.dataCy('connect-schema-unit-from-dropdown');
    }

    public static schemaUnitTransformBtn() {
        return cy.dataCy('connect-schema-unit-transform-btn');
    }

    public static schemaUnitToDropdown() {
        return cy.dataCy('connect-schema-unit-to-dropdown');
    }

    public static saveEditProperty() {
        cy.dataCy('sp-save-edit-property', { timeout: 10000 }).should(
            'have.length',
            1,
        );
        return cy.dataCy('sp-save-edit-property', { timeout: 10000 });
    }

    public static setTimestampConverter(option: 'Number' | 'String') {
        cy.dataCy('connect-timestamp-converter')
            .click()
            .get('mat-option')
            .contains(option)
            .click();
    }

    public static timestampStringRegex() {
        return cy.dataCy('connect-timestamp-string-regex', { timeout: 10000 });
    }

    public static configureSchemaNextBtn() {
        return cy.dataCy('configure-schema-next-button');
    }

    public static configureSchemaBackBtn() {
        return cy.dataCy('configure-schema-back-button');
    }

    public static eventPropertyRow() {
        return cy.dataCy('event-property-row', { timeout: 10000 });
    }

    public static scriptActiveToggle() {
        return cy.dataCy('toggle-script-active', {
            timeout: 10000,
        });
    }

    public static configureSchemaScriptEditor() {
        return cy
            .dataCy('configure-schema-script-editor', {
                timeout: 10000,
            })
            .find('.view-lines');
    }

    public static configureSchemaScriptEditorTextarea() {
        return cy
            .dataCy('configure-schema-script-editor', {
                timeout: 10000,
            })
            .find('.monaco-editor .native-edit-context');
    }

    public static setConfigureSchemaScriptEditorValue(script: string) {
        return cy
            .dataCy('configure-schema-script-editor', {
                timeout: 10000,
            })
            .find('.monaco-editor')
            .then($editor => {
                const dataUri = $editor[0]?.getAttribute('data-uri');

                cy.window().then(win => {
                    const monaco = (win as any).monaco;
                    const model = monaco?.editor
                        ?.getModels()
                        .find(
                            (currentModel: any) =>
                                currentModel.uri.toString() === dataUri,
                        );

                    model.setValue(script);
                });
            });
    }

    public static configureSchemaRunScriptBtn() {
        return cy.dataCy('configure-schema-run-script-button', {
            timeout: 10000,
        });
    }

    public static useScriptTemplateBtn() {
        return cy.dataCy('use-script-template', {
            timeout: 10000,
        });
    }

    public static saveSelectScriptTemplateBtn() {
        return cy.dataCy('save-select-script-template', {
            timeout: 10000,
        });
    }

    public static selectScriptTemplateDropDown() {
        return cy.dataCy('select-script-template', {
            timeout: 10000,
        });
    }

    public static deleteScriptTemplateBtn() {
        return cy.dataCy('delete-script-template', {
            timeout: 10000,
        });
    }

    public static addScriptTemplateBtn() {
        return cy.dataCy('add-script-template-button', {
            timeout: 10000,
        });
    }

    public static scriptTemplateName() {
        return cy.dataCy('script-template-name', {
            timeout: 10000,
        });
    }

    public static saveScriptTemplateBtn() {
        return cy.dataCy('save-script-template', {
            timeout: 10000,
        });
    }

    public static resetScriptBtn() {
        return cy.dataCy('reset-script', {
            timeout: 10000,
        });
    }

    public static configureSchemaEventPreviewOriginal() {
        return cy.dataCy('configure-schema-event-preview-original', {
            timeout: 10000,
        });
    }

    public static configureSchemaEventPreviewResult() {
        return cy.dataCy('configure-schema-event-preview-result', {
            timeout: 10000,
        });
    }

    public static uploadSampleBtn() {
        return cy.dataCy('connect-upload-sample-button', {
            timeout: 10000,
        });
    }

    public static uploadSampleDialogTextarea() {
        return cy.dataCy('upload-sample-event-textarea', {
            timeout: 10000,
        });
    }

    public static uploadSampleDialogSubmitBtn() {
        return cy.dataCy('upload-sample-event-submit', {
            timeout: 10000,
        });
    }

    public static configureFieldsEventPreviewResult() {
        return cy.dataCy('configure-fields-event-preview-result', {
            timeout: 10000,
        });
    }

    public static configureFieldsNextBtn() {
        return cy.dataCy('configure-fields-next-button');
    }

    public static configureFieldsBackBtn() {
        return cy.dataCy('configure-fields-back-button');
    }

    public static semanticTypeInput() {
        return cy.dataCy('semantic-type', {
            timeout: 10000,
        });
    }

    // ========================================================================

    // =====================  Format configurations  ==========================

    public static csvDelimiter() {
        return 'format-org.apache.streampipes.extensions.management.connect.adapter.parser.csv-1-delimiter-0';
    }

    public static csvHeader() {
        return 'format-org.apache.streampipes.extensions.management.connect.adapter.parser.csv-1-header-1';
    }

    public static jsonArrayFieldKey() {
        return 'format-org.apache.streampipes.extensions.management.connect.adapter.parser.json-0-json_options-0-arrayFieldConfig-2-key-0';
    }

    public static xmlTag() {
        return 'format-org.apache.streampipes.extensions.management.connect.adapter.parser.xml-2-tag-0';
    }

    // ========================================================================
}
