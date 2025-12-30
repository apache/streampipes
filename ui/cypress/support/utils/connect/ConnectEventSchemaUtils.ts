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
import { DataTypeString } from '../../model/DataTypeString';
import { PropertyDataTypeChange } from '../../model/PropertyDataTypeChange';

export class ConnectEventSchemaUtils {
    public static markPropertyAsMeasurement(propertyName: string) {
        cy.dataCy('property-scope-' + propertyName, { timeout: 10000 })
            .click()
            .get('.mdc-list-item__primary-text')
            .contains('Measurement')
            .click();
    }

    public static markPropertyAsDimension(propertyName: string) {
        cy.dataCy('property-scope-' + propertyName, { timeout: 10000 })
            .click()
            .get('.mdc-list-item__primary-text')
            .contains('Dimension')
            .click();
    }

    public static markPropertyAsTimestamp(propertyName: string) {
        // Mark property as timestamp
        this.configureFieldsNextBtnDisabled();
        // Edit timestamp

        ConnectEventSchemaUtils.clickEditProperty(propertyName);

        // Mark as timestamp
        ConnectBtns.markAsTimestampBtn().click();

        // Close
        ConnectBtns.saveEditProperty().click();

        this.configureFieldsNextBtnEnabled();
    }

    public static addTimestampProperty() {
        this.addTimestampFieldToScript();
        ConnectBtns.configureSchemaRunScriptBtn().click();
        ConnectBtns.configureSchemaEventPreviewResult().contains('timestamp');
    }

    private static addTimestampFieldToScript() {
        ConnectBtns.configureSchemaScriptEditor()
            .type('{backspace}'.repeat(17)) // 2. Delete the "  return event;\n}" part
            .type(
                '  event.timestamp = new Date().getTime();{enter}return event;{enter}}',
            );
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

        ConnectBtns.saveEditProperty().click();
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

    public static changePropertyDataTypes(
        dataTypeChanges: PropertyDataTypeChange[],
    ) {
        if (dataTypeChanges.length > 0) {
            dataTypeChanges.forEach(dataTypeChange => {
                ConnectEventSchemaUtils.changePropertyDataType(
                    dataTypeChange.propertyName,
                    dataTypeChange.dataType,
                );
            });
        }
    }

    public static changePropertyDataType(
        propertyName: string,
        dataType: DataTypeString,
        warningIsShown: boolean = false,
    ) {
        ConnectEventSchemaUtils.clickEditProperty(propertyName);

        this.checkIfWarningIsShown(warningIsShown);

        ConnectBtns.changeRuntimeType()
            .click()
            .get('mat-option')
            .contains(dataType)
            .click();
        ConnectBtns.saveEditProperty().click();
        // validate that static value is persisted
        cy.dataCy('edit-' + propertyName.toLowerCase(), {
            timeout: 10000,
        }).click({
            force: true,
        });
        ConnectBtns.changeRuntimeType().contains(dataType);
        ConnectBtns.saveEditProperty().click();
    }

    public static changeSemanticType(propertyName: string, value: string) {
        ConnectEventSchemaUtils.clickEditProperty(propertyName);
        ConnectBtns.semanticTypeInput().clear().type(value);
        ConnectBtns.saveEditProperty().click();
    }

    private static checkIfWarningIsShown(warningIsShown: boolean) {
        if (warningIsShown) {
            cy.dataCy('warning-change-data-type').should('be.visible');
        } else {
            cy.dataCy('warning-change-data-type').should('not.exist');
        }
    }

    public static configureFieldsNextBtnDisabled() {
        ConnectBtns.configureFieldsNextBtn().should('be.disabled');
    }

    public static configureFieldsNextBtnEnabled() {
        ConnectBtns.configureFieldsNextBtn().parent().should('not.be.disabled');
    }

    public static schemaPreviewResultEvent() {
        return cy.dataCy('schema-preview-result-event', { timeout: 10000 });
    }

    public static clickEditProperty(propertyName: string) {
        cy.dataCy(`edit-${ConnectEventSchemaUtils.escape(propertyName)}`, {
            timeout: 10000,
        }).click();
    }

    public static regexValueInput() {
        return cy.dataCy('regex-value');
    }

    public static regexReplaceWithValueInput() {
        return cy.dataCy('regex-replace-with-value');
    }

    public static regexReplaceAllCheckbox() {
        return cy.dataCy('regex-replace-all-value');
    }

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
