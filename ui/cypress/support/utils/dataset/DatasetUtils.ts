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
import { DatasetBtns } from './DatasetBtns';

export class DatasetUtils {
    private static readonly CSV_IMPORT_FIXTURE_PREFIX = 'cypress/fixtures/';

    public static goToDatasets() {
        cy.visit('#/datasets');
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
        DatasetBtns.csvImportExistingMeasurement().click();
        DatasetBtns.csvImportExistingMeasurementOption(datasetName).click();
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

    public static expectDatasetTotalEventCount(
        datasetName: string,
        expectedCount: string,
    ) {
        DatasetBtns.datasetRow(datasetName)
            .should('be.visible')
            .find('[data-cy="datalake-total-count-button"]')
            .then($button => {
                if ($button.length > 0) {
                    cy.wrap($button).click({ force: true });
                }
            });

        DatasetBtns.datasetRow(datasetName)
            .find('[data-cy="datalake-number-of-events"]', {
                timeout: 10000,
            })
            .should($element => {
                const text = $element.text().trim();
                expect(text).to.equal(expectedCount);
            });
    }

    public static expectDatasetSevenDayEventCount(
        datasetName: string,
        expectedCount: string,
    ) {
        DatasetBtns.datasetRow(datasetName)
            .should('be.visible')
            .should($row => {
                const text = $row.children().eq(2).text().trim();
                expect(text).to.contain(expectedCount);
            });
    }
}
