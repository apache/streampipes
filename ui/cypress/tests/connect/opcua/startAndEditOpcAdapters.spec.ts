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

import { ConnectUtils } from '../../../support/utils/connect/ConnectUtils';
import { ParameterUtils } from '../../../support/utils/ParameterUtils';
import { AdapterBuilder } from '../../../support/builder/AdapterBuilder';
import { TreeNodeUserInputBuilder } from '../../../support/builder/TreeNodeUserInputBuilder';
import { ConnectBtns } from '../../../support/utils/connect/ConnectBtns';
import { TreeStaticPropertyUtils } from '../../../support/utils/userInput/TreeStaticPropertyUtils';
import { ConnectEventSchemaUtils } from '../../../support/utils/connect/ConnectEventSchemaUtils';
import { AdapterInput } from '../../../support/model/AdapterInput';

describe('Test starting and editing OPC-UA Adapters in different configurations', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
    });

    it('Create OPC-UA Adapter Tree Editor Pull Mode', () => {
        const adapterInput = getAdapterBuilderWithTreeNodes(true);
        startAdapterTest(adapterInput);
    });

    it('Create OPC-UA Adapter Tree Editor Subscription Mode', () => {
        const adapterInput = getAdapterBuilderWithTreeNodes(false);
        startAdapterTest(adapterInput);
    });

    it('Create OPC-UA Adapter Text Editor Pull Mode', () => {
        const adapterInput = getAdapterBuilderWithTextNodes(true);
        startAdapterTest(adapterInput);
    });

    it('Create OPC-UA Adapter Text Editor Subscription Mode', () => {
        const adapterInput = getAdapterBuilderWithTextNodes(false);
        startAdapterTest(adapterInput);
    });

    it('Edit OPC-UA Adapter created with Tree editor', () => {
        const adapterInput = getAdapterBuilderWithTreeNodes(true);

        editAdapterTest(adapterInput);
    });

    it('Edit OPC-UA Adapter created with Text editor', () => {
        const adapterInput = getAdapterBuilderWithTextNodes(true);

        editAdapterTest(adapterInput);
    });
});

const getAdapterBuilderWithTreeNodes = (pullMode: boolean) => {
    const builder = getBaseAdapterConfigBuilder(pullMode);
    builder.addTreeNode(
        TreeNodeUserInputBuilder.create(
            'Objects',
            TreeNodeUserInputBuilder.create(
                'OpcPlc',
                TreeNodeUserInputBuilder.create(
                    'Telemetry',
                    TreeNodeUserInputBuilder.create('Basic').addChildren(
                        TreeNodeUserInputBuilder.create('AlternatingBoolean'),
                        TreeNodeUserInputBuilder.create('StepUp'),
                        TreeNodeUserInputBuilder.create('RandomSignedInt32'),
                        TreeNodeUserInputBuilder.create('RandomUnsignedInt32'),
                    ),
                ),
            ),
        ),
    );

    return builder.build();
};

/**
 * The start adapter test expects an adapter input with the same schema
 * description for all tests. Only the opc ua related options might differ.
 */
const startAdapterTest = (adapterInput: AdapterInput) => {
    ConnectUtils.testAdapter(adapterInput);
    ConnectUtils.validateEventsInPreview(5);
};

/**
 * The edit adapter test expects an adapter input with the same schema
 * description for all tests. Only the opc ua related options might differ.
 */
const editAdapterTest = (adapterInput: AdapterInput) => {
    ConnectUtils.testAdapter(adapterInput);

    ConnectBtns.editAdapter().click();

    // Validate that browse nodes are shown
    TreeStaticPropertyUtils.validateAmountOfShownBrowseNodes(3);

    // Remove a node and validate that resulting events do not contain the property
    TreeStaticPropertyUtils.removeSelectedNode('ns=3;s=RandomUnsignedInt32');
    ConnectUtils.finishAdapterSettings();
    ConnectEventSchemaUtils.finishEventSchemaConfiguration();
    ConnectBtns.storeEditAdapter().click();
    ConnectUtils.closeAdapterPreview();
    ConnectUtils.validateEventsInPreview(4);
};

const getAdapterBuilderWithTextNodes = (pullMode: boolean) => {
    const builder = getBaseAdapterConfigBuilder(pullMode);
    builder.addTreeNode(
        TreeNodeUserInputBuilder.create(
            'ns=3;s=AlternatingBoolean',
        ).isTextConfig(),
    );
    builder.addTreeNode(
        TreeNodeUserInputBuilder.create('ns=3;s=StepUp').isTextConfig(),
    );
    builder.addTreeNode(
        TreeNodeUserInputBuilder.create(
            'ns=3;s=RandomSignedInt32',
        ).isTextConfig(),
    );
    builder.addTreeNode(
        TreeNodeUserInputBuilder.create(
            'ns=3;s=RandomUnsignedInt32',
        ).isTextConfig(),
    );

    return builder.build();
};

const getBaseAdapterConfigBuilder = (pullMode: boolean): AdapterBuilder => {
    const host: string = ParameterUtils.get('localhost', 'opcua');

    const builder = AdapterBuilder.create('OPC_UA').setName('OPC UA Test');

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
