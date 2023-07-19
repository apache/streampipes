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

describe('Test Kafka Integration', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
    });

    it('Perform Test', () => {
        const topicName = 'cypresstopic';
        const host: string = ParameterUtils.get('localhost', 'kafka');
        const port: string = ParameterUtils.get('9094', '9092');

        const sink: PipelineElementInput = PipelineElementBuilder.create(
            'kafka_publisher',
        )
            .addInput('radio', 'access-mode-unauthenticated_plain', '')
            .addInput('input', 'host', host)
            .addInput(
                'input',
                'port',
                '{backspace}{backspace}{backspace}{backspace}' + port,
            )
            .addInput('input', 'topic', topicName)
            .build();

        const adapter = AdapterBuilder.create('Apache_Kafka')
            .setName('Kafka4')
            .setTimestampProperty('timestamp')
            .addProtocolInput('radio', 'access-mode-unauthenticated_plain', '')
            .addProtocolInput('input', 'host', host)
            .addProtocolInput('input', 'port', port)
            .addProtocolInput('click', 'sp-reload', '')
            .addProtocolInput('radio', topicName, '')
            .setFormat('json')
            .addFormatInput('radio', 'json_options-single_object', '')
            .build();

        ThirdPartyIntegrationUtils.runTest(sink, adapter);
    });
});
