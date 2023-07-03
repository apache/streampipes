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
import { ParameterUtils } from '../../support/utils/ParameterUtils';
import { AdapterBuilder } from '../../support/builder/AdapterBuilder';

describe('Test OPC-UA Adapter Pull Mode', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
    });

    it('Perform Test', () => {
        const adapterInput = getAdapterBuilder(true);

        ConnectUtils.testAdapter(adapterInput);
    });
});

describe('Test OPC-UA Adapter Subscription Mode', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
    });

    it('Perform Test', () => {
        const adapterInput = getAdapterBuilder(false);

        ConnectUtils.testAdapter(adapterInput);
    });
});

const getAdapterBuilder = (pullMode: boolean) => {
    const host: string = ParameterUtils.get('localhost', 'opcua');

    const builder = AdapterBuilder.create('OPC_UA').setName(
        'OPC UA Test ' + (pullMode ? '(Pull)' : '(Subscription)'),
    );

    if (pullMode) {
        builder.addInput('select', 'adapter_type-pull_mode', 'check');
        builder.addInput('input', 'undefined-PULLING_INTERVAL-0', '1000');
    } else {
        builder.addInput('select', 'adapter_type-subscription_mode', 'check');
    }
    builder
        .addInput('select', 'access_mode-none', 'check')
        .addInput('select', 'opc_host_or_url-url', 'check')
        .addInput(
            'input',
            'undefined-OPC_SERVER_URL-0',
            'opc.tcp://' + host + ':50000',
        )
        .addInput('input', 'NAMESPACE_INDEX', '2')
        .addInput('input', 'NODE_ID', 'Telemetry')

        .addInput('button', 'button-Telemetry')
        .addInput('button', 'button-Anomaly')
        .addInput('checkbox', 'DipData', 'check')
        .addInput('checkbox', 'NegativeTrendData', 'check');

    return builder.build();
};
