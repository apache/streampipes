package org.apache.streampipes.processors.changedetection.jvm.welford;

import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.runtime.EventFactory;
import org.apache.streampipes.model.runtime.SchemaInfo;
import org.apache.streampipes.model.runtime.SourceInfo;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.PrimitivePropertyBuilder;
import org.apache.streampipes.sdk.builder.adapter.GuessSchemaBuilder;
import org.apache.streampipes.sdk.utils.Datatypes;
import org.apache.streampipes.test.executors.ProcessingElementTestExecutor;
import org.apache.streampipes.test.executors.TestConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestWelfordChangeDetectionProcessor {
    private static final String PROPERTY_NAME = "propertyName";
    private WelfordChangeDetection processor;


    @BeforeEach
    public void setup() {
        processor = new WelfordChangeDetection();
    }


    @Test
    public void testWelfordNoChangeDetected() {
        TestConfiguration configuration = TestConfiguration.builder()
                .configWithDefaultPrefix(WelfordChangeDetection.NUMBER_MAPPING, PROPERTY_NAME)
                .config(WelfordChangeDetection.PARAM_K,1.5)
                .config(WelfordChangeDetection.PARAM_H,5.0)
                .build();

        List<Map<String, Object>> inputEvents = List.of(
                Map.of(PROPERTY_NAME,10.0f),
                Map.of(PROPERTY_NAME,11.0f),
                Map.of(PROPERTY_NAME,10.5f)
        );

        List<Map<String, Object>> outputEvents = List.of(
                Map.of(PROPERTY_NAME,10.0f,WelfordEventFields.VAL_LOW.label,0.0, WelfordEventFields.VAL_HIGH.label,0.0,
                        WelfordEventFields.DECISION_LOW.label,false, WelfordEventFields.DECISION_HIGH.label,false),
                Map.of(PROPERTY_NAME,11.0f,WelfordEventFields.VAL_LOW.label,0.0, WelfordEventFields.VAL_HIGH.label,0.0,
                        WelfordEventFields.DECISION_LOW.label,false, WelfordEventFields.DECISION_HIGH.label,false),
                Map.of(PROPERTY_NAME,10.5f,WelfordEventFields.VAL_LOW.label,0.0, WelfordEventFields.VAL_HIGH.label,0.0,
                        WelfordEventFields.DECISION_LOW.label,false, WelfordEventFields.DECISION_HIGH.label,false)
        );

        ProcessingElementTestExecutor testExecutor = new ProcessingElementTestExecutor(processor, configuration);

        testExecutor.run(inputEvents,outputEvents);
    }

    @Test
    public void testWelfordChangeDetectedHigh(){
        TestConfiguration configuration = TestConfiguration.builder()
                .configWithDefaultPrefix(WelfordChangeDetection.NUMBER_MAPPING, PROPERTY_NAME)
                .config(WelfordChangeDetection.PARAM_K,0.5)
                .config(WelfordChangeDetection.PARAM_H,1.0)
                .build();

        List<Map<String, Object>> inputEvents = List.of(
                Map.of(PROPERTY_NAME,10.0f),
                Map.of(PROPERTY_NAME, 20.0f),
                Map.of(PROPERTY_NAME,5.0f)
        );

        List<Map<String, Object>> outputEvents = List.of(
                Map.of(PROPERTY_NAME,10.0f,WelfordEventFields.VAL_LOW.label,0.0, WelfordEventFields.VAL_HIGH.label,0.0,
                        WelfordEventFields.DECISION_LOW.label,false, WelfordEventFields.DECISION_HIGH.label,false),
                Map.of(PROPERTY_NAME,20.0f,WelfordEventFields.VAL_LOW.label,0.0, WelfordEventFields.VAL_HIGH.label,0.20710678118654746,
                        WelfordEventFields.DECISION_LOW.label,false,WelfordEventFields.DECISION_HIGH.label,false),
                Map.of(PROPERTY_NAME,5.0f,WelfordEventFields.VAL_LOW.label,-0.37287156094396945, WelfordEventFields.VAL_HIGH.label,0.0,
                        WelfordEventFields.DECISION_LOW.label,false, WelfordEventFields.DECISION_HIGH.label,false)
        );

        ProcessingElementTestExecutor testExecutor = new ProcessingElementTestExecutor(processor, configuration);

        testExecutor.run(inputEvents,outputEvents);
    }

    @Test
    public void testWelfordChangeDetectedLow() {

        TestConfiguration configuration = TestConfiguration.builder()
                .configWithDefaultPrefix(WelfordChangeDetection.NUMBER_MAPPING, PROPERTY_NAME)
                .config(WelfordChangeDetection.PARAM_K,0.5)
                .config(WelfordChangeDetection.PARAM_H,1.0)
                .build();

        List<Map<String, Object>> inputEvents = List.of(
                Map.of(PROPERTY_NAME,10.0f),
                Map.of(PROPERTY_NAME,5.0f),
                Map.of(PROPERTY_NAME, 20.0f)
        );

        List<Map<String, Object>> outputEvents = List.of(
                Map.of(PROPERTY_NAME,10.0f,WelfordEventFields.VAL_LOW.label,0.0, WelfordEventFields.VAL_HIGH.label,0.0,
                        WelfordEventFields.DECISION_LOW.label,false, WelfordEventFields.DECISION_HIGH.label,false),
                Map.of(PROPERTY_NAME,5.0f,WelfordEventFields.VAL_LOW.label,-0.20710678118654746, WelfordEventFields.VAL_HIGH.label,0.0,
                        WelfordEventFields.DECISION_LOW.label,false, WelfordEventFields.DECISION_HIGH.label,false),
                Map.of(PROPERTY_NAME,20.0f,WelfordEventFields.VAL_LOW.label,0.0, WelfordEventFields.VAL_HIGH.label,0.5910894511799618,
                        WelfordEventFields.DECISION_LOW.label,false, WelfordEventFields.DECISION_HIGH.label,false)
        );

        ProcessingElementTestExecutor testExecutor = new ProcessingElementTestExecutor(processor, configuration);

        testExecutor.run(inputEvents,outputEvents);
    }



}
