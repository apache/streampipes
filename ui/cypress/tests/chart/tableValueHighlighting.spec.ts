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

import { ChartWidget } from '../../support/model/ChartWidget';
import { PrepareTestDataUtils } from '../../support/utils/PrepareTestDataUtils';
import { ChartBtns } from '../../support/utils/chart/ChartBtns';
import { ChartUtils } from '../../support/utils/chart/ChartUtils';
import { ChartWidgetTableUtils } from '../../support/utils/chart/ChartWidgetTableUtils';

describe('Table value highlighting', () => {
    const highlightRow = (field: string) =>
        cy.contains('.highlight-color-row', field);
    const rangeControls = (field: string) =>
        highlightRow(field).nextUntil('.highlight-color-row');
    const boundInput = (field: string, bound: 'Min' | 'Max') =>
        rangeControls(field).find(`input[aria-label="${bound}"]`);

    function selectRangeMode(field: string, mode: 'Automatic' | 'Custom') {
        rangeControls(field).find('mat-select').click();
        ChartBtns.matOptionByText(mode).click();
    }

    function setCustomRange(field: string, min: number, max: number) {
        selectRangeMode(field, 'Custom');
        boundInput(field, 'Max').clear().type(String(max));
        boundInput(field, 'Min').clear().type(String(min));
    }

    function checkIntensity(field: string, value: number, percent: number) {
        cy.dataCy(`data-explorer-table-row-${field}`).should($cells => {
            const cell = $cells
                .toArray()
                .find(element => element.textContent.trim() === String(value));
            expect(cell?.style.background, `${field}=${value}`).to.contain(
                ` ${percent}%,`,
            );
        });
    }

    beforeEach(() => {
        ChartUtils.initDataLakeTests();
    });

    it('Applies per-column ranges, rejects invalid bounds and restores automatic scaling', () => {
        ChartUtils.createChart(
            PrepareTestDataUtils.dataName,
            ChartWidget.TABLE,
        );
        ChartWidgetTableUtils.checkAmountOfRows(10);
        highlightRow('randomnumber').find('mat-checkbox').click();
        highlightRow('count').find('mat-checkbox').click();

        setCustomRange('randomnumber', 30, 70);
        setCustomRange('count', 120, 140);
        checkIntensity('randomnumber', 22, 0);
        checkIntensity('randomnumber', 62, 80);
        checkIntensity('randomnumber', 95, 100);
        checkIntensity('count', 122, 10);

        boundInput('randomnumber', 'Max').clear().type('1');
        rangeControls('randomnumber')
            .filter('sp-alert-banner')
            .should('be.visible');
        checkIntensity('randomnumber', 62, 80);

        boundInput('randomnumber', 'Max').clear().type('80');
        rangeControls('randomnumber')
            .filter('sp-alert-banner')
            .should('not.exist');
        checkIntensity('randomnumber', 62, 64);
        checkIntensity('count', 122, 10);

        selectRangeMode('randomnumber', 'Automatic');
        boundInput('randomnumber', 'Min').should('not.exist');
        checkIntensity('randomnumber', 62, 55);
        checkIntensity('count', 122, 10);
    });
});
