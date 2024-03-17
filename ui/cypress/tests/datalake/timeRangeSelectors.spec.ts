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
        { selector: '15_min', offset: 15 },
        { selector: '1_hour', offset: 60 },
        { selector: '1_day', offset: 1440 },
        { selector: '1_week', offset: 10080 },
        { selector: '1_month', offset: 43200 },
        { selector: '1_year', offset: 525600 },
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
            cy.log('Testing period: ' + period.selector);
            // Choosing time period and saving initial start and end dates
            cy.dataCy(period.selector).click();
            cy.dataCy(timeRangeTo)
                .invoke('attr', dateAttribute)
                .then(initialEndDateString => {
                    cy.dataCy(timeRangeFrom)
                        .invoke('attr', dateAttribute)
                        .then(initialStartDateString => {
                            const initialStartDate = parseDate(
                                initialStartDateString,
                            );
                            const initialEndDate =
                                parseDate(initialEndDateString);

                            cy.wrap(initialStartDate).should(
                                'deep.eq',
                                getExpectedStartDate(
                                    initialEndDate,
                                    period.offset,
                                ),
                            );
                            // Updating time range to previous one and checking start and end dates
                            cy.dataCy('decrease-time-button').click({
                                force: true,
                            });
                            cy.dataCy(timeRangeTo)
                                .invoke('attr', dateAttribute)
                                .then(updatedEndDateString => {
                                    cy.dataCy(timeRangeFrom)
                                        .invoke('attr', dateAttribute)
                                        .then(updatedStartDateString => {
                                            const updatedStartDate = parseDate(
                                                updatedStartDateString,
                                            );
                                            const updatedEndDate =
                                                parseDate(updatedEndDateString);

                                            cy.wrap(updatedStartDate).should(
                                                'deep.eq',
                                                getExpectedStartDate(
                                                    updatedEndDate,
                                                    period.offset,
                                                ),
                                            );
                                            cy.wrap(updatedEndDate).should(
                                                'deep.eq',
                                                initialStartDate,
                                            );
                                        });
                                });
                            // Updating time range to the next one and comparing start and end dates with initial values
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

function getExpectedStartDate(endDate: Date, offset: number): Date {
    const startDate = new Date(endDate.getTime() - offset * 60000);
    startDate.setMinutes(
        startDate.getMinutes() + getTimezoneDifference(endDate, startDate),
    );
    return startDate;
}

function getTimezoneDifference(endDate: Date, startDate: Date): number {
    return endDate.getTimezoneOffset() - startDate.getTimezoneOffset();
}

function parseDate(dateString: string): Date {
    return new Date(Date.parse(dateString));
}
