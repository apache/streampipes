package de.fzi.cep.sepa.manager.kpi;

import de.fzi.cep.sepa.kpi.*;
import de.fzi.cep.sepa.manager.kpi.context.ContextModel;
import de.fzi.cep.sepa.manager.kpi.context.ContextModelEndpointBuilder;
import de.fzi.cep.sepa.manager.kpi.context.ContextModelFetcher;
import de.fzi.cep.sepa.manager.kpi.mapping.IdMapper;
import de.fzi.cep.sepa.manager.kpi.pipelineelements.*;
import de.fzi.cep.sepa.manager.operations.Operations;
import de.fzi.cep.sepa.model.NamedSEPAElement;
import de.fzi.cep.sepa.model.client.pipeline.Pipeline;
import de.fzi.cep.sepa.model.client.pipeline.PipelineModificationMessage;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.graph.SecInvocation;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by riemer on 03.10.2016.
 */
public class KpiPipelineBuilder {

    private KpiRequest kpiRequest;

    private static final String AGGREGATE_EPA_SUFFIX = "/sepa/aggregation";
    private static final String MATH_EPA_SUFFIX = "/sepa/math-binary";
    private static final String COUNT_EPA_SUFFIX = "/sepa/count";
    private static final String KAFKA_PUBLISHER_SUFFIX = "/sec/kafka";

    private ContextModel contextModel;
    private IdMapper idMapper;
    private String useCase;

    public KpiPipelineBuilder(KpiRequest kpiRequest) throws IOException {
        this.kpiRequest = kpiRequest;
        this.idMapper = new IdMapper(this.contextModel);
        this.useCase = ContextModelEndpointBuilder.CONTEXT_MODEL_HELLA;
        this.contextModel = makeContextModel();
    }

    public KpiPipelineBuilder(KpiRequest kpiRequest, String useCase) throws IOException {
        this(kpiRequest);
        this.useCase = useCase;
    }

    private ContextModel makeContextModel() throws IOException {
        List<String> requiredSensorIds = collectSensorIds(kpiRequest.getOperation(), new ArrayList<>());

        return new ContextModelFetcher(useCase, requiredSensorIds).fetchContextModel();
    }

    private List<String> collectSensorIds(Operation operation, List<String> sensorIds) {
        if (operation instanceof UnaryOperation) {
            sensorIds.add(((UnaryOperation) operation).getSensorId());
        } else {
            collectSensorIds(((BinaryOperation) operation).getLeft(), sensorIds);
            collectSensorIds(((BinaryOperation) operation).getRight(), sensorIds);
        }
        return sensorIds;
    }

    public Pipeline makePipeline() {

        Pipeline pipeline = preparePipeline();

        Operation operation = kpiRequest.getOperation();
        try {
            if (operation instanceof UnaryOperation) {
                UnaryOperation unaryPipelineOperation = (UnaryOperation) operation;

                configureStream(pipeline, unaryPipelineOperation);
                if (unaryPipelineOperation.getUnaryOperationType() == UnaryOperationType.NONE) {
                    configureAction(pipeline, pipeline.getStreams().get(0));
                } else {
                    configureAggregationSepa(pipeline, pipeline.getStreams().get(0), unaryPipelineOperation, 0);
                    configureAction(pipeline, pipeline.getSepas().get(0));
                }
            } else if (operation instanceof BinaryOperation) {
                BinaryOperation binaryPipelineOperation = (BinaryOperation) operation;
                UnaryOperation leftOperation = (UnaryOperation) binaryPipelineOperation.getLeft();
                UnaryOperation rightOperation = (UnaryOperation) binaryPipelineOperation.getRight();

                configureStream(pipeline, leftOperation);
                configureStream(pipeline, rightOperation);

                NamedSEPAElement leftElement = pipeline.getStreams().get(0);
                NamedSEPAElement rightElement = pipeline.getStreams().get(1);

                int index = 0;
                if (leftOperation.getUnaryOperationType() != UnaryOperationType.NONE) {
                    configureAggregationSepa(pipeline, pipeline.getStreams().get(0), leftOperation, index);
                    leftElement = pipeline.getSepas().get(0);
                    index++;
                }
                if (rightOperation.getUnaryOperationType() != UnaryOperationType.NONE) {
                    configureAggregationSepa(pipeline, pipeline.getStreams().get(1), rightOperation, index);
                    rightElement = pipeline.getSepas().get(index);
                    index++;
                }

                configureMathEpa(pipeline, leftElement, rightElement, binaryPipelineOperation, index);
                configureAction(pipeline, pipeline.getSepas().get(pipeline.getSepas().size()-1));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pipeline;
    }

    private Pipeline preparePipeline() {
        Pipeline pipeline = new Pipeline();
        pipeline.setName("KPI - " + kpiRequest.getKpiName());
        pipeline.setPipelineId(kpiRequest.getKpiId());
        pipeline.setCreatedByUser("system");
        pipeline.setCreatedAt(System.currentTimeMillis());
        pipeline.setStreams(new ArrayList<>());
        pipeline.setSepas(new ArrayList<>());
        pipeline.setActions(new ArrayList<>());
        return pipeline;
    }

    private void configureStream(Pipeline pipeline, UnaryOperation unaryPipelineOperation) {
        EventStream stream = idMapper.getEventStream(unaryPipelineOperation.getSensorId());
        stream.setDOM(getUUID());
        List<EventStream> streams = pipeline.getStreams();
        streams.add(stream);
        pipeline.setStreams(streams);
    }

    private void configureMathEpa(Pipeline pipeline, NamedSEPAElement leftElement, NamedSEPAElement rightElement, BinaryOperation binaryPipelineOperation, int index) throws Exception {
        SepaInvocation math = new SepaInvocation(KpiPipelineBuilderUtils.getSepa(MATH_EPA_SUFFIX).get());
        math.setConnectedTo(Arrays.asList(leftElement.getDOM(), rightElement.getDOM()));
        math.setDOM(getUUID());

        List<SepaInvocation> sepas = pipeline.getSepas();
        sepas.add(math);
        pipeline.setSepas(sepas);

        PipelineModificationMessage message = Operations.validatePipeline(pipeline, true);
        math = new BinaryMathGenerator(modifyPipeline(pipeline
                .getSepas()
                .get(sepas.size()-1), message),
                getBinaryMathSettings(leftElement, rightElement, binaryPipelineOperation)).makeInvocationGraph();
        sepas.remove(sepas.size()-1);
        sepas.add(math);
        pipeline.setSepas(sepas);
    }

    private void configureAggregationSepa(Pipeline pipeline, EventStream connectedTo, UnaryOperation unaryPipelineOperation, int index) throws Exception {
        SepaInvocation aggregation = new SepaInvocation(KpiPipelineBuilderUtils.getSepa(AGGREGATE_EPA_SUFFIX).get());
        aggregation.setConnectedTo(Arrays.asList(connectedTo.getDOM()));
        aggregation.setDOM(getUUID());

        List<SepaInvocation> sepas = pipeline.getSepas();
        sepas.add(aggregation);
        pipeline.setSepas(sepas);
        PipelineModificationMessage message = Operations.validatePipeline(pipeline, true);
        aggregation = new AggregationGenerator(modifyPipeline(pipeline
                .getSepas()
                .get(sepas.size()-1), message),
                getAggregationSettings(connectedTo, unaryPipelineOperation)).makeInvocationGraph();
        sepas.remove(sepas.size()-1);
        sepas.add(aggregation);
        pipeline.setSepas(sepas);
    }

    private void configureAction(Pipeline pipeline, NamedSEPAElement connectedTo) throws Exception {
        SecInvocation action = new SecInvocation(KpiPipelineBuilderUtils.getSec(KAFKA_PUBLISHER_SUFFIX).get());
        action.setConnectedTo(Arrays.asList(connectedTo.getDOM()));
        action.setDOM(getUUID());
        pipeline.setActions(Arrays.asList(action));

        PipelineModificationMessage message = Operations.validatePipeline(pipeline, true);

        action = new KafkaPublisherGenerator(modifyPipeline(pipeline.getActions().get(0), message), getKafkaSettings()).makeInvocationGraph();
        pipeline.setActions(Arrays.asList(action));
    }

    private SepaInvocation modifyPipeline(SepaInvocation sepaInvocation, PipelineModificationMessage message) {
        sepaInvocation.setConfigured(true);
        sepaInvocation.setStaticProperties(message.getPipelineModifications().get(0).getStaticProperties());
        sepaInvocation.setOutputStrategies(message.getPipelineModifications().get(0).getOutputStrategies());
        return sepaInvocation;
    }

    private SecInvocation modifyPipeline(SecInvocation secInvocation, PipelineModificationMessage message) {
        secInvocation.setConfigured(true);
        secInvocation.setStaticProperties(message.getPipelineModifications().get(0).getStaticProperties());
        return secInvocation;
    }

    private AggregationSettings getAggregationSettings(EventStream stream, UnaryOperation unaryPipelineOperation) {
        return AggregationSettings.makeSettings(stream, unaryPipelineOperation, idMapper);
    }

    private BinaryMathSettings getBinaryMathSettings(NamedSEPAElement leftElement, NamedSEPAElement rightElement, BinaryOperation binaryPipelineOperation) {
        return BinaryMathSettings.makeSettings(leftElement, rightElement, binaryPipelineOperation, idMapper);
    }


    private KafkaSettings getKafkaSettings() {
        return KafkaSettings.makeSettings(kpiRequest.getKpiId());
    }

    private static final String getUUID() {
        return UUID.randomUUID().toString();
    }


}
