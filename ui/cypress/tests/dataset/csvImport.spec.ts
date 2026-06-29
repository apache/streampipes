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

import { DatasetUtils } from '../../support/utils/dataset/DatasetUtils';

describe('CSV import happy path', () => {
    const datasetName = 'csv_machine_data_import';
    const stringTimestampDatasetName = 'csv_machine_data_import_string_ts';
    const existingDatasetName = 'csv_machine_data_existing_import';
    const missingValuesDatasetName = 'csv_machine_data_missing_values';

    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
    });

    it('Uploads a CSV file into a new dataset and shows the imported events', () => {
        DatasetUtils.openCsvImportDialog();
        DatasetUtils.uploadCsvImportFile(
            'datalake/machine-data-simulator-import.csv',
        );
        DatasetUtils.createNewDatasetFromCsv(datasetName);
        DatasetUtils.continueCsvImportToPreview();
        DatasetUtils.selectCsvImportDelimiterComma();
        DatasetUtils.selectCsvImportTimestampColumn(0);
        DatasetUtils.uploadCsvImport();
        DatasetUtils.expectDatasetNotEmpty(datasetName);
        DatasetUtils.openDatasetPreview(datasetName);
        DatasetUtils.expectDatasetPreviewDoesNotContainKey('Timestamp');
    });

    it('Uploads a CSV file with string timestamps and transforms them during import', () => {
        DatasetUtils.openCsvImportDialog();
        DatasetUtils.uploadCsvImportFile(
            'datalake/machine-data-simulator-import-string-timestamp.csv',
        );
        DatasetUtils.createNewDatasetFromCsv(stringTimestampDatasetName);
        DatasetUtils.continueCsvImportToPreview();
        DatasetUtils.selectCsvImportDelimiterComma();
        DatasetUtils.selectCsvImportTimestampColumn(0);
        DatasetUtils.setCsvImportTimestampFormat('yyyy-MM-dd HH:mm:ss');
        DatasetUtils.uploadCsvImport();
        DatasetUtils.expectDatasetNotEmpty(stringTimestampDatasetName);
    });

    it('Uploads a CSV file with missing values and still imports all rows', () => {
        DatasetUtils.openCsvImportDialog();
        DatasetUtils.uploadCsvImportFile(
            'datalake/machine-data-simulator-import-missing-values.csv',
        );
        DatasetUtils.createNewDatasetFromCsv(missingValuesDatasetName);
        DatasetUtils.continueCsvImportToPreview();
        DatasetUtils.selectCsvImportDelimiterComma();
        DatasetUtils.selectCsvImportTimestampColumn(0);
        DatasetUtils.uploadCsvImport();
        DatasetUtils.expectDatasetNotEmpty(missingValuesDatasetName);
    });

    it('Appends matching data to an existing dataset and warns on mismatched timestamp schema', () => {
        DatasetUtils.openCsvImportDialog();
        DatasetUtils.uploadCsvImportFile(
            'datalake/machine-data-simulator-import.csv',
        );
        DatasetUtils.createNewDatasetFromCsv(existingDatasetName);
        DatasetUtils.continueCsvImportToPreview();
        DatasetUtils.selectCsvImportDelimiterComma();
        DatasetUtils.selectCsvImportTimestampColumn(0);
        DatasetUtils.uploadCsvImport();
        DatasetUtils.expectDatasetNotEmpty(existingDatasetName);

        DatasetUtils.openCsvImportDialog();
        DatasetUtils.uploadCsvImportFile(
            'datalake/machine-data-simulator-import-later-timestamps.csv',
        );
        DatasetUtils.useExistingDatasetForCsvImport(existingDatasetName);
        DatasetUtils.continueCsvImportToPreview();
        DatasetUtils.selectCsvImportDelimiterComma();
        DatasetUtils.uploadCsvImport();
        DatasetUtils.expectDatasetNotEmpty(existingDatasetName);

        DatasetUtils.openCsvImportDialog();
        DatasetUtils.uploadCsvImportFile(
            'datalake/machine-data-simulator-import-mismatched-timestamp.csv',
        );
        DatasetUtils.useExistingDatasetForCsvImport(existingDatasetName);
        DatasetUtils.continueCsvImportToPreview();
        DatasetUtils.selectCsvImportDelimiterComma();
        DatasetUtils.expectCsvImportSchemaMismatch(
            'Imported columns must exactly match the existing measurement schema.',
            'Timestamp column must be "timestamp" but is "event_time".',
        );
    });
});
