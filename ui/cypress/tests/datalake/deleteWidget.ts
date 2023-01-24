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
import { DataLakeUtils } from '../../support/utils/datalake/DataLakeUtils';

describe('Test Table View in Data Explorer', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
        DataLakeUtils.loadDataIntoDataLake('datalake/sample.csv', false);
    });

    it('Perform Test', () => {
        /**
         * Prepare tests
         */
        DataLakeUtils.addDataViewAndTableWidget('TestView', 'Persist');

        // Check that widget is visible
        cy.dataCy('widget-datalake_configuration', { timeout: 10000 }).should(
            'be.visible',
        );

        // Activate edit mode
        DataLakeUtils.saveAndReEditWidget('TestView');

        // Delete widget
        DataLakeUtils.removeWidget('datalake_configuration');

        // Save dashboard
        DataLakeUtils.saveDataExplorerWidgetConfiguration();

        // Check that widget is gone
        cy.dataCy('widget-datalake_configuration', { timeout: 10000 }).should(
            'not.exist',
        );

        // Delete Dashboard
        // DataLakeUtils.clickStartTab();

        // Check that dashboard is gone

        cy.dataCy('delete-dashboard-TestView', { timeout: 10000 }).should(
            'have.length',
            1,
        );
        cy.dataCy('delete-dashboard-TestView', { timeout: 10000 }).click();
        cy.dataCy('delete-dashboard-TestView', { timeout: 10000 }).should(
            'have.length',
            0,
        );
    });
});
