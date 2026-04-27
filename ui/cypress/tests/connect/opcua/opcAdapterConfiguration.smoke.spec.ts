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
import { AdapterBuilder } from '../../../support/builder/AdapterBuilder';
import { TreeStaticPropertyUtils } from '../../../support/utils/userInput/TreeStaticPropertyUtils';
import { ErrorMessageUtils } from '../../../support/utils/ErrorMessageUtils';
import { OpcUaUtils } from '../../../support/utils/connect/OpcUaUtils';

describe('Test OPC-UA Adapter Configuration', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
    });

    it('Test OPC-UA Tree Node Configuration', () => {
        const adapterBuilder = getAdapterBuilder();
        adapterBuilder.addTreeNode(
            OpcUaUtils.createScalarNodeSelection(
                OpcUaUtils.BOOLEAN_NODE,
                OpcUaUtils.INT32_NODE,
            ),
        );

        const adapterInput = adapterBuilder.build();
        OpcUaUtils.setUpInitialConfiguration(adapterInput);

        TreeStaticPropertyUtils.validateAmountOfSelectedNodes(2);

        TreeStaticPropertyUtils.checkThatNodeIsSelectedInTree('Boolean');

        // Test if node details view works
        TreeStaticPropertyUtils.validateAmountOfShownNodeDetailsMetaDataRows(0);
        TreeStaticPropertyUtils.showNodeDetails('Int32');
        TreeStaticPropertyUtils.validateAmountOfShownNodeDetailsMetaDataRows(
            20,
        );
        TreeStaticPropertyUtils.hideNodeDetails('Int32');
        TreeStaticPropertyUtils.validateAmountOfShownNodeDetailsMetaDataRows(0);

        // Test if delete node works
        TreeStaticPropertyUtils.removeSelectedNode(
            'ns=2\\;s=CTT.Static.AllProfiles.Scalar.Boolean',
        );
        TreeStaticPropertyUtils.validateAmountOfSelectedNodes(1);

        // Test clear selection and reload button
        TreeStaticPropertyUtils.clickClearAndReloadButton();
        TreeStaticPropertyUtils.validateAmountOfSelectedNodes(0);
    });

    it('Test OPC-UA Text Editor', () => {
        const adapterInput = getAdapterBuilder().build();
        OpcUaUtils.setUpInitialConfiguration(adapterInput);

        TreeStaticPropertyUtils.treeEditor().should('be.visible');
        TreeStaticPropertyUtils.textEditor().should('not.exist');

        // Switch to text editor
        TreeStaticPropertyUtils.switchToTextEditor();

        // Validate that text editor is shown
        TreeStaticPropertyUtils.treeEditor().should('not.exist');
        TreeStaticPropertyUtils.textEditor().should('be.visible');

        TreeStaticPropertyUtils.typeInTextEditor(
            OpcUaUtils.getNodeId(OpcUaUtils.INT32_NODE),
        );

        // Go back to tree editor and validate nodes are selected and browse editor works
        TreeStaticPropertyUtils.switchToTreeEditor();
        TreeStaticPropertyUtils.validateAmountOfSelectedNodes(1);

        // Check if node is selected
        OpcUaUtils.expandScalarNodeSelectionPath();
        TreeStaticPropertyUtils.checkThatNodeIsSelectedInTree(
            OpcUaUtils.INT32_NODE,
        );
        TreeStaticPropertyUtils.selectNode(OpcUaUtils.BOOLEAN_NODE);

        // Go back tree view and validate that the node is still selected
        TreeStaticPropertyUtils.switchToTextEditor();
        TreeStaticPropertyUtils.getTextInTextEditor().should(
            'contain',
            OpcUaUtils.getNodeId(OpcUaUtils.BOOLEAN_NODE),
        );

        TreeStaticPropertyUtils.getTextInTextEditor().should(
            'contain',
            OpcUaUtils.getNodeId(OpcUaUtils.INT32_NODE),
        );

        TreeStaticPropertyUtils.switchToTreeEditor();
    });

    it('Test OPC-UA Node does not exist', () => {
        const adapterInput = getAdapterBuilder().build();
        OpcUaUtils.setUpInitialConfiguration(adapterInput);

        // Switch to text editor
        TreeStaticPropertyUtils.switchToTextEditor();
        TreeStaticPropertyUtils.typeInTextEditor(
            'ns=2;s=CTT.Static.AllProfiles.Scalar.DoesNotExist',
        );

        ConnectUtils.finishAdapterSettings();

        // validate that an error is shown with node id
        ErrorMessageUtils.containsMessage('DoesNotExist');
    });

    it('Test OPC-UA Wrong Node Id Format', () => {
        const adapterInput = getAdapterBuilder().build();
        OpcUaUtils.setUpInitialConfiguration(adapterInput);

        // Switch to text editor
        TreeStaticPropertyUtils.switchToTextEditor();
        TreeStaticPropertyUtils.typeInTextEditor('NoValidNodeId');

        ConnectUtils.finishAdapterSettings();

        // validate that an error is shown with node id
        ErrorMessageUtils.containsMessage('NoValidNodeId');
    });
});

const getAdapterBuilder = () => {
    return AdapterBuilder.create('OPC_UA')
        .setName('OPC UA Configuration Test')
        .addInput('radio', 'adapter_type-pull_mode', '')
        .addInput(
            'input',
            'ADAPTER_TYPE-pull-mode-group-0-PULLING_INTERVAL-0',
            '1000',
        )
        .addInput('radio', 'securitymode-none', '')
        .addInput('radio', 'userauthentication-anonymous', '')
        .addInput('radio', 'opc_host_or_url-url', '')
        .addInput(
            'input',
            'OPC_HOST_OR_URL-OPC_SERVER_URL-0',
            OpcUaUtils.getEndpointUrl(),
        )
        .setAutoAddTimestampPropery();
};
