package org.apache.streampipes.sinks.internal.jvm.datalake;

import org.apache.commons.codec.binary.Base64;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.dataexplorer.commons.DataExplorerConnectionSettings;
import org.apache.streampipes.dataexplorer.commons.DataExplorerUtils;
import org.apache.streampipes.logging.api.Logger;
import org.apache.streampipes.model.DataSinkType;
import org.apache.streampipes.model.graph.DataSinkDescription;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.DataSinkBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.sinks.internal.jvm.config.ConfigKeys;
import org.apache.streampipes.sinks.internal.jvm.datalake.image.ImageStore;
import org.apache.streampipes.svcdiscovery.api.SpConfig;
import org.apache.streampipes.vocabulary.SPSensor;
import org.apache.streampipes.wrapper.context.EventSinkRuntimeContext;
import org.apache.streampipes.wrapper.standalone.SinkParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataSink;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class DataLakeSink extends StreamPipesDataSink {

    private DataLakeInfluxDbClient influxDbClient;

    private static final String DATABASE_MEASUREMENT_KEY = "db_measurement";
    private static final String TIMESTAMP_MAPPING_KEY = "timestamp_mapping";

    private static Logger LOG;

    private String timestampField;
    private List<EventProperty> imageProperties;

    private EventSchema eventSchema;
    private ImageStore imageStore;

    @Override
    public DataSinkDescription declareModel() {
        return DataSinkBuilder.create("org.apache.streampipes.sinks.internal.jvm.datalake")
                .withLocales(Locales.EN)
                .withAssets(Assets.DOCUMENTATION, Assets.ICON)
                .category(DataSinkType.INTERNAL)
                .requiredStream(StreamRequirementsBuilder.create().requiredPropertyWithUnaryMapping(
                        EpRequirements.timestampReq(),
                        Labels.withId(TIMESTAMP_MAPPING_KEY),
                        PropertyScope.NONE).build())
                .requiredTextParameter(Labels.withId(DATABASE_MEASUREMENT_KEY))
                .build();
    }

    @Override
    public void onInvocation(SinkParams parameters, EventSinkRuntimeContext runtimeContext) throws SpRuntimeException {

        LOG = parameters.getGraph().getLogger(DataLakeSink.class);

        this.timestampField = parameters.extractor().mappingPropertyValue(TIMESTAMP_MAPPING_KEY);
        String measureName = parameters.extractor().singleValueParameter(DATABASE_MEASUREMENT_KEY, String.class);
        measureName = DataLakeUtils.prepareString(measureName);

        SpConfig configStore = runtimeContext.getConfigStore().getConfig();

        String couchDbProtocol = configStore.getString(ConfigKeys.COUCHDB_PROTOCOL);
        String couchDbHost = configStore.getString(ConfigKeys.COUCHDB_HOST);
        int couchDbPort = configStore.getInteger(ConfigKeys.COUCHDB_PORT);

        this.imageStore = new ImageStore(couchDbProtocol, couchDbHost, couchDbPort);

        EventSchema schema = runtimeContext.getInputSchemaInfo().get(0).getEventSchema();
        // Remove the timestamp field from the event schema
        List<EventProperty> eventPropertiesWithoutTimestamp = schema.getEventProperties()
                .stream()
                .filter(eventProperty -> !this.timestampField.endsWith(eventProperty.getRuntimeName()))
                .collect(Collectors.toList());
        schema.setEventProperties(eventPropertiesWithoutTimestamp);

        // deep copy of event schema. Event property runtime name is changed to lower case for the schema registration
        this.eventSchema = new EventSchema(schema);

        schema.getEventProperties().forEach(eventProperty ->
                eventProperty.setRuntimeName(DataLakeUtils.sanitizePropertyRuntimeName(eventProperty.getRuntimeName())));
        DataExplorerUtils.registerAtDataLake(measureName, schema, runtimeContext.getStreamPipesClient());


        // Get schema from couchdb

        // if schema version null -> rename event schema

        //

        imageProperties = schema.getEventProperties().stream()
                .filter(eventProperty -> eventProperty.getDomainProperties() != null &&
                        eventProperty.getDomainProperties().size() > 0 &&
                        eventProperty.getDomainProperties().get(0).toString().equals(SPSensor.IMAGE))
                .collect(Collectors.toList());

        DataExplorerConnectionSettings settings = DataExplorerConnectionSettings.from(
                configStore,
                measureName);

        Integer batchSize = 2000;
        Integer flushDuration = 500;

        this.influxDbClient = new DataLakeInfluxDbClient(
                settings,
                this.timestampField,
                batchSize,
                flushDuration,
                this.eventSchema
        );
    }

    @Override
    public void onEvent(Event event) throws SpRuntimeException {
        try {
            this.imageProperties.forEach(eventProperty -> {
                String imageDocId = UUID.randomUUID().toString();
                String image = event.getFieldByRuntimeName(eventProperty.getRuntimeName()).getAsPrimitive().getAsString();

                this.writeToImageFile(image, imageDocId);
                event.updateFieldBySelector("s0::" + eventProperty.getRuntimeName(), imageDocId);
            });

            influxDbClient.save(event, this.eventSchema);
        } catch (SpRuntimeException e) {
            LOG.error(e.getMessage());
        }
    }

    @Override
    public void onDetach() throws SpRuntimeException {
        influxDbClient.stop();
    }

    private void writeToImageFile(String image, String imageDocId) {
        byte[] data = Base64.decodeBase64(image);
        this.imageStore.storeImage(data, imageDocId);
    }
}
