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

import { DataLakeUtils } from '../../../support/utils/datalake/DataLakeUtils';

describe('Test Indicator View in Data Explorer', () => {
    beforeEach('Setup Test', () => {
        DataLakeUtils.initDataLakeTests();
    });

    it('Perform Test', () => {
        DataLakeUtils.addDataViewAndWidget('view', 'Persist', 'Indicator');

        // Check checkbox
        DataLakeUtils.selectVisualizationConfig();
        cy.get('mat-checkbox input').click({ force: true });
        cy.dataCy('data-explorer-select-delta-field')
            .click()
            .get('mat-option')
            .contains('count')
            .click();

        // Check if indicator is displayed
        cy.get('g').should('have.class', 'indicatorlayer');
        // Check if delta is displayed
        cy.get('text').should('have.class', 'delta');
    });
});
