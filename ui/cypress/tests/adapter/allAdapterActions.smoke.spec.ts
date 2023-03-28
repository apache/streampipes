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

describe('Testing Start/Stop All Adapters', () => {
    beforeEach('Setup Test', () => {
        // To set up test, we are adding 2 stream adapters that can be futher configured
        cy.initStreamPipesTest();
        ConnectUtils.addMachineDataSimulator('simulator-1');
        ConnectUtils.addMachineDataSimulator('simulator-2');
    });

    it('Test start/stop all adapters', () => {
        // Clicking the stop all adapters button
        ConnectBtns.stopAllAdapters().click();
        // Navigating through the stop all adapters dialog box
        ConnectUtils.allAdapterActionsDialog();
        // Clicking the start all adapters button
        ConnectBtns.startAllAdapters().click();
        // Navigating through the start all adapters dialog box
        ConnectUtils.allAdapterActionsDialog();
    });
});
