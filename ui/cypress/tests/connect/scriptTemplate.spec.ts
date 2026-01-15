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
import { AdapterBuilder } from '../../support/builder/AdapterBuilder';
import { ConnectBtns } from '../../support/utils/connect/ConnectBtns';

const TEMPLATE_NAME = 'TestTemplate';
const SCRIPT_LINE = "event.b = 'b';";
const SCRIPT = `  ${SCRIPT_LINE}
out.collect(event);
}`;

describe('Validate Warning Pops For Configuration Changes ', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
        const adapter = AdapterBuilder.create('Machine_Data_Simulator')
            .setName('Validate Configuration Changes Adapter')
            .setTimestampProperty('timestamp')
            .addInput('input', 'wait-time-ms', '1000')
            .build();

        ConnectUtils.goToConnect();
        ConnectUtils.goToNewAdapterPage();
        ConnectUtils.selectAdapter(adapter.adapterType);
        ConnectUtils.configureAdapter(adapter);
        ConnectBtns.scriptActiveToggle().click();
    });

    it('Perform Test', () => {
        addScriptTemplate(TEMPLATE_NAME);
        validateScriptTemplateIsStored(TEMPLATE_NAME);
        deleteScriptTemplate(TEMPLATE_NAME);
        validateScriptTemplateIsDeleted();
    });

    const addScriptTemplate = (templateName: string) => {
        ConnectUtils.addScriptAsScriptTemplate(templateName, SCRIPT);
    };

    const validateScriptTemplateIsStored = (templateName: string) => {
        ConnectBtns.configureSchemaScriptEditor().should(
            'contain.text',
            SCRIPT_LINE,
        );
        ConnectBtns.resetScriptBtn().click();
        ConnectBtns.configureSchemaScriptEditor().should(
            'not.contain',
            SCRIPT_LINE,
        );
        ConnectUtils.useScriptTemplate(templateName);
        ConnectBtns.configureSchemaScriptEditor().should(
            'contain.text',
            SCRIPT_LINE,
        );
    };

    const deleteScriptTemplate = (templateName: string) => {
        ConnectUtils.deleteScriptTemplate(templateName);
    };

    const validateScriptTemplateIsDeleted = () => {
        ConnectBtns.useScriptTemplateBtn().click();
        ConnectBtns.selectScriptTemplateDropDown().click();
        cy.get('mat-option').should('not.exist');
    };
});
