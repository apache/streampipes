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
import { StaticPropertyUtils } from '../../support/utils/userInput/StaticPropertyUtils';
import { TreeStaticPropertyUtils } from '../../support/utils/userInput/TreeStaticPropertyUtils';

describe('Test OPC-UA Adapter Configuration', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
    });

    it('Test OPC-UA Tree Node Configuration', () => {
        const adapterBuilder = getAdapterBuilder();
        adapterBuilder.addTreeNode(
            TreeNodeBuilder.create(
                'Objects',
                TreeNodeBuilder.create(
                    'OpcPlc',
                    TreeNodeBuilder.create(
                        'Telemetry',
                        TreeNodeBuilder.create('Basic').addChildren(
                            TreeNodeBuilder.create('AlternatingBoolean'),
                            TreeNodeBuilder.create('StepUp'),
                        ),
                    ),
                ),
            ),
        );

        const adapterConfiguration = adapterBuilder.build();

        // Set up initial configuration
        ConnectUtils.goToConnect();
        ConnectUtils.goToNewAdapterPage();
        ConnectUtils.selectAdapter(adapterConfiguration.adapterType);
        StaticPropertyUtils.input(adapterConfiguration.adapterConfiguration);

        TreeStaticPropertyUtils.validateAmountOfSelectedNodes(2);

        TreeStaticPropertyUtils.checkThatNodeIsSelectedInTree(
            'AlternatingBoolean',
        );

        // Test if node details view works
        TreeStaticPropertyUtils.validateAmountOfShownNodeDetailsMetaDataRows(0);
        TreeStaticPropertyUtils.showNodeDetails('StepUp');
        TreeStaticPropertyUtils.validateAmountOfShownNodeDetailsMetaDataRows(
            10,
        );
        TreeStaticPropertyUtils.hideNodeDetails('StepUp');
        TreeStaticPropertyUtils.validateAmountOfShownNodeDetailsMetaDataRows(0);

        // Test if delete node works
        TreeStaticPropertyUtils.removeSelectedNode(
            'ns=3\\;s=AlternatingBoolean',
        );
        TreeStaticPropertyUtils.validateAmountOfSelectedNodes(1);

        // Test clear selection and reload button
        TreeStaticPropertyUtils.clickClearAndReloadButton();
        TreeStaticPropertyUtils.validateAmountOfSelectedNodes(0);
    });

    it('Test OPC-UA Code Editor', () => {
        const adapterConfiguration = getAdapterBuilder().build();

        // Set up initial configuration
        ConnectUtils.goToConnect();
        ConnectUtils.goToNewAdapterPage();
        ConnectUtils.selectAdapter(adapterConfiguration.adapterType);
        StaticPropertyUtils.input(adapterConfiguration.adapterConfiguration);


        TreeStaticPropertyUtils.treeEditor().should('be.visible');
        TreeStaticPropertyUtils.textEditor().should('not.exist');

        // Switch to text editor
        TreeStaticPropertyUtils.switchToTextEditor();

        // Validate that text editor is shown
        TreeStaticPropertyUtils.treeEditor().should('not.exist');
        TreeStaticPropertyUtils.textEditor().should('be.visible');

        TreeStaticPropertyUtils.typeInTextEditor('ns=3;s=StepUp');

    });
});

const getAdapterBuilder = () => {
    const host: string = ParameterUtils.get('localhost', 'opcua');

    const builder = AdapterBuilder.create('OPC_UA')
        .setName('OPC UA Configuration Test')
        .addInput('radio', 'adapter_type-pull_mode', '')
        .addInput('input', 'undefined-PULLING_INTERVAL-0', '1000')
        .addInput('radio', 'access_mode-none', '')
        .addInput('radio', 'opc_host_or_url-url', '')
        .addInput(
            'input',
            'undefined-OPC_SERVER_URL-0',
            'opc.tcp://' + host + ':50000',
        )

        .setAutoAddTimestampPropery();

    return builder;
};
