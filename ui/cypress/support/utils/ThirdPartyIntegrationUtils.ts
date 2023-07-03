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

import { ConnectUtils } from './connect/ConnectUtils';
import { PipelineBuilder } from '../builder/PipelineBuilder';
import { PipelineUtils } from './PipelineUtils';
import { PipelineElementInput } from '../model/PipelineElementInput';
import { AdapterInput } from '../model/AdapterInput';
import { AdapterBuilder } from '../builder/AdapterBuilder';

export class ThirdPartyIntegrationUtils {
    public static runTest(sink: PipelineElementInput, adapter: AdapterInput) {
        const simulatorAdapterName = 'simulator';

        const machineAdapter = AdapterBuilder.create('Machine_Data_Simulator')
            .setName(simulatorAdapterName)
            .addInput('input', 'wait-time-ms', '1000')
            .build();

        ConnectUtils.testAdapter(machineAdapter);

        const pipelineInput = PipelineBuilder.create('Pipeline Test')
            .addSource(simulatorAdapterName)
            .addSink(sink)
            .build();

        PipelineUtils.addPipeline(pipelineInput);

        ConnectUtils.testAdapter(adapter);
    }
}
