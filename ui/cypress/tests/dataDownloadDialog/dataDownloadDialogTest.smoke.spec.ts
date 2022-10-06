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
import { CsvFormatExportConfig } from '../../../src/app/core-ui/data-download-dialog/model/format-export-config.model';
import { DataLakeUtils } from '../../support/utils/DataLakeUtils';


describe('Test live data download dialog', () => {
  before('Setup Test', () => {
    cy.initStreamPipesTest();
    DataLakeUtils.loadDataIntoDataLake('datalake/sample.csv');
    DataLakeUtils.addDataViewAndTableWidget(dataViewName, 'Persist');
    DataLakeUtils.saveDataExplorerWidgetConfiguration();
  });

  beforeEach('Setup Test', () => {
    cy.login();
  });

  const dataViewName = 'TestView';

  const exportConfig: ExportConfig = {
    dataExportConfig: {
      dataRangeConfiguration: 'all',
      missingValueBehaviour: 'ignore',
      measurement: 'datalake_configuration'
    },
    formatExportConfig: {
      exportFormat: 'csv',
      delimiter: 'semicolon'
    }
  };

  it('Data Explorer CSV with Semicolon', () => {
    const resultFile = 'test1.csv';
    DataDownloadDialogUtils.testDownload(exportConfig, resultFile, dataViewName);
  });

  it('Data Explorer CSV with Semicolon', () => {
    (exportConfig.formatExportConfig as CsvFormatExportConfig).delimiter = 'comma';
    const resultFile = 'test2.csv';
    DataDownloadDialogUtils.testDownload(exportConfig, resultFile, dataViewName);
  });
});
