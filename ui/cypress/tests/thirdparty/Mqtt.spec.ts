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
import { AdapterBuilder } from '../../support/builder/AdapterBuilder';
import { ParameterUtils } from '../../support/utils/ParameterUtils';

describe('Test MQTT Integration', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
    });

    it('Perform Test', () => {
        const topicName = 'cypresstopic';
        const host: string = ParameterUtils.get('localhost', 'mosquitto');
        const port: string = ParameterUtils.get('1884', '1883');

        const sink: PipelineElementInput = PipelineElementBuilder.create(
            'mqtt_publisher',
        )
            .addInput('input', 'host', host)
            .addInput('input', 'topic', topicName)
            .addInput('input', 'port', port)
            .build();

        const adapter = AdapterBuilder.create('MQTT')
            .setName('Adapter Mqtt')
            .setTimestampProperty('timestamp')
            .addProtocolInput('radio', 'access-mode-unauthenticated', '')
            .addProtocolInput('input', 'broker_url', `tcp://${host}:${port}`)
            .addProtocolInput('input', 'topic', topicName)
            .setFormat('json')
            .build();

        ThirdPartyIntegrationUtils.runTest(sink, adapter);
    });
});
