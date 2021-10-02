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

export class AdapterUtils {

  public static testSpecificStreamAdapter(adapterConfiguration: SpecificAdapterInput) {

    AdapterUtils.goToConnect();

    AdapterUtils.selectAdapter(adapterConfiguration.adapterType);

    AdapterUtils.configureAdapter(adapterConfiguration.adapterConfiguration);

    if (adapterConfiguration.timestampProperty) {
      AdapterUtils.markPropertyAsTimestamp(adapterConfiguration.timestampProperty);
    }

    AdapterUtils.finishEventSchemaConfiguration();

    AdapterUtils.startStreamAdapter(adapterConfiguration);

  }

  public static testGenericStreamAdapter(adapterConfiguration: GenericAdapterInput) {

    AdapterUtils.addGenericStreamAdapter(adapterConfiguration);

  }


  public static addGenericStreamAdapter(adapterConfiguration: GenericAdapterInput) {
    AdapterUtils.addGenericAdapter(adapterConfiguration);

    AdapterUtils.startStreamAdapter(adapterConfiguration);
  }

  public static addGenericSetAdapter(adapterConfiguration: GenericAdapterInput) {
    AdapterUtils.addGenericAdapter(adapterConfiguration);

    AdapterUtils.startSetAdapter(adapterConfiguration);
  }

  private static addGenericAdapter(adapterConfiguration: GenericAdapterInput) {
    AdapterUtils.goToConnect();

    AdapterUtils.selectAdapter(adapterConfiguration.adapterType);

    AdapterUtils.configureAdapter(adapterConfiguration.protocolConfiguration);

    AdapterUtils.configureFormat(adapterConfiguration);

    AdapterUtils.markPropertyAsTimestamp(adapterConfiguration.timestampProperty);

    AdapterUtils.finishEventSchemaConfiguration();
  }

  public static addMachineDataSimulator(name: string) {

    const configuration = SpecificAdapterBuilder
      .create('Machine_Data_Simulator')
      .setName(name)
      .addInput('input', 'wait-time-ms', '1000')
      .build();

    AdapterUtils.goToConnect();

    AdapterUtils.selectAdapter(configuration.adapterType);

    AdapterUtils.configureAdapter(configuration.adapterConfiguration);

    AdapterUtils.finishEventSchemaConfiguration();

    AdapterUtils.startStreamAdapter(configuration);

  }

  public static goToConnect() {
    cy.visit('#/connect');
  }

  private static selectAdapter(name) {
    // Select adapter
    cy.get('#' + name).click();
  }

  private static configureAdapter(configs: UserInput[]) {

    StaticPropertyUtils.input(configs);

    // Next Button should not be disabled
    cy.get('button').contains('Next').parent().should('not.be.disabled');

    // Click next
    cy.get('button').contains('Next').parent().click();
  }

  private static configureFormat(adapterConfiguration: GenericAdapterInput) {
    // Select format
    cy.dataCy(adapterConfiguration.format).click();

    StaticPropertyUtils.input(adapterConfiguration.formatConfiguration);

    // Click next
    cy.dataCy('sp-format-selection-next-button').contains('Next').parent().click();
  }

  private static markPropertyAsTimestamp(propertyName: string) {
    // Mark property as timestamp
    cy.get('#event-schema-next-button').should('be.disabled');
    // Edit timestamp
    cy.dataCy('edit-' + propertyName).click();

    // Mark as timestamp
    cy.dataCy('sp-mark-as-timestamp').children().click();

    // Close
    cy.dataCy('sp-save-edit-property').click();

    cy.get('#event-schema-next-button').parent().should('not.be.disabled');
  }

  private static finishEventSchemaConfiguration() {
    // Click next
    cy.dataCy('sp-connect-schema-editor', { timeout: 10000 }).should('be.visible');
    cy.get('#event-schema-next-button').click();
  }

  private static startStreamAdapter(adapterInput: AdapterInput) {
    AdapterUtils.startAdapter(adapterInput, 'sp-connect-adapter-live-preview');
  }

  private static startSetAdapter(adapterInput: AdapterInput) {
    AdapterUtils.startAdapter(adapterInput, 'sp-connect-adapter-set-success');
  }

  private static startAdapter(adapterInput: AdapterInput, successElement) {
    // Set adapter name
    cy.dataCy('sp-adapter-name').type(adapterInput.adapterName);

    if (adapterInput.storeInDataLake) {
      cy.dataCy('sp-store-in-datalake').children().click();
      cy.dataCy('sp-store-in-datalake-timestamp').click().get('mat-option').contains(adapterInput.timestampProperty).click();
    }

    // Start adapter
    cy.get('#button-startAdapter').click();
    cy.dataCy(successElement, { timeout: 10000 }).should('be.visible');

    // Close adapter preview
    cy.get('button').contains('Close').parent().click();
  }

  public static deleteAdapter() {
    // Delete adapter
    cy.visit('#/connect');

    cy.get('div').contains('My Adapters').parent().click();
    cy.dataCy('delete').should('have.length', 1);
    cy.dataCy('delete').click();
    cy.dataCy('delete-adapter').click();
    cy.dataCy('adapter-deletion-in-progress', { timeout: 10000 }).should('be.visible');
    cy.dataCy('delete', { timeout: 20000 }).should('have.length', 0);
    // });
  }
}
