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
import { ChartUtils } from '../../../support/utils/chart/ChartUtils';
import { UserUtils } from '../../../support/utils/UserUtils';
import { UserRole } from '../../../../src/app/core/auth/user-role.enum';
import { User } from '../../../support/model/User';

describe('Filter audit log', () => {
    const adapterName = 'Audit log filter simulator';
    const chartName = 'Audit log filter chart';
    const adapterStartEvent = 'sp.adapter.start';
    const adminEmail = 'admin@streampipes.apache.org';
    let testStartedAt: string;
    let chartOwner: User;

    beforeEach('Open audit log', () => {
        cy.initStreamPipesTest();
        cy.then(() => {
            testStartedAt = new Date().toISOString();
        });
        ConnectUtils.addMachineDataSimulator(adapterName, true);

        chartOwner = UserUtils.createUser(
            'auditLogChartOwner',
            UserRole.ROLE_DATA_EXPLORER_ADMIN,
        );
        cy.login(chartOwner);
        ChartUtils.goToDataset();
        ChartUtils.createAndEditChart();
        ChartUtils.saveChartConfiguration(false, false, chartName);

        cy.login(UserUtils.adminUser);
        ConfigurationUtils.goToAuditLogConfiguration();
    });

    it('filters events by time range', () => {
        ConfigurationBtns.auditTimeRangeFilter().click();
        ConfigurationBtns.auditTimeRangeOption(7).click();

        ConfigurationBtns.auditApplyFiltersBtn()
            .should('not.be.disabled')
            .click();
        //adapter.create, adapter.start, auth.login, auth.login, pipeline.create, pipeline.start, dataset.create, chart.create
        ConfigurationBtns.auditLogRowsRecordedAfterShouldHaveLength(
            testStartedAt,
            8,
        );
    });

    it('filters events by type', () => {
        ConfigurationBtns.auditEventTypeFilter()
            .click()
            .type(`${adapterStartEvent}{enter}`);

        ConfigurationBtns.auditApplyFiltersBtn()
            .should('not.be.disabled')
            .click();
        //adapter.start
        ConfigurationBtns.auditLogRowsRecordedAfterShouldHaveLength(
            testStartedAt,
            1,
        );
    });

    it('filters events by actor', () => {
        ConfigurationBtns.auditActorFilter()
            .click()
            .type(`${adminEmail}{enter}`);

        ConfigurationBtns.auditApplyFiltersBtn()
            .should('not.be.disabled')
            .click();
        //adapter.create, adapter.start, auth.login, auth.logout, pipeline.create, pipeline.start, dataset.create
        ConfigurationBtns.auditLogRowsRecordedAfterShouldHaveLength(
            testStartedAt,
            7,
        );
    });

    it('filters events by outcome', () => {
        ConfigurationBtns.auditOutcomeFilter().click().type('succeeded{enter}');

        ConfigurationBtns.auditApplyFiltersBtn()
            .should('not.be.disabled')
            .click();
        //adapter.create, adapter.start, auth.login, auth.login, pipeline.create, pipeline.start, dataset.create, chart.create
        ConfigurationBtns.auditLogRowsRecordedAfterShouldHaveLength(
            testStartedAt,
            8,
        );
    });
});
