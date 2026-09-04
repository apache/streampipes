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

export class DatasetBtns {
    public static datasetTable() {
        return cy.dataCy('datalake-settings', { timeout: 10000 });
    }

    public static refreshDataLakeMeasures() {
        return cy.dataCy('refresh-data-lake-measures', { timeout: 10000 });
    }

    public static openCsvImportDialog() {
        return cy.dataCy('open-csv-import-dialog', { timeout: 10000 });
    }

    public static csvImportFileInput() {
        return cy.dataCy('csv-import-file-input', { timeout: 10000 });
    }

    public static csvImportTargetMode() {
        return cy.dataCy('csv-import-target-mode', { timeout: 10000 });
    }

    public static csvImportTargetModeNew() {
        return cy.dataCy('csv-import-target-mode-new', { timeout: 10000 });
    }

    public static csvImportTargetModeExisting() {
        return cy.dataCy('csv-import-target-mode-existing', { timeout: 10000 });
    }

    public static csvImportNewMeasurement() {
        return cy.dataCy('csv-import-new-measurement', { timeout: 10000 });
    }

    public static csvImportExistingMeasurement() {
        return cy.dataCy('csv-import-existing-measurement', { timeout: 10000 });
    }

    public static csvImportDelimiter() {
        return cy.dataCy('csv-import-delimiter', { timeout: 10000 });
    }

    public static csvImportDelimiterComma() {
        return cy.dataCy('csv-import-delimiter-comma', { timeout: 10000 });
    }

    public static csvImportNextBtn() {
        return cy.dataCy('csv-import-next-btn', { timeout: 10000 });
    }

    public static csvImportUploadBtn() {
        return cy.dataCy('csv-import-upload-btn', { timeout: 10000 });
    }

    public static csvImportCloseBtn() {
        return cy.dataCy('csv-import-close-btn', { timeout: 10000 });
    }

    public static csvImportSuccessTitle() {
        return cy.dataCy('csv-import-success-title', { timeout: 10000 });
    }

    public static csvImportColumnScope(index: number) {
        return cy
            .dataCy('csv-import-column-scope', { timeout: 10000 })
            .eq(index);
    }

    public static csvImportColumnScopeTimestamp() {
        return cy.dataCy('csv-import-column-scope-timestamp', {
            timeout: 10000,
        });
    }

    public static csvImportTimestampFormat() {
        return cy.dataCy('csv-import-timestamp-format', { timeout: 10000 });
    }

    public static csvImportSchemaMismatch() {
        return cy.dataCy('csv-import-schema-mismatch', { timeout: 10000 });
    }

    public static csvImportSchemaMismatchList() {
        return cy.dataCy('csv-import-schema-mismatch-list', {
            timeout: 10000,
        });
    }

    public static csvImportSchemaMismatchItems() {
        return cy.dataCy('csv-import-schema-mismatch-item', {
            timeout: 10000,
        });
    }

    public static datasetRow(name: string) {
        return cy.contains('[data-cy="datalake-settings"] tbody tr', name, {
            timeout: 10000,
        });
    }

    public static datasetDetailsSchemaTable() {
        return cy.dataCy('dataset-details-schema-table', { timeout: 10000 });
    }

    public static datasetDetailsSchemaField(runtimeName: string) {
        return cy.dataCy(`dataset-details-schema-field-${runtimeName}`, {
            timeout: 10000,
        });
    }

    public static datasetDetailsSchemaType(runtimeName: string) {
        return cy.dataCy(`dataset-details-schema-type-${runtimeName}`, {
            timeout: 10000,
        });
    }

    public static datasetDetailsEventLimit() {
        return cy.dataCy('dataset-details-event-limit', { timeout: 10000 });
    }

    public static datasetDetailsEventsTable() {
        return cy.dataCy('dataset-details-events-table', { timeout: 30000 });
    }

    public static datasetDetailsEventCell(columnName: string) {
        return cy.dataCy(`dataset-details-event-cell-${columnName}`, {
            timeout: 30000,
        });
    }

    public static datasetDetailsCreateChart() {
        return cy.dataCy('dataset-details-create-chart', { timeout: 10000 });
    }

    public static datasetLastEventCell(name: string) {
        return this.datasetRow(name).find('[data-cy="datalake-last-event"]');
    }

    public static datalakeLastEvent() {
        return cy.dataCy('datalake-last-event', { timeout: 30000 });
    }

    public static dataLakeTruncateBtn() {
        return cy.dataCy('datalake-truncate-btn');
    }

    public static dataLakeDeleteBtn() {
        return cy.dataCy('datalake-delete-btn');
    }

    public static confirmDataLakeTruncateBtn() {
        return cy.dataCy('confirm-truncate-data-btn', { timeout: 10000 });
    }

    public static confirmDataLakeDeleteBtn() {
        return cy.dataCy('confirm-delete-data-btn', { timeout: 10000 });
    }
}
