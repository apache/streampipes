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
export class ConfigurationBtns {
    public static generalConfigAppNameInput() {
        return cy.dataCy('general-config-app-name');
    }

    public static generalConfigHostnameInput() {
        return cy.dataCy('general-config-hostname');
    }

    public static generalConfigPortInput() {
        return cy.dataCy('general-config-port');
    }

    public static generalConfigSaveBtn() {
        return cy.dataCy('sp-element-general-config-save');
    }

    public static systemNotificationEnabledCheckbox() {
        return cy
            .dataCy('system-notification-enabled')
            .find('input[type="checkbox"]');
    }

    public static systemNotificationMessageInput() {
        return cy.dataCy('system-notification-message');
    }

    public static systemNotification() {
        return cy.dataCy('system-notification');
    }

    public static newUserGroupBtn() {
        return cy.dataCy('new-user-group-btn');
    }

    public static authorizedGroupsLabel() {
        return cy.dataCy('authorized-groups-label');
    }

    public static inputGroupName(groupName: string) {
        return cy.dataCy('group-name-input').type(groupName);
    }

    public static auditLogDownloadBtn() {
        return cy.dataCy('audit-log-download-button');
    }

    public static auditTimeRangeFilter() {
        return cy.dataCy('audit-time-range-filter');
    }

    public static auditTimeRangeOption(days: 1 | 7 | 30) {
        return cy.dataCy(`audit-time-range-${days}`);
    }

    public static auditEventTypeFilter() {
        return cy.dataCy('audit-event-type-filter');
    }

    public static auditActorFilter() {
        return cy.dataCy('audit-actor-filter');
    }

    public static auditOutcomeFilter() {
        return cy.dataCy('audit-outcome-filter');
    }

    public static auditApplyFiltersBtn() {
        return cy.dataCy('audit-apply-filters-button');
    }

    public static auditLogRows() {
        return cy.dataCy('audit-log-table').find('tbody tr');
    }

    public static auditLogRowsRecordedAfterShouldHaveLength(
        timestamp: string,
        expectedLength: number,
    ) {
        const start = Date.parse(timestamp);
        return this.auditLogRows().should(rows => {
            const rowsRecordedAfter = rows.toArray().filter(row => {
                const storedAt = row.querySelector('time')?.dateTime;
                return storedAt && Date.parse(storedAt) > start;
            });

            expect(rowsRecordedAfter).to.have.length(expectedLength);
        });
    }

    public static auditDownloadDialog() {
        return cy.dataCy('audit-download-dialog');
    }

    public static auditDownloadNextBtn() {
        return cy.dataCy('audit-download-next-button');
    }

    public static auditDownloadBtn() {
        return cy.dataCy('audit-download-download-button');
    }

    public static auditDownloadFormat(format: 'csv' | 'json' | 'xlsx') {
        return cy.dataCy(`audit-download-format-${format}`).find('.mdc-radio');
    }

    public static auditDownloadDelimiter(delimiter: 'comma' | 'semicolon') {
        return cy
            .dataCy(`audit-download-delimiter-${delimiter}`)
            .find('.mdc-radio');
    }
}
