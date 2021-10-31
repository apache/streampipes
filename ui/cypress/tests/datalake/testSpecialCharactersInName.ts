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

import { DataLakeUtils } from '../../support/utils/DataLakeUtils';
import { AdapterUtils } from '../../support/utils/AdapterUtils';
import { PipelineBuilder } from '../../support/builder/PipelineBuilder';
import { PipelineElementBuilder } from '../../support/builder/PipelineElementBuilder';
import { PipelineUtils } from '../../support/utils/PipelineUtils';
import { FileManagementUtils } from '../../support/utils/FileManagementUtils';


describe('Test Table View in Data Explorer', () => {

  before('Setup Test', () => {
    cy.initStreamPipesTest();
  });

  it('Perform Test', () => {

    FileManagementUtils.addFile('fileTest/random.csv');

    // TODO this test must be fixed and extended with more special characters
    const dataLakeName = 'specialCharacters';
    const adapterName = 'adaptername';
    // Add Adpater

    const adapter = DataLakeUtils.getDataLakeTestSetAdapter(adapterName, false);
    AdapterUtils.addGenericSetAdapter(adapter);

    const pipelineInput = PipelineBuilder.create(dataLakeName)
      .addSource(adapterName)
      .addSourceType('set')
      .addSink(
        PipelineElementBuilder.create('data_lake')
          .addInput('input', 'db_measurement', dataLakeName)
          .build())
      .build();

    PipelineUtils.testPipeline(pipelineInput);

    // Wait till data is stored
    cy.wait(10000);

    // Build Pipeline

    DataLakeUtils.goToDatalake();

    DataLakeUtils.createAndEditDataView();

    DataLakeUtils.addNewWidget();

    DataLakeUtils.selectDataSet(dataLakeName);

    DataLakeUtils.dataConfigSelectAllFields();

    DataLakeUtils.selectVisualizationConfig();

    DataLakeUtils.selectVisualizationType('Table');

    DataLakeUtils.clickCreateButton();

    DataLakeUtils.selectTimeRange(
      new Date(2020, 10, 20, 22, 44),
      new Date(2021, 10, 20, 22, 44));

    cy.dataCy('data-explorer-table-row', { timeout: 10000 }).should('have.length', 10);

  });

});
