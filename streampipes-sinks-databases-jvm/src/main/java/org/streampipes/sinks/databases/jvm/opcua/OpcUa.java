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
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;
import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.logging.api.Logger;
import org.streampipes.model.runtime.Event;
import org.streampipes.model.runtime.field.PrimitiveField;
import org.streampipes.vocabulary.XSD;
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

		Variant v = getValue(inputEvent);

		if (v == null) {
			LOG.error("Mapping property type: " + this.params.getMappingPropertyType() + " is not supported");
		} else {

			DataValue value = new DataValue(v);
			CompletableFuture<StatusCode> f = opcUaClient.writeValue(node, value);

			try {
				StatusCode status = f.get();
				if (status.isBad()) {
					if (status.getValue() == 0x80740000L) {
						LOG.error("Type missmatch! Tried to write value of type: " + this.params.getMappingPropertyType() + " but server did not accept this");
					} else if (status.getValue() == 0x803B0000L) {
						LOG.error("Wrong access level. Not allowed to write to nodes");
					}
					LOG.error("Value: " + value.getValue().toString() + " could not be written to node Id: " + this.params.getNodeId() + " on " +
							"OPC-UA server: " + this.serverUrl);
				}
			} catch (InterruptedException | ExecutionException e) {
				LOG.error("Exception: Value: " + value.getValue().toString() + " could not be written to node Id: " + this.params.getNodeId() + " on " +
						"OPC-UA server: " + this.serverUrl);
			}
		}
	}

	@Override
	public void onDetach() throws SpRuntimeException {
		opcUaClient.disconnect();
	}

	private Variant getValue(Event inputEvent) {
		Variant result = null;
		String mappingType = this.params.getMappingPropertyType();
		PrimitiveField propertyPrimitive = inputEvent.getFieldBySelector(this.params.getMappingPropertySelector()).getAsPrimitive();

		if (mappingType.equals(XSD._integer.toString())){
			result = new Variant(propertyPrimitive.getAsInt());
		} else if (mappingType.equals(XSD._double.toString())){
			result = new Variant(propertyPrimitive.getAsDouble());
		} else if (mappingType.equals(XSD._boolean.toString())){
			result = new Variant(propertyPrimitive.getAsBoolean());
		} else if (mappingType.equals(XSD._float.toString())){
			result = new Variant(propertyPrimitive.getAsFloat());
		} else if (mappingType.equals(XSD._string.toString())){
			result = new Variant(propertyPrimitive.getAsString());
		}

		return result;
	}


	public static void main(String... args) throws Exception {
		String serverUrl = "opc.tcp://141.21.43.39:4840";

//		NodeId node = NodeId.parse("|var|CODESYSControlforRaspberryPiSL.Application.PLC_PRG.auto_rot");
//
		NodeId node = new NodeId(4, "|var|CODESYS Control for Raspberry Pi SL.Application.PLC_PRG.new");


		EndpointDescription[] endpoints;

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
		OpcUaClient	opcUaClient = new OpcUaClient(config);
		opcUaClient.connect().get();

		Variant v = new Variant(new Boolean(true));

		DataValue value = new DataValue(v);
		CompletableFuture<StatusCode> f = opcUaClient.writeValue(node, value);
		StatusCode status = f.get();

		if (status.isBad()) {
			System.out.println(status);

			System.out.println("Did not work");

		}


		CompletableFuture<DataValue> va1 = opcUaClient.readValue(0, TimestampsToReturn.Both, node);

		System.out.println("Auto grün: " + va1.get().getValue());

	}
}
