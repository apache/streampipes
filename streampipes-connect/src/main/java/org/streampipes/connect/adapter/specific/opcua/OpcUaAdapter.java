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

package org.streampipes.connect.adapter.specific.opcua;

import org.eclipse.milo.opcua.stack.core.types.structured.ReferenceDescription;
import org.streampipes.connect.adapter.Adapter;
import org.streampipes.connect.adapter.specific.SpecificDataStreamAdapter;
import org.streampipes.connect.exception.AdapterException;
import org.streampipes.connect.exception.ParseException;
import org.streampipes.model.connect.adapter.SpecificAdapterStreamDescription;
import org.streampipes.model.connect.guess.GuessSchema;
import org.streampipes.model.schema.EventPropertyPrimitive;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.streampipes.model.staticproperty.StaticProperty;
import org.streampipes.sdk.builder.adapter.SpecificDataStreamAdapterBuilder;
import org.streampipes.sdk.helpers.Labels;

import java.util.ArrayList;
import java.util.List;

public class OpcUaAdapter extends SpecificDataStreamAdapter {

    public static final String ID = "http://streampipes.org/adapter/specific/opcua";


    private static final String OPC_SERVER_HOST = "OPC_SERVER_HOST";
    private static final String OPC_SERVER_PORT = "OPC_SERVER_PORT";
    private static final String NAMESPACE_INDEX = "NAMESPACE_INDEX";
    private static final String NODE_ID = "NODE_ID";

    private String opcUaServer;
    private String namespaceIndex;
    private String nodeId;
    private String port;


    public OpcUaAdapter() {
    }

    public OpcUaAdapter(SpecificAdapterStreamDescription adapterDescription) {
        super(adapterDescription);

        getConfigurations(adapterDescription);

    }

    @Override
    public SpecificAdapterStreamDescription declareModel() {

        SpecificAdapterStreamDescription description = SpecificDataStreamAdapterBuilder.create(ID, "OPC UA", "Read values form an opc ua server")
                .iconUrl("opc.jpg")
                .requiredTextParameter(Labels.from(OPC_SERVER_HOST, "OPC Server", "URL of the OPC UA server. No leading opc.tcp://"))
                .requiredTextParameter(Labels.from(NAMESPACE_INDEX, "Namespace Index", "Index of the Namespace of the node"))
                .requiredTextParameter(Labels.from(NODE_ID, "Node Id", "Id of the Node to read the values from"))
                .build();
        description.setAppId(ID);


        return  description;
    }

    @Override
    public void startAdapter() throws AdapterException {

    }

    @Override
    public void stopAdapter() throws AdapterException {

    }

    @Override
    public Adapter getInstance(SpecificAdapterStreamDescription adapterDescription) {
        return new OpcUaAdapter(adapterDescription);
    }

    public static void main(String... args) throws AdapterException {
        List<StaticProperty> all = new ArrayList<>();
        FreeTextStaticProperty p1 = new FreeTextStaticProperty(OPC_SERVER_HOST, "", "");
        p1.setValue("192.168.0.144");
        all.add(p1);

        FreeTextStaticProperty p2 = new FreeTextStaticProperty(NODE_ID, "", "");
        p2.setValue("|var|CODESYS Control for Raspberry Pi SL.Application.PLC_PRG");
        all.add(p2);

        FreeTextStaticProperty p3 = new FreeTextStaticProperty(NAMESPACE_INDEX, "", "");
        p3.setValue("4");
        all.add(p3);

        FreeTextStaticProperty p4 = new FreeTextStaticProperty(OPC_SERVER_PORT, "", "");
        p4.setValue("4840");
        all.add(p4);


        SpecificAdapterStreamDescription description = new SpecificAdapterStreamDescription();
        description.setConfig(all);

        OpcUaAdapter opcUaAdapter = new OpcUaAdapter(description);
        opcUaAdapter.getSchema(description);
    }

    @Override
    public GuessSchema getSchema(SpecificAdapterStreamDescription adapterDescription) throws AdapterException, ParseException {

        GuessSchema guessSchema = new GuessSchema();
        EventSchema eventSchema = new EventSchema();

        getConfigurations(adapterDescription);

        OpcUa opc = new OpcUa(opcUaServer, Integer.parseInt(port), Integer.parseInt(namespaceIndex), nodeId);
        try {
            opc.connect();
            List<ReferenceDescription> res =  opc.browseNode();

            for (ReferenceDescription r : res) {
                EventPropertyPrimitive ep = new EventPropertyPrimitive();
                ep.setRuntimeType(r.getBrowseName().getName());
                System.out.println(r.toString());
            }

            opc.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;
    }

    @Override
    public String getId() {
        return ID;
    }

    private void getConfigurations(SpecificAdapterStreamDescription adapterDescription) {
        List<StaticProperty> all = adapterDescription.getConfig();

        for (StaticProperty sp : all) {
            if (sp.getInternalName().equals(OPC_SERVER_HOST)) {
                this.opcUaServer = ((FreeTextStaticProperty) sp).getValue();
            } else if (sp.getInternalName().equals(OPC_SERVER_PORT)) {
                this.port = ((FreeTextStaticProperty) sp).getValue();
            } else if (sp.getInternalName().equals(NAMESPACE_INDEX)) {
                this.namespaceIndex = ((FreeTextStaticProperty) sp).getValue();
            }else {
                this.nodeId = ((FreeTextStaticProperty) sp).getValue();
            }

        }
    }
}
