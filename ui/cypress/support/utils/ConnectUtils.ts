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

import { UserInput } from '../model/UserInput';
import { StaticPropertyUtils } from './StaticPropertyUtils';
import { SpecificAdapterInput } from '../model/SpecificAdapterInput';
import { GenericAdapterInput } from '../model/GenericAdapterInput';
import { SpecificAdapterBuilder } from '../builder/SpecificAdapterBuilder';
import { AdapterInput } from '../model/AdapterInput';
import { ConnectEventSchemaUtils } from './ConnectEventSchemaUtils';
import { GenericAdapterBuilder } from '../builder/GenericAdapterBuilder';
import { DataLakeUtils } from './DataLakeUtils';

export class ConnectUtils {

  public static testSpecificStreamAdapter(adapterConfiguration: SpecificAdapterInput) {

    ConnectUtils.goToConnect();

    ConnectUtils.goToNewAdapterPage();

    ConnectUtils.selectAdapter(adapterConfiguration.adapterType);

    ConnectUtils.configureAdapter(adapterConfiguration.adapterConfiguration);

    if (adapterConfiguration.timestampProperty) {
      ConnectEventSchemaUtils.markPropertyAsTimestamp(adapterConfiguration.timestampProperty);
    }

    if (adapterConfiguration.autoAddTimestamp) {
      ConnectEventSchemaUtils.addTimestampProperty();
    }

    ConnectEventSchemaUtils.finishEventSchemaConfiguration();

    ConnectUtils.startStreamAdapter(adapterConfiguration);

  }

  public static testGenericStreamAdapter(adapterConfiguration: GenericAdapterInput) {

    ConnectUtils.addGenericStreamAdapter(adapterConfiguration);

  }


  public static addGenericStreamAdapter(adapterConfiguration: GenericAdapterInput) {
    ConnectUtils.addGenericAdapter(adapterConfiguration);

    ConnectUtils.startStreamAdapter(adapterConfiguration);
  }

  public static addGenericSetAdapter(adapterConfiguration: GenericAdapterInput) {
    ConnectUtils.addGenericAdapter(adapterConfiguration);

    ConnectUtils.startSetAdapter(adapterConfiguration);
  }

  private static addGenericAdapter(adapterConfiguration: GenericAdapterInput) {
    ConnectUtils.goToConnect();

    ConnectUtils.goToNewAdapterPage();

    ConnectUtils.selectAdapter(adapterConfiguration.adapterType);

    ConnectUtils.configureAdapter(adapterConfiguration.protocolConfiguration);

    ConnectUtils.configureFormat(adapterConfiguration);


    if (adapterConfiguration.dimensionProperties.length > 0) {
      adapterConfiguration.dimensionProperties.forEach(dimensionPropertyName => {
        ConnectEventSchemaUtils.markPropertyAsDimension(dimensionPropertyName);
      });
    }

    ConnectEventSchemaUtils.markPropertyAsTimestamp(adapterConfiguration.timestampProperty);

    ConnectEventSchemaUtils.finishEventSchemaConfiguration();
  }

  public static addMachineDataSimulator(name: string, persist: boolean = false) {

    const builder = SpecificAdapterBuilder
      .create('Machine_Data_Simulator')
      .setName(name)
      .addInput('input', 'wait-time-ms', '1000');

    if (persist) {
      builder.setTimestampProperty('timestamp').
                setStoreInDataLake();
    }

    const configuration = builder.build();

    ConnectUtils.goToConnect();

    ConnectUtils.goToNewAdapterPage();

    ConnectUtils.selectAdapter(configuration.adapterType);

    ConnectUtils.configureAdapter(configuration.adapterConfiguration);

    ConnectEventSchemaUtils.finishEventSchemaConfiguration();

    ConnectUtils.startStreamAdapter(configuration);

  }

  public static goToConnect() {
    cy.visit('#/connect');
  }

  public static goToNewAdapterPage() {
    cy.dataCy('connect-create-new-adapter-button').click();
  }

  public static selectAdapter(name) {
    // Select adapter
    cy.get('#' + name).click();
  }

  public static configureAdapter(configs: UserInput[]) {
    cy.wait(2000);
    StaticPropertyUtils.input(configs);

    // Next Button should not be disabled
    cy.get('button').contains('Next').parent().should('not.be.disabled');

    // Click next
    cy.get('button').contains('Next').parent().click();
  }

  public static configureFormat(adapterConfiguration: GenericAdapterInput) {

    // Select format
    if (adapterConfiguration.format.indexOf('json') !== -1) {
      cy.dataCy('connect-select-json-formats').click();
      if (adapterConfiguration.format.indexOf('object') !== -1) {
        cy.dataCy('single_object').click();
      } else {
        cy.dataCy('array').click();
      }
    } else {
      cy.dataCy(adapterConfiguration.format).click();
    }

    StaticPropertyUtils.input(adapterConfiguration.formatConfiguration);

    // Click next
    cy.dataCy('sp-format-selection-next-button').contains('Next').parent().click();
  }

  public static finishEventSchemaConfiguration() {
    // Click next
    cy.dataCy('sp-connect-schema-editor', { timeout: 10000 }).should('be.visible');
    cy.get('#event-schema-next-button').click();
  }

  public static startStreamAdapter(adapterInput: AdapterInput) {
    ConnectUtils.startAdapter(adapterInput, 'sp-connect-adapter-live-preview');
  }

  public static startSetAdapter(adapterInput: AdapterInput) {
    ConnectUtils.startAdapter(adapterInput, 'sp-connect-adapter-set-success');
  }

  public static startAdapter(adapterInput: AdapterInput, successElement) {
    // Set adapter name
    cy.dataCy('sp-adapter-name').type(adapterInput.adapterName);

    if (adapterInput.storeInDataLake) {
      cy.dataCy('sp-store-in-datalake').children().click();
      cy.dataCy('sp-store-in-datalake-timestamp').click().get('mat-option').contains(adapterInput.timestampProperty).click();
    }

    // Start adapter
    cy.get('#button-startAdapter').click();
    cy.dataCy(successElement, { timeout: 60000 }).should('be.visible');

    // Close adapter preview
    cy.get('button').contains('Close').parent().click();
  }

  public static deleteAdapter() {
    // Delete adapter
    cy.visit('#/connect');

    cy.dataCy('delete-adapter').should('have.length', 1);
    cy.dataCy('delete-adapter').click();
    cy.dataCy('delete-adapter-confirmation').click();
    cy.dataCy('adapter-deletion-in-progress', { timeout: 10000 }).should('be.visible');
    cy.dataCy('delete-adapter', { timeout: 20000 }).should('have.length', 0);
  }

  public static setUpPreprocessingRuleTest(): AdapterInput {
    const adapterConfiguration = GenericAdapterBuilder
        .create('File_Set')
        .setStoreInDataLake()
        .setTimestampProperty('timestamp')
        .setName('Adapter to test rules')
        .setFormat('csv')
        .addFormatInput('input', 'delimiter', ';')
        .addFormatInput('checkbox', 'header', 'check')
        .build();


    ConnectUtils.goToConnect();
    ConnectUtils.goToNewAdapterPage();
    ConnectUtils.selectAdapter(adapterConfiguration.adapterType);
    ConnectUtils.configureAdapter(adapterConfiguration.protocolConfiguration);
    ConnectUtils.configureFormat(adapterConfiguration);

    // wait till schema is shown
    cy.dataCy('sp-connect-schema-editor', { timeout: 60000 }).should('be.visible');

    return adapterConfiguration;
  }

  public static tearDownPreprocessingRuleTest(adapterConfiguration: AdapterInput,
                                              expectedFile: string,
                                              ignoreTime: boolean) {

    ConnectUtils.startSetAdapter(adapterConfiguration);

    // Wait till data is stored
    cy.wait(10000);

    DataLakeUtils.checkResults(
        'Adapter to test rules',
        expectedFile,
        ignoreTime);
  }

}
