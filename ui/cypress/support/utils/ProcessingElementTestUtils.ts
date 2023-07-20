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
import { ConnectUtils } from './connect/ConnectUtils';
import { PipelineUtils } from './PipelineUtils';
import { DataLakeUtils } from './datalake/DataLakeUtils';
import { PipelineBuilder } from '../builder/PipelineBuilder';
import { PipelineElementBuilder } from '../builder/PipelineElementBuilder';
import { ProcessorTest } from '../model/ProcessorTest';
import { ConnectBtns } from './connect/ConnectBtns';
import { AdapterBuilder } from '../builder/AdapterBuilder';

export class ProcessingElementTestUtils {
    public static testElement(pipelineElementTest: ProcessorTest) {
        const inputFile =
            'pipelineElement/' +
            pipelineElementTest.dir +
            '/' +
            pipelineElementTest.inputFile;
        const expectedResultFile =
            'pipelineElement/' + pipelineElementTest.dir + '/expected.csv';

        let formatType;
        pipelineElementTest.inputFile.endsWith('.csv')
            ? (formatType = 'csv')
            : (formatType = 'json');

        FileManagementUtils.addFile(inputFile);

        const dataLakeIndex = pipelineElementTest.name.toLowerCase();

        const adapterName = pipelineElementTest.name.toLowerCase();

        // Build adapter
        const adapterInputBuilder = AdapterBuilder.create('File_Stream')
            .setName(adapterName)
            .setTimestampProperty('timestamp')
            .setFormat(formatType)
            .setStartAdapter(false)
            .addProtocolInput(
                'radio',
                'speed',
                'fastest_\\(ignore_original_time\\)',
            )
            .addProtocolInput('radio', 'replayonce', 'yes');

        if (formatType === 'csv') {
            adapterInputBuilder
                .addFormatInput('input', ConnectBtns.csvDelimiter(), ';')
                .addFormatInput('checkbox', ConnectBtns.csvHeader(), 'check');
        } else if (formatType === 'json') {
            adapterInputBuilder.addFormatInput(
                'radio',
                'json_options-array',
                '',
            );
        }

        const adapterInput = adapterInputBuilder.build();

        ConnectUtils.addAdapter(adapterInput);
        ConnectUtils.startAdapter(adapterInput);

        // Build Pipeline
        const pipelineInput = PipelineBuilder.create(pipelineElementTest.name)
            .addSource(adapterName)
            .addSourceType('set')
            .addProcessingElement(pipelineElementTest.processor)
            .addSink(
                PipelineElementBuilder.create('data_lake')
                    .addInput('input', 'db_measurement', dataLakeIndex)
                    .build(),
            )
            .build();

        PipelineUtils.addPipeline(pipelineInput);

        ConnectUtils.goToConnect();
        ConnectBtns.startAdapter().click();

        cy.wait(3000);

        DataLakeUtils.checkResults(
            dataLakeIndex,
            'cypress/fixtures/' + expectedResultFile,
            pipelineElementTest.processor.ignoreTimestamp,
        );
    }
}
