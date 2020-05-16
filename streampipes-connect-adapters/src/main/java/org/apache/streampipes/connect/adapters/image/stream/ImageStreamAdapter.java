/*
Copyright 2020 FZI Forschungszentrum Informatik

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

package org.apache.streampipes.connect.adapters.image.stream;

import org.apache.commons.io.IOUtils;
import org.apache.streampipes.connect.adapter.Adapter;
import org.apache.streampipes.connect.adapter.exception.AdapterException;
import org.apache.streampipes.connect.adapter.exception.ParseException;
import org.apache.streampipes.connect.adapter.model.specific.SpecificDataStreamAdapter;
import org.apache.streampipes.connect.adapters.iss.IssAdapter;
import org.apache.streampipes.model.AdapterType;
import org.apache.streampipes.model.connect.adapter.SpecificAdapterStreamDescription;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.model.staticproperty.FileStaticProperty;
import org.apache.streampipes.sdk.builder.adapter.GuessSchemaBuilder;
import org.apache.streampipes.sdk.builder.adapter.SpecificDataStreamAdapterBuilder;
import org.apache.streampipes.sdk.extractor.StaticPropertyExtractor;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.vocabulary.Geo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.apache.streampipes.sdk.helpers.EpProperties.*;

public class ImageStreamAdapter extends SpecificDataStreamAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(ImageStreamAdapter.class);

    public static final String ID = "org.apache.streampipes.connect.adapters.image.stream";

    private static final String INTERVAL_KEY = "interval-key";
    private static final String ZIP_FILE_KEY = "zip-file-key";

    private static final String Timestamp = "timestamp";
    private static final String Image = "image";

    public ImageStreamAdapter() {

    }

    public ImageStreamAdapter(SpecificAdapterStreamDescription adapterDescription) {
        super(adapterDescription);
    }

    @Override
    public SpecificAdapterStreamDescription declareModel() {
        SpecificAdapterStreamDescription description = SpecificDataStreamAdapterBuilder.create(ID)
                .withLocales(Locales.EN)
                .withAssets(Assets.DOCUMENTATION, Assets.ICON)
                .requiredIntegerParameter(Labels.withId(INTERVAL_KEY))
                .requiredFile(Labels.withId(ZIP_FILE_KEY))
                .build();
        description.setAppId(ID);

        return description;
    }

    @Override
    public void startAdapter() throws AdapterException {
        StaticPropertyExtractor extractor =
                StaticPropertyExtractor.from(adapterDescription.getConfig(), new ArrayList<>());
        FileStaticProperty fileStaticProperty = (FileStaticProperty) extractor.getStaticPropertyByName(ZIP_FILE_KEY);

        String fileUri = fileStaticProperty.getLocationPath();

    }



    @Override
    public void stopAdapter() throws AdapterException {

    }

    @Override
    public GuessSchema getSchema(SpecificAdapterStreamDescription adapterDescription) throws AdapterException, ParseException {
        return GuessSchemaBuilder.create()
                .property(timestampProperty(Timestamp))
                .property(imageProperty("image"))
                .build();
    }

    @Override
    public Adapter getInstance(SpecificAdapterStreamDescription adapterDescription) {
        return new ImageStreamAdapter(adapterDescription);
    }

    @Override
    public String getId() {
        return ID;
    }
}
