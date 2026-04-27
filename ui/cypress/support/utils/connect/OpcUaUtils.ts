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

import { AdapterInput } from '../../model/AdapterInput';
import { ConnectUtils } from './ConnectUtils';
import { ErrorMessageUtils } from '../ErrorMessageUtils';
import { StaticPropertyUtils } from '../userInput/StaticPropertyUtils';
import { TreeStaticPropertyUtils } from '../userInput/TreeStaticPropertyUtils';
import { TreeNodeUserInputBuilder } from '../../builder/TreeNodeUserInputBuilder';
import { AdapterBuilder } from '../../builder/AdapterBuilder';
import { ParameterUtils } from '../ParameterUtils';

export class OpcUaUtils {
    public static readonly BOOLEAN_NODE = 'Boolean';
    public static readonly INT32_NODE = 'Int32';
    public static readonly STRING_NODE = 'String';
    public static readonly UINT32_NODE = 'UInt32';

    public static setUpInitialConfiguration(adapterInput: AdapterInput) {
        ConnectUtils.goToConnect();
        ConnectUtils.goToNewAdapterPage();
        ConnectUtils.selectAdapter(adapterInput.adapterType);

        // Wait for the first static property to be rendered
        cy.dataCy(adapterInput.adapterConfiguration[0].selector, {
            timeout: 10000,
        })
            .scrollIntoView()
            .should('be.visible');

        // Validate that no error is not shown when nothing is configured
        cy.dataCy('reloading-nodes', { timeout: 3000 }).should('not.exist');
        ErrorMessageUtils.getExceptionComponent().should('not.exist');

        // The opc ua tests started to become flaky, therefore I split up the configurations of tree and non tree configs
        // First the adapter is configured, then the tree is loaded before the tree configurations are added
        const nonTreeConfigs = adapterInput.adapterConfiguration.filter(
            config => config.type !== 'tree',
        );
        StaticPropertyUtils.input(nonTreeConfigs);

        this.reloadTreeNodeSelection();

        const treeConfigs = adapterInput.adapterConfiguration.filter(
            config => config.type === 'tree',
        );
        StaticPropertyUtils.input(treeConfigs);
    }

    public static reloadTreeNodeSelection() {
        cy.dataCy('reloading-nodes', { timeout: 10000 }).should('not.exist');
        cy.dataCy('reload-tree-node-selection-btn').click();
        cy.dataCy('reloading-nodes', { timeout: 10000 }).should('exist');
        cy.dataCy('reloading-nodes', { timeout: 10000 }).should('not.exist');
    }

    public static createScalarNodeSelection(...leafNodes: string[]) {
        return TreeNodeUserInputBuilder.create(
            'Objects',
            TreeNodeUserInputBuilder.create(
                'Demo',
                TreeNodeUserInputBuilder.create(
                    'Dynamic',
                    ...leafNodes.map(node =>
                        TreeNodeUserInputBuilder.create(node),
                    ),
                ),
            ),
        );
    }

    public static expandScalarNodeSelectionPath() {
        ['Objects', 'Demo', 'Dynamic'].forEach(node =>
            TreeStaticPropertyUtils.expandNode(node),
        );
    }

    public static getEndpointUrl() {
        const host: string = ParameterUtils.get('localhost', 'opcua');
        return 'opc.tcp://' + host + ':4840/milo';
    }

    public static getNodeId(nodeName: string) {
        return `ns=2;s=Demo.Dynamic.${nodeName}`;
    }

    public static getAdapterBuilderWithTreeNodes(pullMode: boolean) {
        const builder = OpcUaUtils.getBaseAdapterConfigBuilder(pullMode);
        builder.addTreeNode(
            OpcUaUtils.createScalarNodeSelection(
                OpcUaUtils.BOOLEAN_NODE,
                OpcUaUtils.INT32_NODE,
                OpcUaUtils.STRING_NODE,
                OpcUaUtils.UINT32_NODE,
            ),
        );

        return builder.build();
    }

    public static getBaseAdapterConfigBuilder(
        pullMode: boolean,
    ): AdapterBuilder {
        const builder = AdapterBuilder.create('OPC_UA').setName('OPC UA Test');

        if (pullMode) {
            builder.addInput('radio', 'adapter_type-pull_mode', '');
            builder.addInput(
                'input',
                'ADAPTER_TYPE-pull-mode-group-0-PULLING_INTERVAL-0',
                '1000',
            );
        } else {
            builder.addInput('radio', 'adapter_type-subscription_mode', '');
        }

        builder
            .addInput('radio', 'securitymode-none', '')
            .addInput('radio', 'userauthentication-anonymous', '')
            .addInput('radio', 'opc_host_or_url-url', '')
            .addInput(
                'input',
                'OPC_HOST_OR_URL-OPC_SERVER_URL-0',
                OpcUaUtils.getEndpointUrl(),
            );

        builder.setAutoAddTimestampPropery();
        builder.setTimestampProperty('timestamp');

        return builder;
    }
}
