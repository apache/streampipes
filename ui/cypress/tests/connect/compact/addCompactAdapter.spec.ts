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

import { ConnectUtils } from '../../../support/utils/connect/ConnectUtils';
import { CompactAdapterUtils } from '../../../support/utils/connect/CompactAdapterUtils';
import { PipelineUtils } from '../../../support/utils/pipeline/PipelineUtils';

describe('Add Compact Adapters', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
    });

    it('Add an adapter via the compact API. Do not start', () => {
        const compactAdapter =
            CompactAdapterUtils.getMachineDataSimulator().build();

        CompactAdapterUtils.storeCompactAdapter(compactAdapter).then(() => {
            ConnectUtils.validateAdapterIsStopped();

            PipelineUtils.checkAmountOfPipelinesPipeline(0);
        });
    });

    it('Add an adapter via the compact API. Start Adapter', () => {
        const compactAdapter = CompactAdapterUtils.getMachineDataSimulator()
            .setStart()
            .build();

        CompactAdapterUtils.storeCompactAdapter(compactAdapter).then(() => {
            ConnectUtils.validateAdapterIsRunning();

            PipelineUtils.checkAmountOfPipelinesPipeline(0);
        });
    });

    it('Add an adapter via the compact API. Start Adapter and start persist pipeline', () => {
        const compactAdapter = CompactAdapterUtils.getMachineDataSimulator()
            .setStart()
            .setPersist()
            .build();

        CompactAdapterUtils.storeCompactAdapter(compactAdapter).then(() => {
            ConnectUtils.validateAdapterIsRunning();

            PipelineUtils.checkAmountOfPipelinesPipeline(1);
        });
    });

    it('Add an adapter via the compact API via yml API.', () => {
        cy.readFile(
            'cypress/fixtures/connect/compact/machineDataSimulator.yml',
        ).then(ymlDescription => {
            CompactAdapterUtils.storeCompactYmlAdapter(ymlDescription).then(
                () => {
                    ConnectUtils.validateAdapterIsStopped();
                },
            );
        });
    });

    it('Ensure correct error code when adapter with the same id already exists', () => {
        const compactAdapter = CompactAdapterUtils.getMachineDataSimulator()
            .setStart()
            .setPersist()
            .build();

        CompactAdapterUtils.storeCompactAdapter(compactAdapter).then(
            response => {
                expect(response.status).to.equal(200);
                ConnectUtils.checkAmountOfAdapters(1);

                // Store the same adapter a second time and validate that resource returns status of conflict
                CompactAdapterUtils.storeCompactAdapter(
                    compactAdapter,
                    false,
                ).then(response => {
                    expect(response.status).to.equal(409);

                    ConnectUtils.checkAmountOfAdapters(1);
                });
            },
        );
    });
});
