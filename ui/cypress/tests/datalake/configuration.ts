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

import { FileManagementUtils } from '../../support/utils/FileManagementUtils';
import { GenericAdapterBuilder } from '../../support/builder/GenericAdapterBuilder';
import { AdapterUtils } from '../../support/utils/AdapterUtils';
import { PipelineBuilder } from '../../support/builder/PipelineBuilder';
import { PipelineElementBuilder } from '../../support/builder/PipelineElementBuilder';
import { PipelineUtils } from '../../support/utils/PipelineUtils';

const adapterName = 'datalake_configuration';
const pipelineName = 'Datalake Configuration Test';
const dataLakeIndex = 'configurationtest';


describe('Test Truncate data in datalake', () => {

  before('Setup Test', () => {
    prepareTest();
  });

  it('Perform Test', () => {

    goToDatalakeConfiguration();

    // Check if amount of events is correct
    cy.dataCy('datalake-number-of-events', { timeout: 10000 })
      .should('be.visible')
      .contains('10');

    // Truncate data
    cy.dataCy('datalake-truncate-btn')
      .should('be.visible')
      .click();
    cy.dataCy('confirm-truncate-data-btn', { timeout: 10000 })
      .should('be.visible')
      .click();

    // Check if amount of events is zero
    cy.dataCy('datalake-number-of-events', { timeout: 10000 })
      .should('be.visible')
      .contains('0');

  });

});

describe('Delete data in datalake', () => {
  before('Setup Test', () => {
    prepareTest();

    PipelineUtils.deletePipeline();
  });

  it('Perform Test', () => {

    goToDatalakeConfiguration();

    // Check if amount of events is correct
    cy.dataCy('datalake-number-of-events', { timeout: 10000 })
      .should('be.visible')
      .contains('10');

    // Delete data
    cy.dataCy('datalake-delete-btn')
      .should('be.visible')
      .click();
    cy.dataCy('confirm-delete-data-btn', { timeout: 10000 })
      .should('be.visible')
      .click();

    // Check if amount of events is zero
    cy.dataCy('datalake-number-of-events', { timeout: 10000 })
      .should('have.length', 0);
  });

});

const goToDatalakeConfiguration = () => {
  cy.visit('#/configuration');
  cy.get('div').contains('DataLake').parent().click();
};

const prepareTest = () => {
  cy.initStreamPipesTest();
  // Create adapter with dataset
  FileManagementUtils.addFile('fileTest/random.csv');
  const adapter = GenericAdapterBuilder
    .create('File_Set')
    .setName(adapterName)
    .setTimestampProperty('timestamp')
    .setFormat('csv')
    .addFormatInput('input', 'delimiter', ';')
    .addFormatInput('checkbox', 'header', 'check')
    .build();
  AdapterUtils.addGenericSetAdapter(adapter);

  // Create pipeline to store dataset in datalake
  const pipelineInput = PipelineBuilder.create(pipelineName)
    .addSource(adapterName)
    .addSourceType('set')
    .addSink(
      PipelineElementBuilder.create('data_lake')
        .addInput('input', 'db_measurement', dataLakeIndex)
        .build())
    .build();
  PipelineUtils.addPipeline(pipelineInput);

  // Wait till data is stored
  cy.wait(10000);
};
