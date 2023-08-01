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

export class ConnectEventSchemaUtils {
    public static markPropertyAsDimension(propertyName: string) {
        cy.dataCy('property-scope-' + propertyName, { timeout: 10000 })
            .click()
            .get('.mdc-list-item__primary-text')
            .contains('Dimension')
            .click();
    }

    public static markPropertyAsTimestamp(propertyName: string) {
        // Mark property as timestamp
        this.eventSchemaNextBtnDisabled();
        // Edit timestamp

        ConnectEventSchemaUtils.clickEditProperty(propertyName);

        // Mark as timestamp
        cy.dataCy('sp-mark-as-timestamp').children().click();

        // Close
        cy.dataCy('sp-save-edit-property').click();

        this.eventSchemaNextBtnEnabled();
    }

    public static addTimestampProperty() {
        this.eventSchemaNextBtnDisabled();
        cy.dataCy('connect-schema-add-timestamp-btn', {
            timeout: 10000,
        }).click();
        this.eventSchemaNextBtnEnabled();
    }

    public static editTimestampProperty(
        propertyName: string,
        timestampRegex: string,
    ) {
        ConnectEventSchemaUtils.clickEditProperty(propertyName);
        cy.dataCy('sp-mark-as-timestamp').children().click();
        cy.dataCy('connect-timestamp-converter')
            .click()
            .get('mat-option')
            .contains('String')
            .click();
        cy.dataCy('connect-timestamp-string-regex').type(timestampRegex);

        cy.dataCy('sp-save-edit-property').click();

        cy.dataCy('edit-' + propertyName.toLowerCase(), {
            timeout: 10000,
        }).click({ force: true });
        cy.dataCy('connect-timestamp-string-regex', { timeout: 10000 }).should(
            'have.value',
            timestampRegex,
        );
        cy.dataCy('sp-save-edit-property', { timeout: 10000 }).should(
            'have.length',
            1,
        );
        cy.dataCy('sp-save-edit-property').click();
    }

    public static numberTransformation(propertyName: string, value: string) {
        ConnectEventSchemaUtils.clickEditProperty(propertyName);
        // cy.wait(1000);
        cy.dataCy('connect-schema-correction-value').type(value);
        cy.dataCy('connect-schema-correction-operator')
            .click()
            .get('mat-option')
            .contains('Multiply')
            .click();

        cy.dataCy('sp-save-edit-property').click();
        cy.dataCy('edit-' + propertyName.toLowerCase(), {
            timeout: 10000,
        }).click({ force: true });
        cy.dataCy('connect-schema-correction-value', { timeout: 10000 }).should(
            'have.value',
            value,
        );
        cy.dataCy('sp-save-edit-property', { timeout: 10000 }).should(
            'have.length',
            1,
        );
        cy.dataCy('sp-save-edit-property').click();
    }

    public static unitTransformation(
        propertyName: string,
        fromUnit: string,
        toUnit: string,
    ) {
        ConnectEventSchemaUtils.clickEditProperty(propertyName);
        cy.dataCy('connect-schema-unit-from-dropdown').type(fromUnit);
        cy.dataCy('connect-schema-unit-transform-btn').click();
        cy.dataCy('connect-schema-unit-to-dropdown')
            .click()
            .get('mat-option')
            .contains(toUnit)
            .click();
        cy.dataCy('sp-save-edit-property').click();

        cy.dataCy('edit-' + propertyName.toLowerCase(), {
            timeout: 10000,
        }).click({ force: true });
        cy.dataCy('connect-schema-unit-from-input', { timeout: 10000 }).should(
            'have.value',
            fromUnit,
        );
        cy.dataCy('connect-schema-unit-to-dropdown', {
            timeout: 10000,
        }).contains(toUnit);

        cy.dataCy('sp-save-edit-property', { timeout: 10000 }).should(
            'have.length',
            1,
        );
        cy.dataCy('sp-save-edit-property').click();
    }

    public static addStaticProperty(
        propertyName: string,
        propertyValue: string,
    ) {
        // Click add a static value to event
        cy.dataCy('connect-add-static-property', { timeout: 10000 }).click();

        cy.wait(100);

        // Edit new property
        cy.dataCy('edit-key_0', { timeout: 10000 }).click();

        cy.dataCy('connect-edit-field-runtime-name', { timeout: 10000 }).type(
            '{backspace}{backspace}{backspace}{backspace}{backspace}' +
                propertyName,
        );
        cy.dataCy('connect-edit-field-static-value', { timeout: 10000 }).type(
            propertyValue,
        );

        cy.dataCy('sp-save-edit-property').click();

        // validate that static value is persisted
        ConnectEventSchemaUtils.clickEditProperty(propertyName);

        cy.dataCy('connect-edit-field-static-value', { timeout: 10000 }).should(
            'have.value',
            propertyValue,
        );
        cy.dataCy('sp-save-edit-property').click();
    }

    public static deleteProperty(propertyName: string) {
        cy.dataCy('delete-property-' + propertyName, { timeout: 10000 })
            .children()
            .click({ force: true });
        cy.dataCy('connect-schema-delete-properties-btn', {
            timeout: 10000,
        }).click();
    }

    public static changePropertyDataType(
        propertyName: string,
        dataType: string,
    ) {
        ConnectEventSchemaUtils.clickEditProperty(propertyName);
        cy.dataCy('connect-change-runtime-type')
            .click()
            .get('mat-option')
            .contains(dataType)
            .click();
        cy.dataCy('sp-save-edit-property').click();
        // validate that static value is persisted
        cy.dataCy('edit-' + propertyName, { timeout: 10000 }).click({
            force: true,
        });
        cy.dataCy('connect-change-runtime-type', { timeout: 10000 }).contains(
            dataType,
        );
        cy.wait(1000);
        cy.dataCy('sp-save-edit-property').click();
    }

    public static eventSchemaNextBtnDisabled() {
        cy.get('#event-schema-next-button').should('be.disabled');
    }

    public static eventSchemaNextBtnEnabled() {
        cy.get('#event-schema-next-button').parent().should('not.be.disabled');
    }

    public static finishEventSchemaConfiguration() {
        // Click next
        cy.dataCy('sp-connect-schema-editor', { timeout: 10000 }).should(
            'be.visible',
        );
        cy.get('#event-schema-next-button').click();
    }

    public static clickEditProperty(propertyName: string) {
        cy.dataCy('edit-' + propertyName.toLowerCase(), {
            timeout: 10000,
        }).click();
        cy.dataCy('connect-edit-field-runtime-name').should(
            'have.value',
            propertyName,
            { timeout: 10000 },
        );
    }
}
