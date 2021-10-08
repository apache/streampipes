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

package org.apache.streampipes.connect.iiot.protocol.set;


import org.apache.http.client.fluent.Request;
import org.apache.streampipes.connect.SendToPipeline;
import org.apache.streampipes.connect.adapter.guess.SchemaGuesser;
import org.apache.streampipes.connect.adapter.model.generic.Protocol;
import org.apache.streampipes.connect.api.IAdapterPipeline;
import org.apache.streampipes.connect.api.IFormat;
import org.apache.streampipes.connect.api.IParser;
import org.apache.streampipes.connect.api.exception.ParseException;
import org.apache.streampipes.model.AdapterType;
import org.apache.streampipes.model.connect.grounding.ProtocolDescription;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.sdk.builder.adapter.ProtocolDescriptionBuilder;
import org.apache.streampipes.sdk.extractor.StaticPropertyExtractor;
import org.apache.streampipes.sdk.helpers.AdapterSourceType;
import org.apache.streampipes.sdk.helpers.Filetypes;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.utils.Assets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FileProtocol extends Protocol {

    private static Logger logger = LoggerFactory.getLogger(FileProtocol.class);

    public static final String ID = "org.apache.streampipes.connect.iiot.protocol.set.file";

    private String fileFetchUrl;
    private String selectedFilename;

    public FileProtocol() {
    }

    public FileProtocol(IParser parser,
                        IFormat format,
                        String fileFetchUrl,
                        String selectedFilename) {
        super(parser, format);
        this.fileFetchUrl = fileFetchUrl;
        this.selectedFilename = selectedFilename;
    }

    @Override
    public ProtocolDescription declareModel() {
        return ProtocolDescriptionBuilder.create(ID)
                .withAssets(Assets.DOCUMENTATION, Assets.ICON)
                .withLocales(Locales.EN)
                .sourceType(AdapterSourceType.SET)
                .category(AdapterType.Generic)
                .requiredFile(Labels.withId("filePath"), Filetypes.XML, Filetypes.JSON, Filetypes.CSV)
                .build();
    }

    @Override
    public Protocol getInstance(ProtocolDescription protocolDescription, IParser parser, IFormat format) {
        StaticPropertyExtractor extractor = StaticPropertyExtractor.from(protocolDescription.getConfig());
        String fileFetchUrl = extractor.selectedFileFetchUrl("filePath");
        String selectedFilename = extractor.selectedFilename("filePath");
        return new FileProtocol(parser, format, fileFetchUrl, selectedFilename);
    }

    @Override
    public void run(IAdapterPipeline adapterPipeline) {
        FileReader fr = null;

        // TODO fix this. Currently needed because it must be wait till the whole pipeline is up and running
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        SendToPipeline stk = new SendToPipeline(format, adapterPipeline);
        try {
            InputStream dataInputStream = getFileInputStream();
            if(dataInputStream != null) {
                parser.parse(dataInputStream, stk);
            } else {
                logger.warn("Could not read data from file.");
            }
        } catch (ParseException e) {
            logger.error("Error while parsing: " + e.getMessage());
        } catch (FileNotFoundException e) {
            logger.error("Error reading file: " + e.getMessage());
        }
    }

    @Override
    public void stop() {

    }

    @Override
    public GuessSchema getGuessSchema() throws ParseException {

        try {
            InputStream targetStream = getFileInputStream();
            List<byte[]> dataByte = parser.parseNEvents(targetStream, 20);

            EventSchema eventSchema = parser.getEventSchema(dataByte);

            GuessSchema result = SchemaGuesser.guessSchma(eventSchema, getNElements(20));

            return result;
        } catch (FileNotFoundException e) {
            throw new ParseException("Could not read local file");
        }
    }

    private InputStream getFileInputStream() throws FileNotFoundException {
        if (!isFilePresent()) {
            try {
                storeFileLocally();
            } catch (IOException e) {
                throw new ParseException("Could not receive file");
            }
        }

        return new FileInputStream(makeFileLoc(this.selectedFilename));
    }

    private boolean isFilePresent() {
        File file = new File(makeFileLoc(selectedFilename));
        return file.exists();
    }

    private void storeFileLocally() throws IOException {
        File storageDir = new File(makeServiceStorageDir());
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        File file = new File(makeFileLoc(selectedFilename));

        Request.Get(fileFetchUrl).execute().saveContent(file);
    }

    private String makeServiceStorageDir() {
        return System.getProperty("user.home")
                + File.separator
                + ".streampipes"
                + File.separator
                + "service";
    }

    private String makeFileLoc(String filename) {
        return makeServiceStorageDir() + File.separator + filename;
    }

    @Override
    public List<Map<String, Object>> getNElements(int n) throws ParseException {
        List<Map<String, Object>> result = new ArrayList<>();

        List<byte[]> dataByteArray = new ArrayList<>();
        try {
            InputStream dataInputStream = getFileInputStream();
            dataByteArray = parser.parseNEvents(dataInputStream, n);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Check that result size is n. Currently just an error is logged. Maybe change to an exception
        if (dataByteArray.size() < n) {
            logger.error("Error in File Protocol! User required: " + n + " elements but the resource just had: " +
                    dataByteArray.size());
        }

        for (byte[] b : dataByteArray) {
            result.add(format.parse(b));
        }

        return result;
    }


    @Override
    public String getId() {
        return ID;
    }

}
