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

describe('Download audit log', () => {
    const auditEntry = {
        eventId: 'audit-event-1',
        locator: 'audit-event-1',
        storedAt: '2026-10-15T10:00:00.000Z',
        eventType: 'pipeline.started',
        outcome: 'succeeded',
        actor: 'admin',
        resourceId: 'pipeline-1',
    };

    beforeEach('Setup test', () => {
        cy.initStreamPipesTest();
        cy.intercept('GET', '**/admin/audit/status', { enabled: true });
        cy.intercept('GET', '**/admin/audit/event-types', [
            auditEntry.eventType,
        ]);
        cy.intercept('GET', '**/admin/audit/events*', {
            items: [auditEntry],
            nextCursor: null,
        }).as('getAuditEvents');

        ConfigurationUtils.goToAuditLogConfiguration();
        cy.wait('@getAuditEvents');
    });

    it('shows a confirmation after downloading the audit log as JSON', () => {
        ConfigurationBtns.auditLogDownloadBtn().should('be.enabled').click();
        ConfigurationBtns.auditDownloadDialog().should('be.visible');

        ConfigurationBtns.auditDownloadNextBtn().click();
        ConfigurationBtns.auditDownloadFormat('json').click();
        ConfigurationBtns.auditDownloadBtn().click();

        ConfigurationBtns.auditDownloadDialog().should(
            'contain.text',
            'Your audit log is being downloaded.',
        );
    });
});
