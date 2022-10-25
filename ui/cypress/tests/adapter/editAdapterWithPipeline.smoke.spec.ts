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
import { PipelineUtils } from '../../support/utils/PipelineUtils';
import { ConnectBtns } from '../../support/utils/connect/ConnectBtns';

describe('Test Edit Adapter', () => {
    beforeEach('Setup Test', () => {
        // To set up test add a stream adapter that can be configured
        cy.initStreamPipesTest();

        ConnectUtils.addMachineDataSimulator('simulator', true);
    });

    it('Perform Test', () => {
        // ensure edit mode is disabled
        ConnectUtils.goToConnect();
        ConnectBtns.editAdapter().should('be.disabled');

        // stop adapter
        ConnectBtns.stopAdapter().click();
        ConnectBtns.startAdapter().should('be.visible');

        // ensure edit mode is still disabled
        ConnectBtns.editAdapter().should('not.be.disabled');
        ConnectBtns.editAdapter().click();

        cy.dataCy('can-not-edit-adapter-dialog-warning').should('be.visible');
        cy.dataCy('can-not-edit-adapter-dialog-close').click();

        // Delete pipeline
        PipelineUtils.deletePipeline();

        // edit mode is enabled
        ConnectUtils.goToConnect();
        ConnectBtns.editAdapter().should('not.be.disabled');
    });
});
