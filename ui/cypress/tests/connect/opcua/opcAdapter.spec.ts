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
import { TreeNodeBuilder } from '../../support/builder/TreeNodeBuilder';

describe('Test OPC-UA Adapter Pull Mode', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
    });

    it('Test OPC-UA Adapter Pull Mode', () => {
        const adapterInput = getAdapterBuilderWithTreeNodes(true);

        ConnectUtils.testAdapter(adapterInput);
    });

    it('Test OPC-UA Adapter Subscription Mode', () => {
        const adapterInput = getAdapterBuilderWithTreeNodes(false);

        ConnectUtils.testAdapter(adapterInput);
    });

    it('Test OPC-UA Adapter Text Editor Configuration', () => {
        const adapterInput = getAdapterBuilderWithTextNodes(false);

        ConnectUtils.testAdapter(adapterInput);
    });
});

const getAdapterBuilderWithTreeNodes = (pullMode: boolean) => {

    const builder = getBaseAdapterConfigBuilder(pullMode);
    builder.addTreeNode(
        TreeNodeBuilder.create(
            'Objects',
            TreeNodeBuilder.create(
                'OpcPlc',
                TreeNodeBuilder.create(
                    'Telemetry',
                    TreeNodeBuilder.create('Basic').addChildren(
                        TreeNodeBuilder.create('AlternatingBoolean'),
                        TreeNodeBuilder.create('StepUp'),
                        TreeNodeBuilder.create('RandomSignedInt32'),
                        TreeNodeBuilder.create('RandomUnsignedInt32'),
                    ),
                ),
            ),
        ),
    );


    return builder.build();
};

const getAdapterBuilderWithTextNodes = (pullMode: boolean) => {
    const builder = getBaseAdapterConfigBuilder(pullMode);
    builder.addTreeNode(
        TreeNodeBuilder.create('ns=3;s=StepUp')
            .isTextConfig()
    );

    return builder.build();
};




const getBaseAdapterConfigBuilder = (pullMode: boolean) : AdapterBuilder => {
    const host: string = ParameterUtils.get('localhost', 'opcua');

    const builder = AdapterBuilder.create('OPC_UA').setName(
        'OPC UA Test ' + (pullMode ? '(Pull)' : '(Subscription)'),
    );

    if (pullMode) {
        builder.addInput('radio', 'adapter_type-pull_mode', '');
        builder.addInput('input', 'undefined-PULLING_INTERVAL-0', '1000');
    } else {
        builder.addInput('radio', 'adapter_type-subscription_mode', '');
    }

    builder
        .addInput('radio', 'access_mode-none', '')
        .addInput('radio', 'opc_host_or_url-url', '')
        .addInput(
            'input',
            'undefined-OPC_SERVER_URL-0',
            'opc.tcp://' + host + ':50000',
        );

    builder.setAutoAddTimestampPropery();

    return builder;
};
