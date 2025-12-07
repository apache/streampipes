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

import { ConnectUtils } from '../../support/utils/connect/ConnectUtils';
import { ConnectBtns } from '../../support/utils/connect/ConnectBtns';
import { AdapterBuilder } from '../../support/builder/AdapterBuilder';
import { ConnectEventSchemaUtils } from '../../support/utils/connect/ConnectEventSchemaUtils';

describe('Test Edit Adapter', () => {
    const adapterName = 'Test Adapter';

    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
    });

    it('Edit and persist adapter schema and reconfigure adapter', () => {
        createAdapterWithSchemaEdits();

        validateSavedAdapterEdits();

        reconfigureAdapterToUsePressureSensorInsteadOfFlowRate();
    });

    function createAdapterWithSchemaEdits() {
        const adapterInput = AdapterBuilder.create('Machine_Data_Simulator')
            .setName(adapterName)
            .addInput('input', 'wait-time-ms', '1000')
            .build();

        ConnectUtils.createAdapterUntilEventSchemaConfiguration(adapterInput);

        // Add new property and edit field
        ConnectEventSchemaUtils.addStaticProperty(
            'test-property-1',
            'static-value-1',
        );

        // Edit property density
        const propertyName = 'density';
        ConnectEventSchemaUtils.changePropertyDataType(propertyName, 'Double');
        ConnectEventSchemaUtils.numberTransformation(propertyName, '2');
        ConnectEventSchemaUtils.changeSemanticType(
            propertyName,
            'http://schema.org/Numbers',
        );
        ConnectEventSchemaUtils.renameProperty(propertyName, 'test-density');

        ConnectUtils.finishEventSchemaConfiguration();

        ConnectUtils.startAdapter(adapterInput);
    }

    function validateSavedAdapterEdits() {
        // Edit adapter and check if given values and added property still provided
        ConnectBtns.openActionsMenu(adapterName);
        ConnectBtns.editAdapter().should('not.be.disabled');
        ConnectBtns.editAdapter().click();
        ConnectBtns.adapterSettingsNextBtn().click();
        ConnectEventSchemaUtils.clickEditProperty('density', false);
        // cy.dataCy('edit-density').click();
        ConnectEventSchemaUtils.validateRuntimeName('test-density');

        ConnectBtns.semanticTypeInput().should(
            'have.value',
            'http://schema.org/Numbers',
        );
        ConnectBtns.changeRuntimeType().should('include.text', 'Double');
        ConnectBtns.connectSchemaCorrectionValueInput().should(
            'have.value',
            '2',
        );
        ConnectBtns.connectSchemaCorrectionOperatorInput().should(
            'include.text',
            'Multiply',
        );

        ConnectBtns.changeRuntimeType()
            .click()
            .get('mat-option')
            .contains('Float')
            .click();
        ConnectBtns.connectSchemaCorrectionValueInput().clear();
        ConnectBtns.saveEditProperty().click();
        ConnectEventSchemaUtils.schemaPreviewResultEvent().should(
            'include.text',
            'test-property-1',
        );

        storeAndCloseAdapterPreview();
    }

    function reconfigureAdapterToUsePressureSensorInsteadOfFlowRate() {
        // Configure adapter with pressure instead of flowrate
        ConnectBtns.openActionsMenu(adapterName);
        ConnectBtns.editAdapter().click();

        const adapterInput = AdapterBuilder.create('Machine_Data_Simulator')
            .addInput('input', 'wait-time-ms', '2000')
            .addInput('radio', 'selected', 'simulator-option-pressure')
            .build();
        ConnectUtils.configureAdapter(adapterInput);

        ConnectEventSchemaUtils.schemaPreviewResultEvent().should(
            'include.text',
            'test-property-1',
        );

        storeAndCloseAdapterPreview();
    }

    function storeAndCloseAdapterPreview() {
        ConnectBtns.schemaNextBtn().click();
        ConnectBtns.storeEditAdapter().click();
        ConnectUtils.closeAdapterPreview();
    }
});
