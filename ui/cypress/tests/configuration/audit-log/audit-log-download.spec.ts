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

describe('Download audit log', () => {
    const adapterName = 'Audit log download simulator';

    beforeEach('Setup test', () => {
        cy.initStreamPipesTest();

        ConnectUtils.addMachineDataSimulator(adapterName);

        ConfigurationUtils.goToAuditLogConfiguration();
    });

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
