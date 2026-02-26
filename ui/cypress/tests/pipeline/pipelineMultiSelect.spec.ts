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
        cy.dataCy('sp-table-selection-toolbar').should('be.visible');
        cy.dataCy('sp-table-row-checkbox').should('have.length', 2);

        cy.dataCy('sp-table-multi-action-execute').should('be.disabled');
        cy.dataCy('sp-table-select-none').should('be.disabled');

        cy.dataCy('sp-table-row-checkbox')
            .eq(0)
            .find('input[type="checkbox"]')
            .check({ force: true });
        cy.dataCy('sp-table-select-none').should('not.be.disabled');
        cy.dataCy('sp-table-multi-action-execute').should('be.disabled');

        cy.dataCy('sp-table-multi-action-select').click();
        cy.dataCy('sp-table-multi-action-option-stop').click();
        cy.dataCy('sp-table-multi-action-execute').should('not.be.disabled');

        cy.dataCy('sp-table-row-checkbox')
            .eq(0)
            .find('input')
            .should('be.checked');
        cy.dataCy('sp-table-row-checkbox')
            .eq(1)
            .find('input')
            .should('not.be.checked');

        cy.dataCy('sp-table-select-visible').click();
        cy.dataCy('sp-table-row-checkbox')
            .eq(0)
            .find('input')
            .should('be.checked');
        cy.dataCy('sp-table-row-checkbox')
            .eq(1)
            .find('input')
            .should('be.checked');
        cy.dataCy('sp-table-multi-action-execute').should('not.be.disabled');

        cy.dataCy('sp-table-select-none').click();
        cy.dataCy('sp-table-row-checkbox')
            .eq(0)
            .find('input')
            .should('not.be.checked');
        cy.dataCy('sp-table-row-checkbox')
            .eq(1)
            .find('input')
            .should('not.be.checked');
        cy.dataCy('sp-table-multi-action-execute').should('be.disabled');

        cy.dataCy('sp-table-select-all-checkbox')
            .find('input[type="checkbox"]')
            .check({ force: true });
        cy.dataCy('sp-table-row-checkbox')
            .eq(0)
            .find('input')
            .should('be.checked');
        cy.dataCy('sp-table-row-checkbox')
            .eq(1)
            .find('input')
            .should('be.checked');
        cy.dataCy('sp-table-multi-action-execute').should('not.be.disabled');

        cy.dataCy('sp-table-select-all-checkbox')
            .find('input[type="checkbox"]')
            .uncheck({ force: true });
        cy.dataCy('sp-table-row-checkbox')
            .eq(0)
            .find('input')
            .should('not.be.checked');
        cy.dataCy('sp-table-row-checkbox')
            .eq(1)
            .find('input')
            .should('not.be.checked');
        cy.dataCy('sp-table-multi-action-execute').should('be.disabled');
    });
});
