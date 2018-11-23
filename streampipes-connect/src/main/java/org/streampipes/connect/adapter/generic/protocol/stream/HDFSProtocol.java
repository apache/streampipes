/*
Copyright 2018 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package org.streampipes.connect.adapter.generic.protocol.stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.connect.SendToPipeline;
import org.streampipes.connect.adapter.generic.format.Format;
import org.streampipes.connect.adapter.generic.format.Parser;
import org.streampipes.connect.adapter.generic.pipeline.AdapterPipeline;
import org.streampipes.connect.adapter.generic.protocol.Protocol;
import org.streampipes.connect.adapter.generic.sdk.ParameterExtractor;
import org.streampipes.model.connect.grounding.ProtocolDescription;
import org.streampipes.model.connect.guess.GuessSchema;
import org.streampipes.model.staticproperty.AnyStaticProperty;
import org.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.streampipes.model.staticproperty.Option;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class HDFSProtocol extends Protocol {

    public static final String ID = "https://streampipes.org/vocabulary/v1/protocol/stream/HDFS";

    private static String INTERVAL_PROPERTY = "intervalProperty";
    private static String URL_PROPERTY = "urlProperty";
    private static String USER_PROPERTY = "userProperty";
    private static String PASSWORD_PROPERTY = "passwordProperty";
    private static String DATA_PATH_PROPERTY = "dataPathProperty";
    private static String RECURSIVELY_PROPERTY = "recursively";
    private static String OPTIONS = "optionsFile";

    private long intervalProperty;
    private String dataPathProperty;
    private String urlProperty;
    private String userProperty;
    private String passwordProperty;
    private boolean recursively;

    private ScheduledExecutorService scheduler;
    private Logger logger = LoggerFactory.getLogger(HDFSProtocol.class);


    public HDFSProtocol() {

    }

    public HDFSProtocol(Parser parser, Format format, long intervalProperty, String dataPathProperty, String urlProperty, boolean recursively) {
        super(parser, format);
        this.intervalProperty = intervalProperty;
        this.dataPathProperty = dataPathProperty;
        this.urlProperty = urlProperty;
        this.recursively = recursively;
    }

    @Override
    public Protocol getInstance(ProtocolDescription protocolDescription, Parser parser, Format format) {
        ParameterExtractor extractor = new ParameterExtractor(protocolDescription.getConfig());
        long intervalProperty = Long.parseLong(extractor.singleValue(INTERVAL_PROPERTY));
        String urlProperty = extractor.singleValue(URL_PROPERTY);
    //    String userProperty = extractor.singleValue(USER_PROPERTY);
    //    String passwordProperty = extractor.singleValue(PASSWORD_PROPERTY);
        String dataPathProperty = extractor.singleValue(DATA_PATH_PROPERTY);

        boolean recursively = extractor.selectedMultiValues(RECURSIVELY_PROPERTY).stream()
                .anyMatch(o -> o.equals("recursively"));

        return new HDFSProtocol();

    }

    @Override
    public ProtocolDescription declareModel() {
        ProtocolDescription pd = new ProtocolDescription(ID, "HDFS", "This is the " +
                "description for the HDFS Stream protocol.");

        FreeTextStaticProperty intervalProperty = new FreeTextStaticProperty(INTERVAL_PROPERTY, "Interval", "This property " +
                "defines the pull interval in seconds.");
        FreeTextStaticProperty urlProperty = new FreeTextStaticProperty(URL_PROPERTY, "HDFS-Server URL",
               "This property defines the path to the file.");
    //     FreeTextStaticProperty userNameProperty = new FreeTextStaticProperty(USER_PROPERTY, "Username",
    //            "The Username to login");
    //    FreeTextStaticProperty passwordProperty = new FreeTextStaticProperty(PASSWORD_PROPERTY, "Password",
    //            "The Password to login");
        FreeTextStaticProperty dataPathProperty = new FreeTextStaticProperty(DATA_PATH_PROPERTY, "Data Path",
                "The Data Path which should be watched");


        AnyStaticProperty offset = new AnyStaticProperty(OPTIONS, "Options for Folders", "");
        offset.setOptions(Arrays.asList(new Option("Search Recursively","recursively")));


        pd.setSourceType("STREAM");

     //   pd.setIconUrl("ftp.png");
        pd.addConfig(urlProperty);
        pd.addConfig(intervalProperty);
     //   pd.addConfig(userNameProperty);
    //    pd.addConfig(passwordProperty);
        pd.addConfig(dataPathProperty);
    //    pd.addConfig(repeatProperty);

        return pd;
    }

    @Override
    public GuessSchema getGuessSchema() {
        return null;
    }

    @Override
    public List<Map<String, Object>> getNElements(int n) {
        return null;
    }

    @Override
    public void run(AdapterPipeline adapterPipeline) {
        final Runnable errorThread = () -> {
            executeProtocolLogic(adapterPipeline);
        };


        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(errorThread, 0, TimeUnit.MILLISECONDS);

    }


    private void executeProtocolLogic(AdapterPipeline adapterPipeline) {
        final Runnable task = () -> {
            SendToPipeline stk = new SendToPipeline(format, adapterPipeline);
        //    InputStream data = getDataFromEndpoint();
        // TODO Get Data
        // 1. Get File/Files Name/Path from HDSF
        // 2. Create InputStream for File
        //    parser.parse(data, stk);
        };

        scheduler = Executors.newScheduledThreadPool(1);
        ScheduledFuture<?> handle = scheduler.scheduleAtFixedRate(task, 1, this.intervalProperty, TimeUnit.SECONDS);
        try {
            handle.get();
        } catch (ExecutionException e ) {
            logger.error("Error", e);
        } catch (InterruptedException e) {
            logger.error("Error", e);
        }
    }


    @Override
    public void stop() {
        scheduler.shutdownNow();
    }

    @Override
    public String getId() {
        return ID;
    }
}
