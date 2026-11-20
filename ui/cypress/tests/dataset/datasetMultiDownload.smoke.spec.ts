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

import { ExportConfig } from '../../../projects/streampipes/shared-ui/src/lib/dialog/data-download-dialog/model/export-config.model';
import { DataDownloadDialogBtns } from '../../support/utils/DataDownloadDialogBtns';
import { DataDownloadDialogUtils } from '../../support/utils/DataDownloadDialogUtils';
import { DatasetSeedUtils } from '../../support/utils/dataset/DatasetSeedUtils';
import { DatasetUtils } from '../../support/utils/dataset/DatasetUtils';

describe('Download multiple datasets from the dataset overview', () => {
    const headers = ['timestamp', 'temperature'];
    const datasets = [
        {
            name: 'multi-download-a',
            rows: [
                ['1667904471000', '21.5'],
                ['1667904472000', '22.5'],
            ],
        },
        {
            name: 'multi-download-b',
            rows: [
                ['1667904471000', '31.5'],
                ['1667904472000', '32.5'],
            ],
        },
    ];

    const exportConfigFor = (measurement: string): ExportConfig => ({
        dataExportConfig: {
            dataRangeConfiguration: 'all',
            missingValueBehaviour: 'ignore',
            measurement,
        },
        formatExportConfig: {
            format: 'csv',
            delimiter: 'comma',
            headerColumnName: 'key',
        },
    });

    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
        cy.removeDownloadDirectory();
        datasets.forEach(dataset => {
            DatasetSeedUtils.importCsvData({
                headers,
                rows: dataset.rows,
                measurementName: dataset.name,
                delimiter: ';',
                timestampColumn: 'timestamp',
            });
        });
    });

    it('Downloads each selected dataset with the same export settings', () => {
        DatasetUtils.openDownloadDialogForSelectedDatasets(
            datasets.map(dataset => dataset.name),
        );

        datasets.forEach(dataset => {
            DataDownloadDialogBtns.selectedDatasets().should(
                'contain.text',
                dataset.name,
            );
        });

        DataDownloadDialogUtils.downloadWithDefaultSettings();
        DataDownloadDialogBtns.fileSummary()
            .invoke('text')
            .should('match', /\b2\D+2\b/);
        DataDownloadDialogBtns.fileErrors().should('not.exist');

        datasets.forEach(dataset => {
            DataDownloadDialogUtils.expectDownloadedFile(
                exportConfigFor(dataset.name),
                ['temperature', ...dataset.rows.map(row => row[1])],
            );
        });
    });
});
