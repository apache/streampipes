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

describe('Test Map View in Data Explorer', () => {
    beforeEach('Setup Test', () => {
        DataLakeUtils.initDataLakeTests();
    });

    it('Perform Test', () => {
        DataLakeUtils.addDataViewAndWidget('view', 'Persist', 'Map');

        // Change marker positions
        DataLakeUtils.selectVisualizationConfig();
        cy.dataCy('data-view-map-select-latitude')
            .click()
            .get('mat-option')
            .contains('randomnumber (prepared_data #1)')
            .click();
        cy.dataCy('data-view-map-select-longitude')
            .click()
            .get('mat-option')
            .contains('randomnumber (prepared_data #1)')
            .click();

        // Check if map with markers is visible
        cy.get('sp-data-explorer-map-widget').should('be.visible');
        cy.get('img[alt=Marker]').should(
            'have.attr',
            'src',
            'assets/img/marker-icon.png',
        );

        // Change from markers to trace
        DataLakeUtils.selectVisualizationConfig();
        cy.dataCy('data-view-map-select-marker-or-trace')
            .click()
            .get('mat-option')
            .contains('Trace')
            .click();

        // Check if trace is visible
        cy.get('path').should('have.class', 'leaflet-interactive');
    });
});
