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
import {
    subDays,
    subHours,
    subMinutes,
    subMonths,
    subWeeks,
    subYears,
} from 'date-fns';

describe('Test Time Range Selectors in Data Explorer', () => {
    const periods = [
        { selector: 1, start: (now: Date) => subMinutes(now, 15) },
        { selector: 2, start: (now: Date) => subHours(now, 1) },
        { selector: 4, start: (now: Date) => subDays(now, 1) },
        { selector: 6, start: (now: Date) => subWeeks(now, 1) },
        { selector: 8, start: (now: Date) => subMonths(now, 1) },
        { selector: 10, start: (now: Date) => subYears(now, 1) },
    ];

    const timeRangeFrom = 'time-selector-start-time';
    const timeRangeTo = 'time-selector-end-time';
    const dateRangeFrom = 'time-selector-start-date';
    const dateRangeTo = 'time-selector-end-date';

    before('Setup Tests', () => {
        cy.initStreamPipesTest();
        DataLakeUtils.loadDataIntoDataLake('datalake/sample.csv', false);
        DataLakeUtils.goToDatalake();
        DataLakeUtils.createAndEditDataView();
    });

    it('Perform Test', () => {
        periods.forEach(period => {
            cy.log('Testing period: ' + period.selector);
            DataLakeUtils.openTimeSelectorMenu();
            // Choosing time period and saving initial start and end dates
            cy.dataCy(`time-selector-quick-${period.selector}`).click();
            const expectedEndDate = new Date();
            DataLakeUtils.openTimeSelectorMenu();
            // check if dates can differ from the selected dates
            const expectedStartDate = getExpectedStartDate(
                expectedEndDate,
                period.start,
            );
            cy.dataCy(dateRangeFrom).should(
                'have.text',
                getLocalizedDateString(expectedStartDate),
            );
            cy.dataCy(dateRangeTo).should(
                'have.text',
                getLocalizedDateString(expectedEndDate),
            );

            cy.dataCy(timeRangeFrom)
                .invoke('val')
                .then(actualTime => {
                    const expectedDate =
                        getLocalizedTimeString(expectedStartDate);
                    expect(
                        isTimeWithinTolerance(
                            actualTime as string,
                            expectedDate,
                            10,
                        ),
                    ).to.be.true;
                });
            cy.dataCy(timeRangeTo)
                .invoke('val')
                .then(actualTime => {
                    const expectedDate =
                        getLocalizedTimeString(expectedEndDate);
                    expect(
                        isTimeWithinTolerance(
                            actualTime as string,
                            expectedDate,
                            10,
                        ),
                    ).to.be.true;
                });

            DataLakeUtils.applyCustomTimeSelection();
        });
    });
});

function getExpectedStartDate(endDate: Date, startFn: (Date) => Date): Date {
    const startDate = startFn(endDate);
    startDate.setMinutes(
        startDate.getMinutes() + getTimezoneDifference(endDate, startDate),
    );
    return startDate;
}

function getTimezoneDifference(endDate: Date, startDate: Date): number {
    return endDate.getTimezoneOffset() - startDate.getTimezoneOffset();
}

function getLocalizedDateString(date: Date) {
    return date.toLocaleDateString();
}

function getLocalizedTimeString(date: Date) {
    return date.toLocaleTimeString().slice(0, 8);
}

function parseTimeStringToSeconds(timeString: string) {
    const [hours, minutes, seconds] = timeString.split(':').map(Number);
    return hours * 3600 + minutes * 60 + seconds || 0;
}

function isTimeWithinTolerance(
    actualTimeString: string,
    expectedTimeString: string,
    toleranceInSeconds: number,
) {
    const actualTimeInSeconds = parseTimeStringToSeconds(actualTimeString);
    const expectedTimeInSeconds = parseTimeStringToSeconds(expectedTimeString);
    return (
        Math.abs(actualTimeInSeconds - expectedTimeInSeconds) <=
        toleranceInSeconds
    );
}
