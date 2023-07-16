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

import { PipelineElementBuilder } from '../../support/builder/PipelineElementBuilder';
import { ThirdPartyIntegrationUtils } from '../../support/utils/ThirdPartyIntegrationUtils';
import { PipelineElementInput } from '../../support/model/PipelineElementInput';
import { ParameterUtils } from '../../support/utils/ParameterUtils';
import { AdapterBuilder } from '../../support/builder/AdapterBuilder';

describe('Test InfluxDB Integration', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
    });

    it('Perform Test', () => {
        const dbName = 'cypresstestdb';
        const host: string = ParameterUtils.get('localhost', 'influxdb');

        const sink: PipelineElementInput = PipelineElementBuilder.create(
            'influxdb',
        )
            .addInput('input', 'db_host', host)
            .addInput('input', 'db_port', '8086')
            .addInput('input', 'db_name', 'sp')
            .addInput('input', 'db_measurement', dbName)
            .addInput('radio', 'db_authentication-token', '')
            .addInput('input', 'undefined-db_token-0', 'sp-admin')
            .addInput('input', 'batch_interval_actions', '2')
            .addInput(
                'input',
                'max_flush_duration',
                '{backspace}{backspace}{backspace}{backspace}500',
            )
            .addInput('drop-down', 'timestamp_mapping', 'timestamp')
            .build();

        const adapter = AdapterBuilder.create('InfluxDB_Stream_Adapter')
            .setName('InfluxDB Adapter')
            .addInput('input', 'db_host', host)
            .addInput('input', 'db_port', '8086')
            .addInput('input', 'db_name', 'sp')
            .addInput('input', 'db_measurement', dbName)
            .addInput('radio', 'db_authentication-token', '')
            .addInput('input', 'undefined-db_token-0', 'sp-admin')
            .addInput('input', 'pollingInterval', '200')
            .build();

        ThirdPartyIntegrationUtils.runTest(sink, adapter);
    });
});
