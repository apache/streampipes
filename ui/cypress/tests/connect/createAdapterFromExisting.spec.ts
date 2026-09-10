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

import { ConnectUtils } from '../../support/utils/connect/ConnectUtils';
import { ConnectBtns } from '../../support/utils/connect/ConnectBtns';

describe('Test create adapter from existing', () => {
    const sourceAdapter = 'simulator';
    const adapterCopy = 'simulator-copy';
    const waitTime = '2000';

    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
        ConnectUtils.addMachineDataSimulator(sourceAdapter, false, waitTime);
    });

    it('Stores a copy with the prefilled configuration of the source adapter', () => {
        ConnectUtils.createAdapterFromExisting(sourceAdapter);

        ConnectBtns.adapterConfigInput('wait-time-ms').should(
            'have.value',
            waitTime,
        );

        // Asserts the prefilled copy name before renaming and storing
        ConnectUtils.storeAdapterFromExisting(sourceAdapter, adapterCopy);

        ConnectUtils.checkAmountOfAdapters(2);
        ConnectUtils.checkAdapterListed(sourceAdapter);
        ConnectUtils.checkAdapterListed(adapterCopy);
    });
});
