/*
 * Copyright 2019 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.sinks.databases.jvm.opcua;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfig;
import org.eclipse.milo.opcua.stack.client.UaTcpStackClient;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.types.builtin.*;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;
import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.logging.api.Logger;
import org.streampipes.model.runtime.Event;
import org.streampipes.wrapper.context.EventSinkRuntimeContext;
import org.streampipes.wrapper.runtime.EventSink;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class OpcUa implements EventSink<OpcUaParameters> {

	private static Logger LOG;

	private OpcUaClient opcUaClient;
	private OpcUaParameters params;
	private String serverUrl;
	private NodeId node;

	@Override
	public void onInvocation(OpcUaParameters parameters, EventSinkRuntimeContext runtimeContext) throws
			SpRuntimeException {
		LOG = parameters.getGraph().getLogger(OpcUa.class);

		serverUrl = "opc.tcp://" + parameters.getHostName() + ":" + parameters.getPort();

		node = new NodeId(parameters.getNameSpaceIndex(), parameters.getNodeId());
		this.params = parameters;

		EndpointDescription[] endpoints;

		try {
			endpoints = UaTcpStackClient.getEndpoints(serverUrl).get();

			EndpointDescription endpoint = null;

			endpoint = Arrays.stream(endpoints)
					.filter(e -> e.getSecurityPolicyUri().equals(SecurityPolicy.None.getSecurityPolicyUri()))
					.findFirst().orElseThrow(() -> new Exception("no desired endpoints returned"));

			OpcUaClientConfig config = OpcUaClientConfig.builder()
					.setApplicationName(LocalizedText.english("eclipse milo opc-ua client"))
					.setApplicationUri("urn:eclipse:milo:examples:client")
					.setEndpoint(endpoint)
					.build();

			new OpcUaClient(config);
			opcUaClient = new OpcUaClient(config);
			opcUaClient.connect().get();

		} catch (Exception e) {
			throw new SpRuntimeException("Could not connect to OPC-UA server: " + serverUrl);
		}

	}

	@Override
	public void onEvent(Event inputEvent) {

		// TODO how to handle multiple data types
	  Double fieldValue = inputEvent.getFieldBySelector(this.params.getNumberMapping()).getAsPrimitive().getAsDouble();

	  Variant v = new Variant(fieldValue);
		DataValue value = new DataValue(v);
		CompletableFuture<StatusCode> f =	opcUaClient.writeValue(node, value);

		try {
			StatusCode status = f.get();
			if (status.isBad()) {
			    LOG.error("Value: " + fieldValue + " could not be written to node Id: " + this.params.getNodeId() + " on " +
							"OPC-UA server: " + this.serverUrl);
			}
		} catch (InterruptedException | ExecutionException e) {
			LOG.error("Exception: Value: " + fieldValue + " could not be written to node Id: " + this.params.getNodeId() + " on " +
							"OPC-UA server: " + this.serverUrl);
		}
	}

	@Override
	public void onDetach() throws SpRuntimeException {
		opcUaClient.disconnect();
	}


}
