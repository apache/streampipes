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

import { ExportConfig } from '../../../src/app/core-ui/data-download-dialog/model/export-config.model';
import { DataDownloadDialogUtils } from '../../support/utils/DataDownloadDialogUtils';
import { DataLakeUtils } from '../../support/utils/datalake/DataLakeUtils';
import { PrepareTestDataUtils } from '../../support/utils/PrepareTestDataUtils';

describe('Test data explorer data download dialog', () => {
    before('Setup Test', () => {
        cy.initStreamPipesTest();
        PrepareTestDataUtils.loadDataIntoDataLake(
            'dataDownloadDialog/input.json',
            'json_array',
        );

        DataLakeUtils.addDataViewAndTableWidget(dataViewName, 'Persist');
        DataLakeUtils.saveDataViewConfiguration();
    });

    beforeEach('Setup Test', () => {
        cy.removeDownloadDirectory();
        cy.login();
    });

    const dataViewName = 'NewWidget';

    const formatTestsExportConfig: ExportConfig = {
        formatExportConfig: undefined,
        dataExportConfig: {
            dataRangeConfiguration: 'all',
            missingValueBehaviour: 'empty',
            measurement: 'prepared_data',
        },
    };

    it('Test csv export with semicolon', () => {
        formatTestsExportConfig.formatExportConfig = {
            exportFormat: 'csv',
            delimiter: 'semicolon',
        };
        const resultFile = 'testCsvSemicolon.csv';

        DataDownloadDialogUtils.testDownload(
            formatTestsExportConfig,
            resultFile,
            dataViewName,
        );
    });

    it('Test csv export with comma', () => {
        formatTestsExportConfig.formatExportConfig = {
            exportFormat: 'csv',
            delimiter: 'comma',
        };
        const resultFile = 'testCsvComma.csv';

        DataDownloadDialogUtils.testDownload(
            formatTestsExportConfig,
            resultFile,
            dataViewName,
        );
    });

    it('Test json export', () => {
        formatTestsExportConfig.formatExportConfig = {
            exportFormat: 'json',
        };

        const resultFile = 'testJson.json';
        DataDownloadDialogUtils.testDownload(
            formatTestsExportConfig,
            resultFile,
            dataViewName,
        );
    });

    it('Test csv export with semicolon and remove missing values', () => {
        formatTestsExportConfig.formatExportConfig = {
            exportFormat: 'csv',
            delimiter: 'semicolon',
        };
        formatTestsExportConfig.dataExportConfig.missingValueBehaviour =
            'ignore';
        const resultFile = 'testRemoveLinesWithMissingValues.csv';

        DataDownloadDialogUtils.testDownload(
            formatTestsExportConfig,
            resultFile,
            dataViewName,
        );
    });
});
