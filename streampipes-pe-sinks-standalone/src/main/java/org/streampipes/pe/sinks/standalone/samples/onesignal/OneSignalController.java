package org.streampipes.pe.sinks.standalone.samples.onesignal;

import org.streampipes.model.DataSinkType;
import org.streampipes.model.graph.DataSinkDescription;
import org.streampipes.model.graph.DataSinkInvocation;
import org.streampipes.pe.sinks.standalone.config.ActionConfig;
import org.streampipes.sdk.builder.DataSinkBuilder;
import org.streampipes.sdk.extractor.DataSinkParameterExtractor;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.sdk.helpers.SupportedFormats;
import org.streampipes.sdk.helpers.SupportedProtocols;
import org.streampipes.wrapper.standalone.ConfiguredEventSink;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventSinkDeclarer;

public class OneSignalController extends StandaloneEventSinkDeclarer<OneSignalParameters> {

    private static final String CONTENT_KEY = "content";
    private static final String APP_ID = "app_id";
    private static final String REST_API_KEY = "api_key";

    @Override
    public DataSinkDescription declareModel() {
        return DataSinkBuilder.create("onesignal", "OneSignal", "Send Push Message to OneSignal-Application")
                .category(DataSinkType.NOTIFICATION)
                .iconUrl(ActionConfig.getIconUrl("onesignal_icon.png"))
                .requiredPropertyStream1(EpRequirements.anyProperty())
                .supportedFormats(SupportedFormats.jsonFormat())
                .supportedProtocols(SupportedProtocols.kafka())
                .requiredHtmlInputParameter(Labels.from(CONTENT_KEY, "Content", "Push Message"))
                .requiredTextParameter(APP_ID, "App-ID", "OneSignal App ID")
                .requiredTextParameter(REST_API_KEY, "API-Key", "REST API Key")
                .build();
    }

    @Override
    public ConfiguredEventSink<OneSignalParameters> onInvocation(DataSinkInvocation graph, DataSinkParameterExtractor extractor) {

        String content = extractor.singleValueParameter(CONTENT_KEY, String.class);
        String appId = extractor.singleValueParameter(APP_ID, String.class);
        String apiKey = extractor.singleValueParameter(REST_API_KEY, String.class);

        OneSignalParameters params = new OneSignalParameters(graph, content, appId, apiKey);

        return new ConfiguredEventSink<>(params, OneSignalProducer::new);
    }

}
