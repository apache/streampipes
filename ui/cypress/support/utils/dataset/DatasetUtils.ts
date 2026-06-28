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

import { PermissionUtils } from '../user/PermissionUtils';
import { GeneralUtils } from '../GeneralUtils';
import { DatasetBtns } from './DatasetBtns';

export class DatasetUtils {
    private static readonly CSV_IMPORT_FIXTURE_PREFIX = 'cypress/fixtures/';

    public static goToDatasets() {
        cy.visit('#/datasets');
    }

    public static goToDatalakeConfiguration() {
        this.goToDatasets();
    }

    public static refreshDataLakeMeasures() {
        DatasetBtns.refreshDataLakeMeasures().should('be.visible').click();
        DatasetBtns.datasetTable().should('be.visible');
    }

    public static checkAmountOfDatasets(amount: number) {
        DatasetUtils.goToDatasets();

        if (amount === 0) {
            // The wait is needed because the default value is the no-table-entries element.
            // It must be waited till the data is loaded. Once a better solution is found, this can be removed.
            cy.wait(1000);
            cy.dataCy('no-table-entries').should('be.visible');
        } else {
            DatasetBtns.datasetTable().should('have.length', amount);
        }
    }

    public static authorizeUserOnDataset(datasetname: string, email: string) {
        DatasetUtils.goToDatasets();
        PermissionUtils.authorizeUser(datasetname, email);
    }

    public static openCsvImportDialog() {
        this.goToDatasets();
        DatasetBtns.openCsvImportDialog().click();
    }

    public static uploadCsvImportFile(filePath: string) {
        DatasetBtns.csvImportFileInput().selectFile(
            this.CSV_IMPORT_FIXTURE_PREFIX + filePath,
            { force: true },
        );
    }

    public static createNewDatasetFromCsv(datasetName: string) {
        DatasetBtns.csvImportTargetMode().click();
        DatasetBtns.csvImportTargetModeNew().click();
        DatasetBtns.csvImportNewMeasurement().clear().type(datasetName);
    }

    public static useExistingDatasetForCsvImport(datasetName: string) {
        DatasetBtns.csvImportTargetMode().click();
        DatasetBtns.csvImportTargetModeExisting().click();
        DatasetBtns.csvImportExistingMeasurement().click({ force: true });
        cy.get('mat-option', { timeout: 10000 })
            .contains(datasetName)
            .click({ force: true });
    }

    public static selectCsvImportDelimiterComma() {
        DatasetBtns.csvImportDelimiter().click();
        DatasetBtns.csvImportDelimiterComma().click();
        cy.dataCy('csv-import-column-scope', { timeout: 10000 }).should(
            'have.length',
            7,
        );
    }

    public static continueCsvImportToPreview() {
        DatasetBtns.csvImportNextBtn().click();
        cy.dataCy('csv-import-preview', { timeout: 10000 }).should(
            'be.visible',
        );
    }

    public static selectCsvImportTimestampColumn(columnIndex: number) {
        DatasetBtns.csvImportColumnScope(columnIndex).click();
        DatasetBtns.csvImportColumnScopeTimestamp().click();
    }

    public static setCsvImportTimestampFormat(format: string) {
        DatasetBtns.csvImportTimestampFormat().clear().type(format);
    }

    public static uploadCsvImport() {
        DatasetBtns.csvImportUploadBtn().click();
        DatasetBtns.csvImportSuccessTitle().should('be.visible');
        DatasetBtns.csvImportCloseBtn().click();
    }

    public static expectCsvImportSchemaMismatch(
        summary: string,
        detailText?: string,
    ) {
        DatasetBtns.csvImportSchemaMismatch().should('be.visible');
        cy.dataCy('exception-message-title').should('contain.text', summary);
        if (detailText) {
            DatasetBtns.csvImportSchemaMismatchList().should('be.visible');
            DatasetBtns.csvImportSchemaMismatchItems().should(
                'contain.text',
                detailText,
            );
        }
    }

    public static openDatasetPreview(datasetName: string) {
        DatasetBtns.datasetRow(datasetName)
            .find('mat-icon')
            .contains('preview')
            .parent('button')
            .click();
    }

    public static openDatasetDetails(datasetName: string) {
        DatasetUtils.goToDatasets();
        DatasetUtils.waitForDatasetNotEmpty(datasetName);
        DatasetBtns.datasetRow(datasetName).click();
        cy.url().should('include', '#/datasets/');
    }

    public static waitForDatasetNotEmpty(
        datasetName?: string,
        attempts = 30,
    ): Cypress.Chainable<string> {
        this.refreshDataLakeMeasures();
        return this.getDatasetLastEventCell(datasetName).then($cell => {
            const lastEvent = this.getComparableLastEventValueFromElements(
                Array.from($cell),
            );

            if (this.isDatasetNotEmptyValue(lastEvent)) {
                return lastEvent;
            } else if (attempts > 0) {
                cy.wait(1000);
                return DatasetUtils.waitForDatasetNotEmpty(
                    datasetName,
                    attempts - 1,
                );
            } else {
                expect(this.isDatasetNotEmptyValue(lastEvent)).to.equal(true);
                return lastEvent;
            }
        });
    }

    public static expectDatasetEmpty(datasetName?: string) {
        this.refreshDataLakeMeasures();
        this.getDatasetLastEventCell(datasetName)
            .should('be.visible')
            .should($element => {
                expect(
                    DatasetUtils.isDatasetEmptyValue($element.text()),
                ).to.equal(true);
            });
    }

    public static expectDatasetNotEmpty(datasetName?: string) {
        this.refreshDataLakeMeasures();
        this.getDatasetLastEventCell(datasetName)
            .should('be.visible')
            .should($element => {
                expect(
                    DatasetUtils.isDatasetNotEmptyValue($element.text()),
                ).to.equal(true);
            });
    }

    public static expectDatasetDeleted(datasetName?: string) {
        this.refreshDataLakeMeasures();
        if (datasetName) {
            DatasetBtns.datasetRow(datasetName).should('not.exist');
            return;
        }

        this.getDatasetLastEventCell(datasetName).should('not.exist');
    }

    public static expectDatasetLastEventChanged(
        previousLastEvent: string,
        datasetName?: string,
    ) {
        this.waitForDatasetLastEventChanged(previousLastEvent, datasetName);
    }

    private static getDatasetLastEventCell(datasetName?: string) {
        return datasetName
            ? DatasetBtns.datasetLastEventCell(datasetName)
            : DatasetBtns.datalakeLastEvent();
    }

    private static isDatasetEmptyValue(value: string) {
        return value.trim() === 'n/a';
    }

    private static isDatasetNotEmptyValue(value: string) {
        const normalizedValue = value.trim();
        return normalizedValue.length > 0 && !this.isDatasetEmptyValue(value);
    }

    private static getComparableLastEventValue(value: string) {
        const trimmedValue = value.trim();
        const exactTimeMatch = trimmedValue.match(/\(([^()]*)\)$/);
        return exactTimeMatch?.[1] ?? trimmedValue;
    }

    private static waitForDatasetLastEventChanged(
        previousLastEvent: string,
        datasetName?: string,
        attempts = 30,
    ): Cypress.Chainable<string> {
        const previousComparableValue =
            this.getComparableLastEventValue(previousLastEvent);

        this.refreshDataLakeMeasures();
        return this.getDatasetLastEventCell(datasetName).then($cell => {
            const lastEvent = this.getComparableLastEventValueFromElements(
                Array.from($cell),
            );
            const lastEventChanged =
                this.isDatasetNotEmptyValue(lastEvent) &&
                this.getComparableLastEventValue(lastEvent) !==
                    previousComparableValue;

            if (lastEventChanged) {
                return lastEvent;
            } else if (attempts > 0) {
                cy.wait(1000);
                return this.waitForDatasetLastEventChanged(
                    previousLastEvent,
                    datasetName,
                    attempts - 1,
                );
            } else {
                expect(
                    this.getComparableLastEventValue(lastEvent),
                ).not.to.equal(previousComparableValue);
                return lastEvent;
            }
        });
    }

    private static getComparableLastEventValueFromElements(
        cells: HTMLElement[],
    ): string {
        const rawLastEventValue = cells
            .flatMap(cell =>
                Array.from(
                    cell.querySelectorAll('sp-datalake-last-event-label'),
                ),
            )
            .map(label => label.getAttribute('data-last-event-value'))
            .find(value => value && this.isDatasetNotEmptyValue(value));

        return (
            rawLastEventValue ??
            this.getComparableLastEventValue(
                cells.map(cell => cell.textContent ?? '').join(' '),
            )
        );
    }

    public static openLatestEventsTab() {
        GeneralUtils.tab('Latest events');
    }

    public static setLatestEventsLimit(limit: number) {
        DatasetBtns.datasetDetailsEventLimit().clear().type(`${limit}`);
        DatasetBtns.datasetDetailsEventLimit().blur();
    }

    public static expectSchemaField(runtimeName: string, expectedType: string) {
        DatasetBtns.datasetDetailsSchemaField(runtimeName).should('be.visible');
        DatasetBtns.datasetDetailsSchemaType(runtimeName).should(
            'contain.text',
            expectedType,
        );
    }

    public static expectLatestEventsForColumn(columnName: string) {
        DatasetBtns.datasetDetailsEventsTable().should('be.visible');
        DatasetBtns.datasetDetailsEventCell(columnName)
            .should('exist')
            .and('have.length.at.least', 1);
    }

    public static createChartFromDatasetDetails() {
        DatasetBtns.datasetDetailsCreateChart().click();
    }

    public static expectDatasetPreviewDoesNotContainKey(key: string) {
        cy.dataCy('dataset-preview-table', { timeout: 10000 }).should(
            'be.visible',
        );
        cy.dataCy(`dataset-preview-key-${key.toLowerCase()}`, {
            timeout: 10000,
        }).should('not.exist');
    }
}
