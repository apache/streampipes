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

import { PipelineUtils } from '../../support/utils/pipeline/PipelineUtils';
import { PipelineBuilder } from '../../support/builder/PipelineBuilder';
import { PipelineElementBuilder } from '../../support/builder/PipelineElementBuilder';
import { ConnectUtils } from '../../support/utils/connect/ConnectUtils';
import { PipelineElementTemplateUtils } from '../../support/utils/PipelineElementTemplateUtils';

describe('Test Edit Adapter', () => {
    const TEMPLATE_NAME = 'Test Template';
    const adapterName = 'simulator';

    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
        ConnectUtils.addMachineDataSimulator(adapterName);
    });

    it('Create and use configuration templates for processing elements', () => {
        PipelineUtils.goToPipelineEditor();

        const pipelineInput = PipelineBuilder.create('Pipeline Test')
            .addSource(adapterName)
            .addProcessingElement(
                PipelineElementBuilder.create('numerical_filter')
                    .addInput('drop-down', 'number-mapping', 'temperature')
                    .addInput('radio', 'operation', '\\=\\=')
                    .addInput('input', 'value', '20')
                    .build(),
            )
            .build();

        // Create pipeline element template
        PipelineUtils.selectDataStream(pipelineInput);
        PipelineUtils.openPossibleElementsMenu(adapterName);
        PipelineUtils.configureProcessingElement(
            pipelineInput.processingElement,
        );

        // Add new template
        PipelineElementTemplateUtils.addTemplate(TEMPLATE_NAME);

        cy.dataCy('cancel-customize').click();

        PipelineUtils.goToPipelineEditor();
        PipelineUtils.selectDataStream(pipelineInput);
        PipelineUtils.openPossibleElementsMenu(adapterName);
        PipelineUtils.selectCompatibleElement(
            pipelineInput.processingElement.name,
        );

        // Select template
        PipelineElementTemplateUtils.selectTemplate(TEMPLATE_NAME);

        // validate that selection is correct
        cy.dataCy('value').should('have.value', '20');
        cy.dataCy('operation-\\=\\=').should(
            'have.class',
            'mat-mdc-radio-checked',
        );
        // Mapping properties can not be stored in templates, therefore the default value is required
        cy.dataCy('number-mapping').contains('density');

        // delete template
        PipelineElementTemplateUtils.deleteTemplate();
    });
});
