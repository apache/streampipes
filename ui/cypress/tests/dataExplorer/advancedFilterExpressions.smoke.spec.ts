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

import { DataExplorerUtils } from '../../support/utils/dataExplorer/DataExplorerUtils';
import { DataExplorerWidgetTableUtils } from '../../support/utils/dataExplorer/DataExplorerWidgetTableUtils';

describe('Advanced Filter Expressions in Data Explorer', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
        DataExplorerUtils.loadDataIntoDataLake('datalake/sample.csv');
    });

    it('Applies nested advanced filter expressions and persists them', () => {
        DataExplorerUtils.addDataViewAndTableWidget(
            'AdvancedFilterWidget',
            DataExplorerUtils.ADAPTER_NAME,
        );

        DataExplorerWidgetTableUtils.checkAmountOfRows(10);
        DataExplorerUtils.selectDataConfig();

        cy.dataCy('design-panel-data-settings-advanced-filter').click();

        // Root condition: randomtext = a
        cy.dataCy('advanced-filter-add-condition').first().click();
        // Root nested group
        cy.dataCy('advanced-filter-add-group').first().click();
        // Two conditions in nested group
        cy.dataCy('advanced-filter-add-condition').last().click();
        cy.dataCy('advanced-filter-add-condition').last().click();

        // Set nested group operator to OR
        cy.dataCy('advanced-filter-group-operator', {}, true)
            .last()
            .click({ force: true });
        cy.get('mat-option').contains(/^OR$/).click();

        setAdvancedCondition(0, 'randomtext', '=', 'a');
        setAdvancedCondition(1, 'randomnumber', '=', '22');
        setAdvancedCondition(2, 'randomnumber', '=', '56');

        // Value inputs update the model on change; blur the active input before checking the preview.
        cy.focused().blur();

        cy.dataCy('advanced-filter-preview-banner')
            .should('contain.text', 'randomtext = a')
            .and('contain.text', 'randomnumber = 22')
            .and('contain.text', 'randomnumber = 56')
            .and('contain.text', 'OR');

        cy.dataCy('advanced-filter-apply').click();

        // a AND (22 OR 56) => 2 rows in sample.csv
        DataExplorerWidgetTableUtils.checkAmountOfRows(2);

        DataExplorerUtils.saveAndReEditWidget('AdvancedFilterWidget');
        DataExplorerWidgetTableUtils.checkAmountOfRows(2);

        DataExplorerUtils.selectDataConfig();
        cy.dataCy('design-panel-data-settings-advanced-filter').should(
            'be.visible',
        );
        cy.dataCy('filter-alert-banner', { timeout: 2000 })
            .should('be.visible')
            .within(() => {
                cy.contains('randomtext = a');
            });
    });
});

function setAdvancedCondition(
    index: number,
    field: string,
    operator: '=' | '!=' | '<' | '<=' | '>' | '>=',
    value: string,
) {
    cy.dataCy('design-panel-data-settings-filter-field', {}, true)
        .eq(index)
        .click({ force: true });
    cy.get('mat-option').contains(field).click();

    cy.dataCy('design-panel-data-settings-filter-operator', {}, true)
        .eq(index)
        .click({ force: true });
    cy.get('mat-option').contains(operator).click();

    cy.dataCy('design-panel-data-settings-filter-value', {}, true)
        .eq(index)
        .clear({ force: true })
        .type(value, { force: true });
}
