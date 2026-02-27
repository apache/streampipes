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
import { DataExplorerBtns } from '../../support/utils/dataExplorer/DataExplorerBtns';
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

        DataExplorerBtns.advancedFilterBtn().click();

        // Root condition: randomtext = a
        DataExplorerBtns.advancedFilterAddConditionBtn().first().click();
        // Root nested group
        DataExplorerBtns.advancedFilterAddGroupBtn().first().click();
        // Two conditions in nested group
        DataExplorerBtns.advancedFilterAddConditionBtn().last().click();
        DataExplorerBtns.advancedFilterAddConditionBtn().last().click();

        // Set nested group operator to OR
        DataExplorerBtns.advancedFilterGroupOperator()
            .last()
            .click({ force: true });
        DataExplorerBtns.matOptionByText(/^OR$/).click();

        setAdvancedCondition(0, 'randomtext', '=', 'a');
        setAdvancedCondition(1, 'randomnumber', '=', '22');
        setAdvancedCondition(2, 'randomnumber', '=', '56');

        // Value inputs update the model on change; blur the active input before checking the preview.
        cy.focused().blur();

        DataExplorerBtns.advancedFilterPreviewBanner()
            .should('contain.text', 'randomtext = a')
            .and('contain.text', 'randomnumber = 22')
            .and('contain.text', 'randomnumber = 56')
            .and('contain.text', 'OR');

        DataExplorerBtns.advancedFilterApplyBtn().click();

        // a AND (22 OR 56) => 2 rows in sample.csv
        DataExplorerWidgetTableUtils.checkAmountOfRows(2);

        DataExplorerUtils.saveAndReEditWidget('AdvancedFilterWidget');
        DataExplorerWidgetTableUtils.checkAmountOfRows(2);

        DataExplorerUtils.selectDataConfig();
        DataExplorerBtns.advancedFilterBtn().should('be.visible');
        DataExplorerBtns.filterAlertBanner()
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
    DataExplorerBtns.filterFieldSelect().eq(index).click({ force: true });
    DataExplorerBtns.matOptionByText(field).click();

    DataExplorerBtns.filterOperatorSelect().eq(index).click({ force: true });
    DataExplorerBtns.matOptionByText(operator).click();

    DataExplorerBtns.filterValueInput()
        .eq(index)
        .clear({ force: true })
        .type(value, { force: true });
}
