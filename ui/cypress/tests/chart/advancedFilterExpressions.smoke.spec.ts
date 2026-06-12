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

import { ChartUtils } from '../../support/utils/chart/ChartUtils';
import { ChartBtns } from '../../support/utils/chart/ChartBtns';
import { ChartWidgetTableUtils } from '../../support/utils/chart/ChartWidgetTableUtils';

describe('Advanced Filter Expressions in Charts', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
        ChartUtils.loadDataIntoDataLake('datalake/sample.csv');
    });

    it('Applies nested advanced filter expressions and persists them', () => {
        ChartUtils.addDataViewAndTableWidget(ChartUtils.ADAPTER_NAME);

        ChartWidgetTableUtils.checkAmountOfRows(10);
        ChartUtils.selectDataConfig();

        ChartBtns.advancedFilterBtn().click();

        // Root condition: randomtext = a
        ChartBtns.advancedFilterAddConditionBtn().first().click();
        // Root nested group
        ChartBtns.advancedFilterAddGroupBtn().first().click();
        // Two conditions in nested group
        ChartBtns.advancedFilterAddConditionBtn().last().click();
        ChartBtns.advancedFilterAddConditionBtn().last().click();

        // Set nested group operator to OR
        ChartBtns.advancedFilterGroupOperator().last().click({ force: true });
        ChartBtns.matOptionByText(/^OR$/).click();

        setAdvancedCondition(0, 'randomtext', '=', 'a');
        setAdvancedCondition(1, 'randomnumber', '=', '22');
        setAdvancedCondition(2, 'randomnumber', '=', '56');

        // Value inputs update the model on change; blur the active input before checking the preview.
        cy.focused().blur();

        ChartBtns.advancedFilterPreviewBanner()
            .should('contain.text', 'randomtext = a')
            .and('contain.text', 'randomnumber = 22')
            .and('contain.text', 'randomnumber = 56')
            .and('contain.text', 'OR');

        ChartBtns.advancedFilterApplyBtn().click();

        // a AND (22 OR 56) => 2 rows in sample.csv
        ChartWidgetTableUtils.checkAmountOfRows(2);

        ChartUtils.saveAndReEditWidget('AdvancedFilterWidget');
        ChartWidgetTableUtils.checkAmountOfRows(2);
        ChartUtils.selectDataConfig();
        ChartBtns.advancedFilterBtn().should('be.visible');
        ChartBtns.filterAlertBanner()
            .should('be.visible')
            .within(() => {
                cy.contains('randomtext = a');
            });
    });

    it('Closes table filter dropdown with ESC', () => {
        ChartUtils.addDataViewAndTableWidget(ChartUtils.ADAPTER_NAME);

        cy.dataCy('column-filter-trigger-randomtext').click({ force: true });
        cy.get('.column-filter-dropdown').should('be.visible');

        cy.get('body').type('{esc}');
        cy.get('.column-filter-dropdown').should('not.exist');
    });
});

function setAdvancedCondition(
    index: number,
    field: string,
    operator: '=' | '!=' | '<' | '<=' | '>' | '>=',
    value: string,
) {
    ChartBtns.filterFieldSelect().eq(index).click({ force: true });
    ChartBtns.matOptionByText(field).click();

    ChartBtns.filterOperatorSelect().eq(index).click({ force: true });
    ChartBtns.matOptionByText(operator).click();

    ChartBtns.filterValueInput()
        .eq(index)
        .clear({ force: true })
        .type(value, { force: true });
}
