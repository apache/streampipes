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
import { PipelineBuilder } from '../../support/builder/PipelineBuilder';
import { PipelineElementBuilder } from '../../support/builder/PipelineElementBuilder';
import { UserUtils } from '../../support/utils/UserUtils';
import { PipelineUtils } from '../../support/utils/pipeline/PipelineUtils';

const adapterName = 'simulator';

const pipelineInput = PipelineBuilder.create('Pipeline Test')
    .addSource(adapterName)
    .addProcessingElement(
        PipelineElementBuilder.create('field_renamer')
            .addInput('drop-down', 'convert-property', 'timestamp')
            .addInput('input', 'field-name', 't')
            .build(),
    )
    .addSink(
        PipelineElementBuilder.create('data_lake')
            .addInput('input', 'db_measurement', 'demo')
            .build(),
    )
    .build();

describe('Test Enhanced Adapter Deletion', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
        UserUtils.addUser(UserUtils.userWithAdapterAndPipelineAdminRights);
    });

    it('Test Delete Adapter and Associated Pipelines', () => {
        ConnectUtils.addMachineDataSimulator(adapterName, false);
        PipelineUtils.addPipeline(pipelineInput);

        ConnectUtils.deleteAdapterAndAssociatedPipelines();
    });

    it('Test Admin Should Be Able to Delete Adapter and Not Owned Associated Pipelines', () => {
        // Let the user create the adapter and the pipeline
        ConnectUtils.addMachineDataSimulator(adapterName, false);
        PipelineUtils.addPipeline(pipelineInput);

        // Then let the admin delete them
        UserUtils.switchUser(UserUtils.adminUser);
        ConnectUtils.deleteAdapterAndAssociatedPipelines(true);
    });

    it('Test Delete Adapter and Associated Pipelines Permission Denied', () => {
        // Let the admin create the adapter and the pipeline
        UserUtils.switchUser(UserUtils.adminUser);
        ConnectUtils.addMachineDataSimulator(adapterName, false);
        PipelineUtils.addPipeline(pipelineInput);

        // Then the user shouldn't be able to delete them
        UserUtils.switchUser(UserUtils.userWithAdapterAndPipelineAdminRights);
        ConnectUtils.deleteAdapterAndAssociatedPipelinesPermissionDenied();
    });
});
