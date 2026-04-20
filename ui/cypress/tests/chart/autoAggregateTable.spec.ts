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
import { ChartWidgetTableUtils } from '../../support/utils/chart/ChartWidgetTableUtils';
import { DataLakeSeedUtils } from '../../support/utils/dataset/DataLakeSeedUtils';

describe('Test auto aggregate table result size', () => {
    const eventCount = 10050;
    const maximumAmountOfEvents = 10000;
    const expectedAutoAggregatedRows = 2001;

    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
        DataLakeSeedUtils.importJsonArrayRecords({
            records: makeAutoAggregateRecords(
                eventCount,
                maximumAmountOfEvents,
            ),
            measurementName: ChartUtils.ADAPTER_NAME,
            timestampColumn: 'timestamp',
        });
    });

    it('Aggregates large one-second series to the client limit', () => {
        cy.intercept(
            'GET',
            '**/streampipes-backend/api/v4/datalake/measurements/datalake_configuration*',
            req => {
                if (req.query.autoAggregate === 'true') {
                    req.alias = 'autoAggregateQuery';
                }
            },
        );

        ChartUtils.addDataViewAndTableWidget(
            'Auto aggregate table',
            ChartUtils.ADAPTER_NAME,
        );
        ChartUtils.selectDataConfig();
        ChartUtils.selectAggregatedQueryType();
        ChartUtils.enableAutoAggregate();

        cy.wait('@autoAggregateQuery').then(({ request, response }) => {
            const query = request.query as Record<string, string>;
            expect(query.autoAggregate).to.equal('true');
            expect(query).not.to.have.property('maximumAmountOfEvents');
            expect(response?.body.total).to.equal(expectedAutoAggregatedRows);
        });

        ChartWidgetTableUtils.checkAmountOfRows(20);
        ChartWidgetTableUtils.checkTotalAmountOfRows(
            expectedAutoAggregatedRows,
        );
    });
});

function makeAutoAggregateRecords(
    totalEvents: number,
    maxAmountOfEvents: number,
) {
    const eventIntervalMs = 1000;
    const coveredRange = (totalEvents - 1) * eventIntervalMs + 1;
    const aggregationInterval = Math.max(
        1,
        ceilDiv(coveredRange, maxAmountOfEvents),
    );
    const baseTimestamp =
        Math.floor(Date.UTC(2025, 0, 1, 0, 0, 0) / aggregationInterval) *
        aggregationInterval;

    return Array.from({ length: totalEvents }, (_, index) => ({
        timestamp: baseTimestamp + index * eventIntervalMs,
        density: Number((40 + (index % 25) * 0.1).toFixed(3)),
        mass_flow: Number((5 + (index % 40) * 0.01).toFixed(3)),
        sensor_fault_flags: index % 1200 === 0,
        sensorId: `flowrate-${String((index % 4) + 1).padStart(2, '0')}`,
        temperature: Number((65 + (index % 30) * 0.2).toFixed(3)),
        volume_flow: Number((5.5 + (index % 50) * 0.015).toFixed(3)),
    }));
}

function ceilDiv(dividend: number, divisor: number) {
    return Math.floor((dividend + divisor - 1) / divisor);
}
