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

import { DataLakeFilterConfig } from '../../support/model/DataLakeFilterConfig';
import { DataLakeUtils } from '../../support/utils/DataLakeUtils';


describe('Test Table View in Data Explorer', () => {

  before('Setup Test', () => {
    cy.initStreamPipesTest();
    DataLakeUtils.loadDataIntoDataLake('datalake/sample.csv');
  });

  it('Perform Test', () => {

    DataLakeUtils.goToDatalake();

    DataLakeUtils.createAndEditDataView();

    DataLakeUtils.selectTimeRange(
      new Date(2020, 10, 20, 22, 44),
      new Date(2021, 10, 20, 22, 44));

    DataLakeUtils.addNewWidget();

    DataLakeUtils.selectDataSet('Persist');

    DataLakeUtils.dataConfigSelectAllFields();

    DataLakeUtils.selectVisualizationConfig();

    DataLakeUtils.selectVisualizationType('Table');

    DataLakeUtils.clickCreateButton();

    // Validate that X lines are available
    checkTableRows(10);

    // Go back to data configuration
    DataLakeUtils.selectDataConfig();

    /**
     * Test filter configuration
     */
      // Test number
    let filterConfig = new DataLakeFilterConfig('randomnumber', '22', '=');
    DataLakeUtils.dataConfigAddFilter(filterConfig);
    checkTableRows(2);
    DataLakeUtils.dataConfigRemoveFilter();
    checkTableRows(10);

    // Test number greater then
    filterConfig = new DataLakeFilterConfig('randomnumber', '50', '>');
    DataLakeUtils.dataConfigAddFilter(filterConfig);
    checkTableRows(5);
    DataLakeUtils.dataConfigRemoveFilter();

    // Test number smaller then
    filterConfig = new DataLakeFilterConfig('randomnumber', '50', '<');
    DataLakeUtils.dataConfigAddFilter(filterConfig);
    checkTableRows(5);
    DataLakeUtils.dataConfigRemoveFilter();

    // Test boolean
    filterConfig = new DataLakeFilterConfig('randombool', 'true', '=');
    DataLakeUtils.dataConfigAddFilter(filterConfig);
    checkTableRows(6);
    DataLakeUtils.dataConfigRemoveFilter();

    // Test string
    filterConfig = new DataLakeFilterConfig('randomtext', 'a', '=');
    DataLakeUtils.dataConfigAddFilter(filterConfig);
    checkTableRows(4);
    DataLakeUtils.dataConfigRemoveFilter();

    /**
     * Test groupBy configuration
     */
    // Select group by option
    cy.dataCy('data-explorer-group-by-randomtext').children().click();
    cy.dataCy('data-explorer-table-row-randomtext', { timeout: 10000 }).first().contains('a');
    cy.dataCy('data-explorer-table-row-randomtext', { timeout: 10000 }).last().contains('c');
    checkTableRows(10);
    cy.dataCy('data-explorer-group-by-randomtext').children().click();

  });

  const checkTableRows = (numberOfRows: number) => {
    cy.dataCy('data-explorer-table-row-timestamp', { timeout: 10000 }).should('have.length', numberOfRows);
  };


});
