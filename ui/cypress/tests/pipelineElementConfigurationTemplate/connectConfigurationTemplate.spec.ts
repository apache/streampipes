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
import { StaticPropertyUtils } from '../../support/utils/userInput/StaticPropertyUtils';
import { AdapterBuilder } from '../../support/builder/AdapterBuilder';
import { PipelineElementTemplateUtils } from '../../support/utils/PipelineElementTemplateUtils';

describe('Test Edit Adapter', () => {
    beforeEach('Setup Test', () => {
        // To set up test add a stream adapter that can be configured
        cy.initStreamPipesTest();
        ConnectUtils.goToConnect();
        ConnectUtils.goToNewAdapterPage();
    });

    it('Create and use configuration templates for adapters', () => {
        const WAIT_TIME_MS = 'wait-time-ms';
        const SIMULATOR_OPTION_PRESSURE = 'simulator-option-pressure';
        const TEMPLATE_NAME = 'Test Template';

        // configure adapter template
        const adapterTemplateInput = AdapterBuilder.create(
            'Machine_Data_Simulator',
        )
            .addInput('input', WAIT_TIME_MS, '3000')
            .addInput('radio', 'selected', SIMULATOR_OPTION_PRESSURE)
            .build();
        ConnectUtils.selectAdapter(adapterTemplateInput.adapterType);
        StaticPropertyUtils.input(adapterTemplateInput.adapterConfiguration);

        // store adapter template
        PipelineElementTemplateUtils.addTemplate(TEMPLATE_NAME);

        cy.dataCy('connect-new-adapter-cancel').click();

        // Reload template configuration
        ConnectUtils.goToNewAdapterPage();
        ConnectUtils.selectAdapter(adapterTemplateInput.adapterType);

        // Select template
        PipelineElementTemplateUtils.selectTemplate(TEMPLATE_NAME);

        // validate that the template values are selected
        cy.dataCy(WAIT_TIME_MS).should('have.value', '3000');
        cy.dataCy('selected-' + SIMULATOR_OPTION_PRESSURE).should(
            'have.class',
            'mat-mdc-radio-checked',
        );

        // Delete the template
        PipelineElementTemplateUtils.deleteTemplate();
    });
});
