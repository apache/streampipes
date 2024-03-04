package org.apache.streampipes.connect.iiot.adapters.iolink.sensor;

import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.PrimitivePropertyBuilder;
import org.apache.streampipes.sdk.builder.adapter.GuessSchemaBuilder;
import org.apache.streampipes.sdk.utils.Datatypes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

import static org.apache.streampipes.sdk.helpers.EpProperties.timestampProperty;

public class SensorVVB001Hilscher {

  public static final String V_RMS_NAME = "v-Rms";
  public static final String A_PEAK_NAME = "a-Peak";
  public static final String A_RMS_NAME = "a-Rms";
  public static final String TEMPERATURE_NAME = "Temperature";
  public static final String CREST_NAME = "Crest";
  public static final String STATUS_NAME = "Device status";
  public static final String OUT_1_NAME = "OUT1";
  public static final String OUT_2_NAME = "OUT2";
  public static final String SENSOR_ID_NAME = "SensorID";
  public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
  public static final String TIMESTAMP_NAME_SOURCE = "Timestamp";
  public static final String TIMESTAMP_NAME_SP = TIMESTAMP_NAME_SOURCE.toLowerCase();

  public static final Double NUMERICAL_SCALE_FACTOR = 0.1;

  public GuessSchema getEventSchema() {

    return GuessSchemaBuilder
        .create()
        .property(timestampProperty(TIMESTAMP_NAME_SP))
        .sample(TIMESTAMP_NAME_SP, 1685525380729L)
        .property(
            PrimitivePropertyBuilder
                .create(Datatypes.Float, V_RMS_NAME)
                .scope(PropertyScope.MEASUREMENT_PROPERTY)
                .description("Speed RMS value")
                .build()
        )
        .sample(V_RMS_NAME, 0.0023)
        .property(
            PrimitivePropertyBuilder
                .create(Datatypes.Float, A_PEAK_NAME)
                .scope(PropertyScope.MEASUREMENT_PROPERTY)
                .description("Acceleration peak value")
                .build()
        )
        .sample(A_PEAK_NAME, 6.6)
        .property(
            PrimitivePropertyBuilder
                .create(Datatypes.Float, A_RMS_NAME)
                .scope(PropertyScope.MEASUREMENT_PROPERTY)
                .description("Acceleration RMS value")
                .build()
        )
        .sample(A_RMS_NAME, 1.8)
        .property(
            PrimitivePropertyBuilder
                .create(Datatypes.Float, TEMPERATURE_NAME)
                .scope(PropertyScope.MEASUREMENT_PROPERTY)
                .description("Current temperature")
                .build()
        )
        .sample(TEMPERATURE_NAME, 22.0)
        .property(
            PrimitivePropertyBuilder
                .create(Datatypes.Float, CREST_NAME)
                .scope(PropertyScope.MEASUREMENT_PROPERTY)
                .description("Acceleration crest factor")
                .build()
        )
        .sample(CREST_NAME, 3.7)
        .property(
            PrimitivePropertyBuilder
                .create(Datatypes.Boolean, OUT_1_NAME)
                .scope(PropertyScope.MEASUREMENT_PROPERTY)
                .description("Current state of the digital signal")
                .build()
        )
        .sample(OUT_1_NAME, true)
        .property(
            PrimitivePropertyBuilder
                .create(Datatypes.Boolean, OUT_2_NAME)
                .scope(PropertyScope.MEASUREMENT_PROPERTY)
                .description("Current state of the digital signal")
                .build()
        )
        .sample(OUT_2_NAME, true)
        .property(
            PrimitivePropertyBuilder
                .create(Datatypes.Integer, STATUS_NAME)
                .scope(PropertyScope.MEASUREMENT_PROPERTY)
                .description("Device status (0: OK, 1: Maintenance required, "
                                 + "2: Out of specification, 3: Function Test, 4: Error)")
                .build()
        )
        .sample(STATUS_NAME, 0)
        .property(
            PrimitivePropertyBuilder
                .create(Datatypes.String, SENSOR_ID_NAME)
                .scope(PropertyScope.DIMENSION_PROPERTY)
                .description("Identifier of the sensor")
                .build()
        )
        .sample(SENSOR_ID_NAME, "000008740649")
        .build();
  }

  public Map<String, Object> parseEvent(Map<String, Object> event){
    try {
      // convert timestamp from string representation to UNIX timestamp
      event.put(TIMESTAMP_NAME_SP, new SimpleDateFormat(DATE_FORMAT).parse((String) event.get(TIMESTAMP_NAME_SP)).getTime());

      // correct all numerical values by scale factor
      event.put(CREST_NAME,  Float.parseFloat(event.get(CREST_NAME).toString()) * NUMERICAL_SCALE_FACTOR);
      event.put(TEMPERATURE_NAME,  Float.parseFloat(event.get(TEMPERATURE_NAME).toString()) * NUMERICAL_SCALE_FACTOR);
      event.put(A_PEAK_NAME,  Float.parseFloat(event.get(A_PEAK_NAME).toString()) * NUMERICAL_SCALE_FACTOR);
      event.put(A_RMS_NAME,  Float.parseFloat(event.get(A_RMS_NAME).toString()) * NUMERICAL_SCALE_FACTOR);
      event.put(V_RMS_NAME,  Float.parseFloat(event.get(V_RMS_NAME).toString()) * NUMERICAL_SCALE_FACTOR);

    } catch (ParseException e) {
      throw new RuntimeException("Error during parsing the raw event: %s".formatted(event));
    }
    return event;
  }
}
