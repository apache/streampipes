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
import * as CSV from 'csv-string';

describe('Download audit log', () => {
    const adapterName = 'Audit log download simulator';

    beforeEach('Setup test', () => {
        cy.removeDownloadDirectory();
        cy.initStreamPipesTest();

        ConnectUtils.addMachineDataSimulator(adapterName);

        ConfigurationUtils.goToAuditLogConfiguration();
    });

    const assertLatestAdapterEvents = (
        format: 'csv' | 'json',
        delimiter?: 'comma' | 'semicolon',
    ) => {
        const downloadsFolder = Cypress.config('downloadsFolder');
        cy.readFile(`${downloadsFolder}/audit-log.${format}`).then(
            (content: unknown) => {
                const eventTypes =
                    format === 'json'
                        ? (content as { eventType: string }[]).map(
                              event => event.eventType,
                          )
                        : CSV.parse(
                              content as string,
                              delimiter === 'semicolon' ? ';' : ',',
                          )
                              .slice(1)
                              .map(event => event[1]);

                // Audit exports are ordered newest first.
                expect(eventTypes.slice(0, 2)).to.deep.equal([
                    'sp.adapter.start',
                    'sp.adapter.create',
                ]);
            },
        );
    };

    const downloadAuditLog = (
        format: 'csv' | 'json',
        delimiter?: 'comma' | 'semicolon',
    ) => {
        ConfigurationBtns.auditLogDownloadBtn().should('be.enabled').click();
        ConfigurationBtns.auditDownloadDialog().should('be.visible');

        ConfigurationBtns.auditDownloadNextBtn().click();
        ConfigurationBtns.auditDownloadFormat(format).click();
        if (format === 'csv' && delimiter) {
            ConfigurationBtns.auditDownloadDelimiter(delimiter).click();
        }
        ConfigurationBtns.auditDownloadBtn().click();

        ConfigurationBtns.auditDownloadDialog().should(
            'contain.text',
            'Download successful',
        );
        assertLatestAdapterEvents(format, delimiter);
    };

    it('shows a confirmation after downloading the audit log as JSON', () => {
        downloadAuditLog('json');
    });

    it('shows a confirmation after downloading the audit log as CSV with commas', () => {
        downloadAuditLog('csv', 'comma');
    });

    it('shows a confirmation after downloading the audit log as CSV with semicolons', () => {
        downloadAuditLog('csv', 'semicolon');
    });
});
