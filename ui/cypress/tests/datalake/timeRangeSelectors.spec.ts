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

describe('Test Time Range Selectors in Data Explorer', () => {
    const periods = [
        '15_min',
        '1_hour',
        '1_day',
        '1_week',
        '1_month',
        '1_year',
    ];

    const dateAttribute = 'ng-reflect-date';
    const timeRangeFrom = 'time-range-from';
    const timeRangeTo = 'time-range-to';

    before('Setup Tests', () => {
        cy.initStreamPipesTest();
        DataLakeUtils.loadDataIntoDataLake('datalake/sample.csv', false);
        DataLakeUtils.goToDatalake();
        DataLakeUtils.createAndEditDataView('TestView');
    });

    it('Perform Test', () => {
        periods.forEach(period => {
            cy.log('Testing period: ' + period);
            cy.dataCy(period).click();
            cy.dataCy(timeRangeTo)
                .invoke('attr', dateAttribute)
                .then(initialEndDateString => {
                    cy.dataCy(timeRangeFrom)
                        .invoke('attr', dateAttribute)
                        .then(initialStartDateString => {
                            const initialStartDate = new Date(
                                Date.parse(initialStartDateString),
                            );
                            const initialEndDate = new Date(
                                Date.parse(initialEndDateString),
                            );

                            cy.wrap(initialStartDate).should(
                                'deep.eq',
                                getExpectedStartDate(initialEndDate, period),
                            );

                            cy.dataCy('decrease-time-button').click({
                                force: true,
                            });
                            cy.dataCy(timeRangeTo)
                                .invoke('attr', dateAttribute)
                                .then(updatedEndDateString => {
                                    cy.dataCy(timeRangeFrom)
                                        .invoke('attr', dateAttribute)
                                        .then(updatedStartDateString => {
                                            const updatedStartDate = new Date(
                                                Date.parse(
                                                    updatedStartDateString,
                                                ),
                                            );
                                            const updatedEndDate = new Date(
                                                Date.parse(
                                                    updatedEndDateString,
                                                ),
                                            );

                                            cy.wrap(updatedStartDate).should(
                                                'deep.eq',
                                                getExpectedStartDate(
                                                    updatedEndDate,
                                                    period,
                                                ),
                                            );
                                            cy.wrap(updatedEndDate).should(
                                                'deep.eq',
                                                initialStartDate,
                                            );
                                        });
                                });

                            cy.dataCy('increase-time-button').click({
                                force: true,
                            });
                            cy.dataCy(timeRangeFrom)
                                .invoke('attr', dateAttribute)
                                .should('eq', initialStartDateString);
                            cy.dataCy(timeRangeTo)
                                .invoke('attr', dateAttribute)
                                .should('eq', initialEndDateString);
                        });
                });
        });
    });
});

function getExpectedStartDate(endDate: Date, period: string): Date {
    let startDate = new Date(endDate);

    switch (period) {
        case '15_min':
            startDate.setMinutes(startDate.getMinutes() - 15);
            break;
        case '1_hour':
            startDate.setHours(startDate.getHours() - 1);
            break;
        case '1_day':
            startDate.setDate(startDate.getDate() - 1);
            break;
        case '1_week':
            startDate.setDate(startDate.getDate() - 7);
            break;
        case '1_month':
            startDate.setDate(startDate.getDate() - 30);
            break;
        case '1_year':
            startDate.setDate(startDate.getDate() - 365);
            break;
    }
    return startDate;
}
