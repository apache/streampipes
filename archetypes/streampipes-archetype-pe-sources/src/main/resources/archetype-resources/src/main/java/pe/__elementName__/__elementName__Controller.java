#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.pe.notification;

import ${groupId}.model.DataSinkType;
import ${groupId}.model.graph.DataSinkDescription;
import ${groupId}.model.graph.DataSinkInvocation;
import ${package}.config.ActionConfig;
import ${groupId}.sdk.builder.DataSinkBuilder;
import ${groupId}.sdk.extractor.DataSinkParameterExtractor;
import ${groupId}.sdk.helpers.EpRequirements;
import ${groupId}.sdk.helpers.Labels;
import ${groupId}.sdk.helpers.SupportedFormats;
import ${groupId}.sdk.helpers.SupportedProtocols;
import ${groupId}.wrapper.ConfiguredEventSink;
import ${groupId}.wrapper.runtime.EventSink;
import ${groupId}.wrapper.standalone.declarer.StandaloneEventSinkDeclarer;

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
										"use place holders like ${symbol_pound}fieldName${symbol_pound} to add the value of a stream variable."))
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
