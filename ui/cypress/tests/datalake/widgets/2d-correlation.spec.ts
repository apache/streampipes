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

describe('Test 2d Correlation View in Data Explorer', () => {
    beforeEach('Setup Test', () => {
        DataLakeUtils.initDataLakeTests();
    });

    it('Perform Test', () => {
        DataLakeUtils.addDataViewAndWidget('view', 'Persist', '2D Correlation');

        // Check if scatter plot is displayed
        cy.get('g').should('have.class', 'scatterlayer mlayer');
        cy.get('g.points', { timeout: 10000 })
            .children()
            .should('have.length', 10);

        // Change from line plot to scatter plot
        DataLakeUtils.selectVisualizationConfig();
        cy.get('div').contains('Scatter').click();
        cy.get('div').contains('Density').click();

        // Check if scatter plot is displayed
        cy.get('g').should('have.class', 'contourlayer mlayer');
    });
});
