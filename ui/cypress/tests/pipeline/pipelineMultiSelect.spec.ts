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

import { ConnectUtils } from '../../support/utils/connect/ConnectUtils';
import { PipelineUtils } from '../../support/utils/pipeline/PipelineUtils';
import { PipelineBtns } from '../../support/utils/pipeline/PipelineBtns';
import { PipelineBuilder } from '../../support/builder/PipelineBuilder';
import { PipelineElementBuilder } from '../../support/builder/PipelineElementBuilder';

describe('Pipeline Overview Multi Select', () => {
    const adapterName = 'multi-select-simulator';
    const pipelineNames = [
        'Pipeline Multi Select 1',
        'Pipeline Multi Select 2',
    ];

    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();

        ConnectUtils.addMachineDataSimulator(adapterName);

        pipelineNames.forEach(pipelineName => {
            const pipelineInput = PipelineBuilder.create(pipelineName)
                .addSource(adapterName)
                .addSink(
                    PipelineElementBuilder.create('data_lake')
                        .addInput('input', 'db_measurement', 'demo')
                        .build(),
                )
                .build();

            PipelineUtils.addPipeline(pipelineInput);
        });

        PipelineUtils.goToPipelines();
        cy.wait(1000);
        cy.dataCy('all-pipelines-table', { timeout: 10000 }).should(
            'be.visible',
        );
    });

    it('supports selecting rows and bulk action state changes', () => {
        PipelineBtns.selectionToolbar().should('be.visible');
        PipelineBtns.rowCheckbox().should('have.length', 2);

        PipelineBtns.selectNone().should('be.disabled');
        PipelineBtns.multiActionExecute().should('not.exist');

        PipelineBtns.rowCheckboxInput(0).check({ force: true });
        PipelineBtns.selectNone().should('not.be.disabled');
        PipelineBtns.multiActionExecute().should('be.disabled');

        PipelineBtns.multiActionSelect().click();
        PipelineBtns.multiActionOptionStop().click();
        PipelineBtns.multiActionExecute().should('not.be.disabled');

        PipelineBtns.rowCheckboxInput(0).should('be.checked');
        PipelineBtns.rowCheckboxInput(1).should('not.be.checked');

        PipelineBtns.selectVisible().click();
        PipelineBtns.rowCheckboxInput(0).should('be.checked');
        PipelineBtns.rowCheckboxInput(1).should('be.checked');
        PipelineBtns.multiActionExecute().should('not.be.disabled');

        PipelineBtns.selectNone().click();
        PipelineBtns.rowCheckboxInput(0).should('not.be.checked');
        PipelineBtns.rowCheckboxInput(1).should('not.be.checked');
        PipelineBtns.multiActionExecute().should('not.exist');

        PipelineBtns.selectAllCheckboxInput().check({ force: true });
        PipelineBtns.rowCheckboxInput(0).should('be.checked');
        PipelineBtns.rowCheckboxInput(1).should('be.checked');
        PipelineBtns.multiActionExecute().should('not.be.disabled');

        PipelineBtns.selectAllCheckboxInput().uncheck({ force: true });
        PipelineBtns.rowCheckboxInput(0).should('not.be.checked');
        PipelineBtns.rowCheckboxInput(1).should('not.be.checked');
        PipelineBtns.multiActionExecute().should('not.exist');
    });
});
