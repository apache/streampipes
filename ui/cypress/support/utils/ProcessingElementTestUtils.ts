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

import { FileManagementUtils } from './FileManagementUtils';
import { AdapterUtils } from './AdapterUtils';
import { PipelineElementInput } from '../model/PipelineElementInput';
import { PipelineUtils } from './PipelineUtils';
import { DataLakeUtils } from './DataLakeUtils';
import { GenericAdapterBuilder } from '../builder/GenericAdapterBuilder';
import { PipelineBuilder } from '../builder/PipelineBuilder';
import { PipelineElementBuilder } from '../builder/PipelineElementBuilder';

export class ProcessingElementTestUtils {

    public static testElement(testName: string, inputFile: string, expectedResultFile: string, processor: PipelineElementInput) {
        // Test
        FileManagementUtils.addFile(inputFile);

        const dataLakeIndex = testName.toLowerCase();

        const adapterName = testName.toLowerCase();

        // Build adapter
        const adapterInput = GenericAdapterBuilder
            .create('File_Set')
            .setName(adapterName)
            .setTimestampProperty('timestamp')
            .addProtocolInput('input', 'interval-key', '0')
            .setFormat('csv')
            .addFormatInput('input', 'delimiter', ';')
            .addFormatInput('checkbox', 'header', 'check')
            .build();

        AdapterUtils.addGenericSetAdapter(adapterInput);

        // Build Pipeline
        const pipelineInput = PipelineBuilder.create(testName)
            .addSource(adapterName)
            .addProcessingElement(processor)
            .addSink(
                PipelineElementBuilder.create('data_lake')
                    .addInput('input', 'db_measurement', dataLakeIndex)
                    .build())
            .build();

        PipelineUtils.addPipeline(pipelineInput);

        // // Wait
        it('Wait till data is stored', () => {
            cy.wait(10000);
        });

        DataLakeUtils.checkResults(dataLakeIndex, 'cypress/fixtures/' + expectedResultFile);

        PipelineUtils.deletePipeline();

        AdapterUtils.deleteAdapter();

        FileManagementUtils.deleteFile();
    }
}
