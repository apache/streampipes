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

import { ConfigurationBtns } from '../../../support/utils/configuration/ConfigurationBtns';
import { ConfigurationUtils } from '../../../support/utils/configuration/ConfigurationUtils';
import { ConnectUtils } from '../../../support/utils/connect/ConnectUtils';

describe('Filter audit log', () => {
    const adapterName = 'Audit log filter simulator';
    const adapterStartEvent = 'sp.adapter.start';
    const adminEmail = 'admin@streampipes.apache.org';

    beforeEach('Setup test', () => {
        cy.initStreamPipesTest();

        ConnectUtils.addMachineDataSimulator(adapterName, true);

        ConfigurationUtils.goToAuditLogConfiguration();
    });

    it('filters events by time range, type, actor, and outcome', () => {
        ConfigurationBtns.auditTimeRangeFilter().click();
        ConfigurationBtns.auditTimeRangeOption(7).click();
        ConfigurationBtns.auditEventTypeFilter()
            .click()
            .type(`${adapterStartEvent}{enter}`);
        ConfigurationBtns.auditActorFilter()
            .click()
            .type(`${adminEmail}{enter}`);
        ConfigurationBtns.auditOutcomeFilter().click().type('succeeded{enter}');

        ConfigurationBtns.auditApplyFiltersBtn()
            .should('not.be.disabled')
            .click();

        ConfigurationBtns.auditLogRows().should('have.length', 1);
    });
});
