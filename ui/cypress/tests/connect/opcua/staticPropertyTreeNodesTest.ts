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
import { ConnectEventSchemaUtils } from '../../../support/utils/connect/ConnectEventSchemaUtils';
import { OpcUaUtils } from '../../../support/utils/connect/OpcUaUtils';

describe('Test Tree Node Configuration', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
    });

    /**
     * This test creates an OPC UA adapter and ensures that no node information is sent to the backend,
     * to reduce the size of the stored adapter.
     */
    it('Tree nodes should not be submitted to the backend', () => {
        const adapterInput = OpcUaUtils.getAdapterBuilderWithTreeNodes(true);

        OpcUaUtils.setUpInitialConfiguration(adapterInput);

        cy.intercept(
            'POST',
            'streampipes-backend/api/v2/connect/master/guess/schema',
        ).as('guessSchema');

        ConnectUtils.finishAdapterSettings();

        cy.wait('@guessSchema').then(interception => {
            validateNodesAreEmptyInAdapterDescriptionBody(interception);
        });

        cy.intercept(
            'POST',
            'streampipes-backend/api/v2/connect/master/adapters',
        ).as('startAdapter');

        ConnectEventSchemaUtils.addTimestampProperty();
        ConnectUtils.finishEventSchemaConfiguration();
        ConnectUtils.startAdapter(adapterInput);

        cy.wait('@startAdapter').then(interception => {
            validateNodesAreEmptyInAdapterDescriptionBody(interception);
        });
    });
});

/**
 * This method intercepts the http requests, validates that it was successfully.
 * Further it validates that the client does not send node information to the backend.
 */
const validateNodesAreEmptyInAdapterDescriptionBody = interception => {
    expect(interception.response.statusCode).to.equal(200);

    const adapterDescription = interception.request.body;
    console.log(adapterDescription);

    const runtimeResolvableTreeInput = adapterDescription.config.find(
        (configItem: any) =>
            configItem['@class'] ===
            'org.apache.streampipes.model.staticproperty.RuntimeResolvableTreeInputStaticProperty',
    );

    expect(runtimeResolvableTreeInput).to.exist;

    expect(runtimeResolvableTreeInput.nodes).to.be.an('array').that.is.empty;

    expect(runtimeResolvableTreeInput.latestFetchedNodes).to.be.an('array').that
        .is.empty;
};
