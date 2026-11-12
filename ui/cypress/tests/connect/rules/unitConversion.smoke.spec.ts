/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

import { CompactAdapterUtils } from '../../../support/utils/connect/CompactAdapterUtils';
import { ConnectUtils } from '../../../support/utils/connect/ConnectUtils';

const QUDT1 = 'http://qudt.org/vocab/unit#';
const QUDT2 = 'http://qudt.org/vocab/unit/';
const STREAMPIPES = 'https://streampipes.apache.org/vocabulary/unit/';

// The transformation script runs before the unit conversion, so pinning the
// simulated value makes the converted value deterministic.
const PIN_TEMPERATURE_TO_25_CELSIUS =
    'function transform(event, out, ctx) {\n' +
    '  event.temperature = 25;\n' +
    '  out.collect(event);\n' +
    '}\n';

describe('Adapter unit conversion', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
    });

    it('Converts a property and stores canonical unit identifiers', () => {
        // Source and target unit use the QUDT 2.x and StreamPipes aliases;
        // the adapter stores the canonical identifiers.
        const compactAdapter = CompactAdapterUtils.getMachineDataSimulator()
            .setName('Unit conversion')
            .withScript(PIN_TEMPERATURE_TO_25_CELSIUS)
            .setScriptActive()
            .withMeasurementUnit(
                'temperature',
                `${QUDT2}DEG_C`,
                `${STREAMPIPES}DEG_F`,
            )
            .setStart()
            .build();

        CompactAdapterUtils.storeCompactAdapter(compactAdapter).then(
            response => {
                const adapterId = CompactAdapterUtils.getAdapterId(response);

                CompactAdapterUtils.getStoredEventProperty(
                    adapterId,
                    'temperature',
                ).then(property => {
                    expect(property.measurementUnit).to.equal(
                        `${QUDT1}DegreeFahrenheit`,
                    );
                    expect(
                        property.additionalMetadata.fromMeasurementUnit,
                    ).to.equal(`${QUDT1}DegreeCelsius`);
                });

                // 25 °C = 77 °F
                ConnectUtils.openAdapterDetails(compactAdapter.name);
                ConnectUtils.livePreviewValueShouldBeCloseTo('temperature', 77);
            },
        );
    });

    it('Rejects an adapter with an unknown measurement unit', () => {
        const compactAdapter = CompactAdapterUtils.getMachineDataSimulator()
            .setName('Unknown unit')
            .withMeasurementUnit(
                'temperature',
                `${QUDT1}DegreeCelsius`,
                `${QUDT1}DegreeCelcius`,
            )
            .build();

        CompactAdapterUtils.storeCompactAdapter(compactAdapter, false).then(
            response => {
                expect(response.status).to.equal(400);
                expect(JSON.stringify(response.body)).to.contain(
                    'DegreeCelcius',
                );
            },
        );

        ConnectUtils.checkAmountOfAdapters(0);
    });
});
