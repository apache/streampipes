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
import { TreeNodeUserInputBuilder } from '../../builder/TreeNodeUserInputBuilder';
import { AdapterBuilder } from '../../builder/AdapterBuilder';
import { ParameterUtils } from '../ParameterUtils';

export class OpcUaUtils {
    public static setUpInitialConfiguration(adapterInput: AdapterInput) {
        ConnectUtils.goToConnect();
        ConnectUtils.goToNewAdapterPage();
        ConnectUtils.selectAdapter(adapterInput.adapterType);

        // Wait for the first static property to be rendered
        cy.dataCy(adapterInput.adapterConfiguration[0].selector).should(
            'be.visible',
        );
        // Validate that no error is not shown when nothing is configured
        cy.dataCy('reloading-nodes', { timeout: 3000 }).should('not.exist');
        ErrorMessageUtils.getExceptionComponent().should('not.exist');

        StaticPropertyUtils.input(adapterInput.adapterConfiguration);
    }

    public static getAdapterBuilderWithTreeNodes(pullMode: boolean) {
        const builder = OpcUaUtils.getBaseAdapterConfigBuilder(pullMode);
        builder.addTreeNode(
            TreeNodeUserInputBuilder.create(
                'Objects',
                TreeNodeUserInputBuilder.create(
                    'OpcPlc',
                    TreeNodeUserInputBuilder.create(
                        'Telemetry',
                        TreeNodeUserInputBuilder.create('Basic').addChildren(
                            TreeNodeUserInputBuilder.create(
                                'AlternatingBoolean',
                            ),
                            TreeNodeUserInputBuilder.create('StepUp'),
                            TreeNodeUserInputBuilder.create(
                                'RandomSignedInt32',
                            ),
                            TreeNodeUserInputBuilder.create(
                                'RandomUnsignedInt32',
                            ),
                        ),
                    ),
                ),
            ),
        );

        return builder.build();
    }

    public static getBaseAdapterConfigBuilder(
        pullMode: boolean,
    ): AdapterBuilder {
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
    }
}
