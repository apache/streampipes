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

import { DashboardUtils } from '../../../support/utils/DashboardUtils';
import { DataLakeUtils } from '../../../support/utils/datalake/DataLakeUtils';

describe('Validate StreamPipes after restart', () => {
    beforeEach('Setup Test', () => {
        cy.login();
    });

    it('Perform Test', () => {
        // Truncate data in db
        DataLakeUtils.goToDatalakeConfiguration();
        cy.dataCy('datalake-truncate-btn').should('be.visible').click();
        cy.dataCy('confirm-truncate-data-btn', { timeout: 10000 })
            .should('be.visible')
            .click();

        // open dashboard
        DashboardUtils.goToDashboard();
        DashboardUtils.showDashboard('testDashboard');

        cy.wait(6000);

        // validate that data is coming
        DashboardUtils.validateRawWidgetEvents(3);
    });
});
