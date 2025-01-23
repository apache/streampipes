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

import { AdapterBuilder } from '../../support/builder/AdapterBuilder';
import { ConnectUtils } from '../../support/utils/connect/ConnectUtils';
import { ConnectBtns } from '../../support/utils/connect/ConnectBtns';

describe('Test Adapter Transformation Rules are properly stored', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
    });

    it('Test configuration of adapter fields ', () => {
        // Set up new adapter
        const builder = AdapterBuilder.create('Machine_Data_Simulator')
            .setName('Machine_Data_Simulator')
            .addInput('input', 'wait-time-ms', '1000');
        const configuration = builder.build();
        ConnectUtils.goToConnect();
        ConnectUtils.goToNewAdapterPage();
        ConnectUtils.selectAdapter(configuration.adapterType);
        cy.contains('Next').click();

        cy.dataCy('sp-event-schema-next-button').click();
        cy.dataCy('sp-adapter-name').type('Test Adapter');
        cy.dataCy('connect-remove-duplicates-box').click();
        cy.dataCy('connect-remove-duplicates-input').type('10000');
        cy.dataCy('connect-reduce-event-rate-box').click();
        cy.dataCy('connect-reduce-event-input').type('20000');
        cy.dataCy('adapter-settings-start-adapter-btn').click();
        ConnectUtils.closeAdapterPreview();

        // Edit adapter and check if given values and added property still provided
        ConnectBtns.editAdapter().should('not.be.disabled');
        ConnectBtns.editAdapter().click();
        cy.contains('Next').click();
        cy.dataCy('sp-event-schema-next-button').click();

        cy.dataCy('connect-remove-duplicates-box')
            .find('input')
            .should('be.checked');
        cy.dataCy('connect-remove-duplicates-input').should(
            'have.value',
            '10000',
        );

        cy.dataCy('connect-reduce-event-rate-box')
            .find('input')
            .should('be.checked');
        cy.dataCy('connect-reduce-event-input').should('have.value', '20000');
    });
});
