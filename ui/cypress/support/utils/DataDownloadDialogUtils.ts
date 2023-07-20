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

import { ExportConfig } from '../../../src/app/core-ui/data-download-dialog/model/export-config.model';
import { DataLakeUtils } from './datalake/DataLakeUtils';
import { FileNameService } from '../../../src/app/core-ui/data-download-dialog/services/file-name.service';
import { CsvFormatExportConfig } from '../../../src/app/core-ui/data-download-dialog/model/format-export-config.model';

export class DataDownloadDialogUtils {
    public static testDownload(
        exportConfig: ExportConfig,
        resultFileLocation: string,
        dataViewName: string,
    ) {
        // const exportDate: Date;
        DataLakeUtils.goToDatalake();

        // select data view in edit mode
        DataLakeUtils.editDataView(dataViewName);

        // select download button
        cy.dataCy('download-prepared_data-table').click();

        // download-customInterval, download-all, download-visible
        cy.dataCy(
            `download-configuration-${exportConfig.dataExportConfig.dataRangeConfiguration}`,
        ).within(() => {
            cy.get('.mdc-radio').click();
        });

        // download-ignore, download-emtpy
        cy.dataCy(
            `download-configuration-${exportConfig.dataExportConfig.missingValueBehaviour}`,
        ).within(() => {
            cy.get('.mdc-radio').click();
        });

        // click next
        cy.dataCy('download-configuration-next-btn').click();

        // Format
        cy.dataCy(
            `download-configuration-${exportConfig.formatExportConfig.exportFormat}`,
        ).within(() => {
            cy.get('.mdc-radio').click();
        });
        if ('delimiter' in exportConfig.formatExportConfig) {
            cy.dataCy(
                `download-configuration-delimiter-${
                    (exportConfig.formatExportConfig as CsvFormatExportConfig)
                        .delimiter
                }`,
            ).within(() => {
                cy.get('.mdc-radio').click();
            });
        }

        // click next
        cy.dataCy('download-configuration-download-btn').click();

        const fileNameService: FileNameService = new FileNameService();
        const fileName = fileNameService.generateName(exportConfig, new Date());
        const downloadsFolder = Cypress.config('downloadsFolder');
        cy.readFile(downloadsFolder + '/' + fileName).then(
            (downloadFileString: string) => {
                cy.readFile(
                    `cypress/fixtures/dataDownloadDialog/${resultFileLocation}`,
                ).then(expectedResultString => {
                    expect(expectedResultString).to.deep.equal(
                        downloadFileString,
                    );
                });
            },
        );
    }
}
