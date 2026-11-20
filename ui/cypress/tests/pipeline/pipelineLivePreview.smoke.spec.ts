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

import { PipelineElementBuilder } from '../../support/builder/PipelineElementBuilder';
import { ConnectUtils } from '../../support/utils/connect/ConnectUtils';
import { PipelineBtns } from '../../support/utils/pipeline/PipelineBtns';
import { PipelineUtils } from '../../support/utils/pipeline/PipelineUtils';

describe('Pipeline editor live preview', () => {
    const adapterName = 'simulator';
    const processorName = 'field_renamer';

    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
        ConnectUtils.addMachineDataSimulator(adapterName);
    });

    it('shows live data per element and releases the preview when the panel closes', () => {
        PipelineUtils.addDataStreamWithProcessor(
            adapterName,
            PipelineElementBuilder.create(processorName)
                .addInput('drop-down', 'convert-property', 'timestamp')
                .addInput('input', 'field-name', 't')
                .build(),
        );

        PipelineUtils.openLivePreview();

        // the canvas only shows the status of each element
        PipelineBtns.livePreviewBadge(adapterName).should(
            'contain.text',
            'Live',
        );
        PipelineBtns.livePreviewBadge(processorName).should(
            'contain.text',
            'Live',
        );

        // the panel compares input and output of the selected processor
        PipelineUtils.selectLivePreviewElement(processorName);
        PipelineBtns.livePreviewDetailTable()
            .should('contain.text', 'Input')
            .and('contain.text', 'Output');
        // the renamer removes 'timestamp' and adds 't'; all other values pass through
        PipelineBtns.livePreviewChangedValues().should('have.length', 2);

        PipelineUtils.closeLivePreview();
        PipelineBtns.livePreviewBadge(adapterName).should('not.exist');
    });
});
