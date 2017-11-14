package org.streampipes.pe.sinks.standalone.samples.notification;

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
import org.streampipes.wrapper.ConfiguredEventSink;
import org.streampipes.wrapper.runtime.EventSink;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventSinkDeclarer;

public class NotificationController extends StandaloneEventSinkDeclarer<NotificationParameters> {

	private static final String TITLE_KEY = "title";
	private static final String CONTENT_KEY = "content";

	@Override
	public DataSinkDescription declareModel() {
		return DataSinkBuilder.create("notification", "Notification", "Displays a notification in the UI panel")
						.category(DataSinkType.NOTIFICATION)
						.iconUrl(ActionConfig.getIconUrl("notification_icon.png"))
						.requiredPropertyStream1(EpRequirements.anyProperty())
						.supportedFormats(SupportedFormats.jsonFormat())
						.supportedProtocols(SupportedProtocols.kafka())
						.requiredTextParameter(TITLE_KEY, "Notification title", "Notification title")
						.requiredHtmlInputParameter(Labels.from(CONTENT_KEY, "Content", "Enter the notification text. You can " +
										"use place holders like #fieldName# to add the value of a stream variable."))
						.build();
	}

	@Override
	public ConfiguredEventSink<NotificationParameters, EventSink<NotificationParameters>> onInvocation(DataSinkInvocation graph) {
		DataSinkParameterExtractor extractor = getExtractor(graph);

		String title = extractor.singleValueParameter(TITLE_KEY, String.class);
		String content = extractor.singleValueParameter(CONTENT_KEY, String.class);

		NotificationParameters params = new NotificationParameters(graph, title, content);

		return new ConfiguredEventSink<>(params, NotificationProducer::new);
	}

}
