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

import { PipelineUtils } from '../../support/utils/pipeline/PipelineUtils';

describe('Test rename of running pipeline', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
    });

    it('Perform Test', () => {
        PipelineUtils.addSampleAdapterAndPipeline();

        PipelineUtils.verifyPipelineCount(1);
        PipelineUtils.verifyPipelineName('Pipeline Test');

        PipelineUtils.editPipeline('Pipeline Test');
        cy.wait(1000);
        cy.dataCy('sp-editor-save-pipeline').click();
        cy.dataCy('sp-editor-pipeline-name').clear();
        PipelineUtils.updatePipeline('Renamed Pipeline');
        PipelineUtils.finalizePipelineStart();

        PipelineUtils.verifyPipelineCount(1);
        PipelineUtils.verifyPipelineName('Renamed Pipeline');

        PipelineUtils.editPipeline('Renamed Pipeline');
        cy.dataCy('sp-editor-save-pipeline').click();
        cy.dataCy('sp-editor-pipeline-name').clear();
        PipelineUtils.clonePipeline('Cloned Renamed Pipeline');
        PipelineUtils.finalizePipelineStart();

        PipelineUtils.verifyPipelineCount(2);
        PipelineUtils.verifyPipelineName('Cloned Renamed Pipeline');
    });
});
