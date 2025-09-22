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

import { ConnectUtils } from '../../support/utils/connect/ConnectUtils';
import { ConnectBtns } from '../../support/utils/connect/ConnectBtns';
import { CompactAdapterUtils } from '../../support/utils/connect/CompactAdapterUtils';

describe('Adapter Paging Test', () => {
    beforeEach('Setup Test', () => {
        // To set up test, we are adding 2 stream adapters that can be further configured
        //TODO compactAdapter
        cy.initStreamPipesTest();
        //cy.login(); // Comment this in and the line above out to disable a clean setup
        console.log('Add a simulator');
        ConnectUtils.addMachineDataSimulator('simulator-1');

        //Generate 5 Adapters
        //for (let i = 0; i < 5; i++) {
        // const compactAdapter = CompactAdapterUtils.getMachineDataSimulator()
        //    .build();

        //CompactAdapterUtils.storeCompactAdapter(compactAdapter)
        //}

        // One Adapter running for testing purposes

        //const compactAdapter = CompactAdapterUtils.getMachineDataSimulator()
        //    .setStart()
        //    .build();

        //CompactAdapterUtils.storeCompactAdapter(compactAdapter)
    });

    it('Basic Paging check', () => {
        ConnectUtils.goToConnect();
        // set the paging to 5 items

        // calculate list of items on page 1

        // calculate lust of items on page 2
    });
});
