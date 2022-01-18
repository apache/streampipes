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

import { DataLakeUtils } from '../../../support/utils/DataLakeUtils';


describe('Test Table View in Data Explorer', () => {

  const useGlobalEditOption = false;

  beforeEach('Setup Test', () => {
    cy.initStreamPipesTest();
    DataLakeUtils.loadRandomDataSetIntoDataLake();
  });

  it('Perform Test', () => {

    DataLakeUtils.goToDatalake();

    configureTimeSeriesView('Test View 1', false);

    cy.get('div').contains('Start').parent().click();

    configureTimeSeriesView('Test View 2', true);

    cy.get('div').contains('Test View 1').parent().click();

    cy.get('div').contains('Test View 2').parent().click();

    changeConfigurationTimeSeriesView(useGlobalEditOption);

    cy.get('path[class="scatterpts"]');

  });

});

const configureTimeSeriesView = (name : string, useOptions : boolean) =>  
{
  
  DataLakeUtils.createAndEditDataView(name);

  if (useOptions) {
    cy.dataCy('options-data-explorer')
        .click();
    
    cy.get('div').contains('Edit dashboard').click();
  }

  DataLakeUtils.addNewWidget();

  DataLakeUtils.selectDataSet('Persist');

  DataLakeUtils.dataConfigSelectAllFields();

  DataLakeUtils.selectVisualizationConfig();

  DataLakeUtils.selectVisualizationType('Time Series');

  DataLakeUtils.clickCreateButton();

  DataLakeUtils.selectTimeRange(
    new Date(2020, 10, 20, 22, 44),
    new Date(2021, 10, 20, 22, 44));

    cy.get('div').contains('Save').parent().click();

};

const changeConfigurationTimeSeriesView = (useGlobalEditOptions : boolean) =>  
{
 
  if (useGlobalEditOptions) {

    cy.dataCy('options-data-explorer')
        .click();
    
    cy.get('div').contains('Edit dashboard').click();

  } else{ 

    cy.get('button[mattooltip*="options"]')
        .click();

    cy.get('div').contains('Edit Widget').click();

  }

  cy.get('div').contains('Line').click();

  cy.get('div').contains('Scatter').click();

};