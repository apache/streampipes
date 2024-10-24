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

import { ConnectUtils } from '../../../support/utils/connect/ConnectUtils';
import { CompactAdapterUtils } from '../../../support/utils/connect/CompactAdapterUtils';
import { ConnectBtns } from '../../../support/utils/connect/ConnectBtns';

describe('Add Compact Adapters', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
    });

    it('Add an adapter and rename a property', () => {
        const newPropertyName = 'temperature_renamed';
        const compactAdapter = CompactAdapterUtils.getMachineDataSimulator()
            .withRename('temperature', newPropertyName)
            .setStart()
            .build();

        CompactAdapterUtils.storeCompactAdapter(compactAdapter).then(() => {
            const runtimeNames = [
                'density',
                'mass_flow',
                'sensor_fault_flags',
                'sensorId',
                newPropertyName,
                'timestamp',
                'volume_flow',
            ];

            ConnectUtils.validateEventSchema(runtimeNames);
        });
    });

    it('Add an adapter and change measurement unit', () => {
        const compactAdapter = CompactAdapterUtils.getMachineDataSimulator()
            .withMeasurementUnit(
                'temperature',
                'http://qudt.org/vocab/unit#DegreeFahrenheit',
            )
            .setStart()
            .build();

        CompactAdapterUtils.storeCompactAdapter(compactAdapter).then(() => {
            ConnectUtils.goToConnect();
            ConnectBtns.detailsAdapter().click();

            // This assertion works because the original value is below 100
            // with the transformation the value is above 100
            ConnectUtils.getLivePreviewValue('temperature')
                .invoke('text')
                .then(text => {
                    const value = parseFloat(text.trim());
                    expect(value).to.be.greaterThan(100);
                });
        });
    });
});
