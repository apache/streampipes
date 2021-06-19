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
package org.apache.streampipes.container.extensions;

import org.apache.streampipes.connect.api.IAdapter;
import org.apache.streampipes.connect.api.IProtocol;
import org.apache.streampipes.connect.container.worker.management.MasterRestClient;
import org.apache.streampipes.container.init.DeclarersSingleton;
import org.apache.streampipes.service.extensions.base.StreamPipesExtensionsServiceBase;
import org.apache.streampipes.container.init.RunningInstances;
import org.apache.streampipes.container.locales.LabelGenerator;
import org.apache.streampipes.container.model.ExtensionsConfig;
import org.apache.streampipes.dataformat.cbor.CborDataFormatFactory;
import org.apache.streampipes.dataformat.fst.FstDataFormatFactory;
import org.apache.streampipes.dataformat.json.JsonDataFormatFactory;
import org.apache.streampipes.dataformat.smile.SmileDataFormatFactory;
import org.apache.streampipes.messaging.jms.SpJmsProtocolFactory;
import org.apache.streampipes.messaging.kafka.SpKafkaProtocolFactory;
import org.apache.streampipes.messaging.mqtt.SpMqttProtocolFactory;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.adapter.GenericAdapterDescription;
import org.apache.streampipes.model.connect.grounding.ProtocolDescription;
import org.apache.streampipes.model.connect.worker.ConnectWorkerContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableAutoConfiguration
@Import({ ExtensionsResourceConfig.class })
public abstract class ExtensionsModelSubmitter extends StreamPipesExtensionsServiceBase<ExtensionsConfig> {
    private static final Logger LOG =
            LoggerFactory.getLogger(ExtensionsModelSubmitter.class.getCanonicalName());

    public void init(ExtensionsConfig conf) {
        DeclarersSingleton.getInstance().setHostName(conf.getHost());
        DeclarersSingleton.getInstance().setPort(conf.getPort());

        DeclarersSingleton.getInstance().registerDataFormats(
                new JsonDataFormatFactory(),
                new CborDataFormatFactory(),
                new SmileDataFormatFactory(),
                new FstDataFormatFactory());

        DeclarersSingleton.getInstance().registerProtocols(
                new SpKafkaProtocolFactory(),
                new SpMqttProtocolFactory(),
                new SpJmsProtocolFactory());

        LOG.info("Starting StreamPipes Extensions Bundle");
        SpringApplication app = new SpringApplication(ExtensionsModelSubmitter.class);
        app.setDefaultProperties(Collections.singletonMap("server.port", conf.getPort()));
        app.run();

        String backendUrl = "http://" + conf.getBackendHost() + ":" + conf.getBackendPort() + "/streampipes-backend";
        String adapterUrl = "http://" + conf.getHost() + ":" + conf.getPort() + "/";

        boolean connected = false;
        while (!connected) {
            LOG.info("Trying to connect to master in backend: " + backendUrl);
            connected = MasterRestClient.register(backendUrl, getContainerDescription(adapterUrl));

            if (!connected) {
                LOG.info("Retrying in 5 seconds");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        LOG.info("Successfully registered adapter at master in backend: " + backendUrl);
        // TODO remove after implementation of STREAMPIPES-319
//        ConsulUtil.registerPeService(
//                conf.getId(),
//                conf.getHost(),
//                conf.getPort()
//        );
    }

    private ConnectWorkerContainer getContainerDescription(String endpointUrl) {
        List<AdapterDescription> adapters = new ArrayList<>();
        for (IAdapter<?> a : DeclarersSingleton.getInstance().getAllAdapters()) {
            AdapterDescription desc = (AdapterDescription) rewrite(a.declareModel(), endpointUrl);
            adapters.add(desc);
        }

        List<ProtocolDescription> protocols = new ArrayList<>();
        for (IProtocol p : DeclarersSingleton.getInstance().getAllProtocols()) {
            ProtocolDescription desc = (ProtocolDescription) rewrite(p.declareModel(), endpointUrl);
            protocols.add(desc);
        }

        ConnectWorkerContainer result = new ConnectWorkerContainer(endpointUrl, protocols, adapters);
        return result;
    }

    private NamedStreamPipesEntity rewrite(NamedStreamPipesEntity entity, String endpointUrl) {
        if (!(entity instanceof GenericAdapterDescription)) {
            if (entity instanceof  ProtocolDescription) {
                entity.setElementId(endpointUrl +  "protocol/" + entity.getElementId());
            } else if (entity instanceof  AdapterDescription) {
                entity.setElementId(endpointUrl + "adapter/" + entity.getElementId());
            }
        }

        // TODO remove after full internationalization support has been implemented
        if (entity.isIncludesLocales()) {
            LabelGenerator lg = new LabelGenerator(entity);
            try {
                entity = lg.generateLabels();
            } catch (IOException e) {
                LOG.error("Could not load labels for: " + entity.getAppId());
            }
        }
        return entity;
    }

    @PreDestroy
    public void onExit() {
        LOG.info("Shutting down StreamPipes extensions container...");
        int runningInstancesCount = RunningInstances.INSTANCE.getRunningInstancesCount();

        while (runningInstancesCount > 0) {
            LOG.info("Waiting for {} running pipeline elements to be stopped...", runningInstancesCount);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                LOG.error("Could not pause current thread...");
            }
            runningInstancesCount = RunningInstances.INSTANCE.getRunningInstancesCount();
        }
    }

}
