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

import { ConnectBtns } from './ConnectBtns';

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
        ConnectBtns.markAsTimestampBtn().click();

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

    public static editTimestampPropertyWithRegex(
        propertyName: string,
        timestampRegex: string,
    ) {
        ConnectEventSchemaUtils.clickEditProperty(propertyName);

        ConnectBtns.markAsTimestampBtn().click();
        ConnectBtns.setTimestampConverter('String');

        ConnectBtns.timestampStringRegex().type(timestampRegex);

        ConnectBtns.saveEditProperty().click();

        // The following code validates that the regex is persisted by reopening the edit dialog again
        cy.dataCy('edit-' + propertyName.toLowerCase(), {
            timeout: 10000,
        }).click({ force: true });
        ConnectBtns.timestampStringRegex().should('have.value', timestampRegex);

        ConnectBtns.saveEditProperty().should('have.length', 1);
        ConnectBtns.saveEditProperty().click();
    }

    public static editTimestampPropertyWithNumber(
        propertyName: string,
        configurationValue: 'Seconds' | 'Milliseconds',
    ) {
        ConnectEventSchemaUtils.clickEditProperty(propertyName);
        ConnectBtns.markAsTimestampBtn().click();

        ConnectBtns.setTimestampConverter('Number');

        ConnectBtns.timestampNumberDropdown()
            .click({ force: true })
            .get('mat-option')
            .contains(configurationValue)
            .click();

        ConnectBtns.saveEditProperty().click();

        // Check if the configuration is persisted by reopening the edit dialog
        ConnectEventSchemaUtils.clickEditProperty(propertyName);

        ConnectBtns.timestampNumberDropdown().should(
            'contain',
            configurationValue,
        );

        ConnectBtns.saveEditProperty().click();
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

    public static renameProperty(
        fromRuntimeName: string,
        toRuntimeName: string,
    ) {
        ConnectEventSchemaUtils.clickEditProperty(fromRuntimeName);
        ConnectEventSchemaUtils.setRuntimeName(toRuntimeName);
        ConnectBtns.saveEditProperty().click();
    }

    public static setRuntimeName(newRuntimeName: string) {
        ConnectBtns.runtimeNameInput().clear().type(newRuntimeName);
    }

    public static validateRuntimeName(expectedRuntimeName: string) {
        ConnectBtns.runtimeNameInput().should(
            'have.value',
            expectedRuntimeName,
        );
    }

    public static unitTransformation(
        propertyName: string,
        fromUnit: string,
        toUnit: string,
    ) {
        ConnectEventSchemaUtils.clickEditProperty(propertyName);
        ConnectBtns.schemaUnitFromDropdown().type(fromUnit);
        ConnectBtns.schemaUnitTransformBtn().click();
        ConnectBtns.schemaUnitToDropdown().click();

        ConnectBtns.schemaUnitToDropdown()
            .get('mat-option')
            .contains(toUnit)
            .click();
        ConnectBtns.saveEditProperty().click();
    }

    public static addStaticProperty(
        propertyName: string,
        propertyValue: string,
    ) {
        // Click add a static value to event
        cy.dataCy('connect-add-static-property', { timeout: 10000 }).click();

        cy.wait(100);

        // Edit new property
        cy.dataCy('connect-add-field-name', { timeout: 10000 }).type(
            '{backspace}{backspace}{backspace}{backspace}{backspace}' +
                propertyName,
        );
        cy.dataCy('connect-add-field-name-button').click();

        cy.dataCy('edit-' + propertyName.toLowerCase()).click();

        cy.dataCy('connect-edit-field-static-value').clear();
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
        cy.dataCy('"delete-property-' + propertyName + '"', { timeout: 10000 })
            .children()
            .click({ force: true });
        cy.dataCy('connect-schema-delete-properties-btn', {
            timeout: 10000,
        }).click({ force: true });

        // The following two commands are required to fix flaky tests
        // if another solution can be found, it can be removed
        cy.wait(200);
        cy.dataCy('connect-schema-update-preview-btn', {
            timeout: 10000,
        }).click({ force: true });
    }

    public static changePropertyDataType(
        propertyName: string,
        dataType: string,
        warningIsShown: boolean = false,
    ) {
        ConnectEventSchemaUtils.clickEditProperty(propertyName);

        this.checkIfWarningIsShown(warningIsShown);

        ConnectBtns.changeRuntimeType()
            .click()
            .get('mat-option')
            .contains(dataType)
            .click();
        cy.dataCy('sp-save-edit-property').click();
        // validate that static value is persisted
        cy.dataCy('edit-' + propertyName, { timeout: 10000 }).click({
            force: true,
        });
        ConnectBtns.changeRuntimeType().contains(dataType);
        cy.wait(1000);
        cy.dataCy('sp-save-edit-property').click();
    }

    private static checkIfWarningIsShown(warningIsShown: boolean) {
        if (warningIsShown) {
            cy.dataCy('warning-change-data-type').should('be.visible');
        } else {
            cy.dataCy('warning-change-data-type').should('not.exist');
        }
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
        cy.dataCy('sp-event-schema-next-button').click();
    }

    public static clickEditProperty(propertyName: string) {
        cy.dataCy(`edit-${ConnectEventSchemaUtils.escape(propertyName)}`, {
            timeout: 10000,
        }).click();
        ConnectEventSchemaUtils.validateRuntimeName(propertyName);
    }

    //
    /**
     * Function to escape special characters in a string for use in Cypress
     * selectors
     */
    public static escape(selector: string): string {
        return selector
            .replace(/([.*+?^=!:${}()|\[\]\/\\])/g, '\\$1')
            .toLowerCase();
    }
}
