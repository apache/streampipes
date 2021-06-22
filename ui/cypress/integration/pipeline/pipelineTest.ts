import {UserInput} from '../../support/model/UserInput';
import {AdapterUtils} from '../../support/utils/AdapterUtils';
import {PipelineUtils} from '../../support/utils/PipelineUtils';
import {PipelineElementBuilder} from '../../support/builder/PipelineElementBuilder';
import {PipelineBuilder} from '../../support/builder/PipelineBuilder';

describe('Test Random Data Simulator Stream Adapter', function () {

    it('Login', function () {
        cy.login();
    });

    const adapterName = 'simulator';
    AdapterUtils.addMachineDataSimulator(adapterName);

    const pipelineInput = PipelineBuilder.create('Pipeline Test')
        .addSource(adapterName)
        .addProcessingElement(
            PipelineElementBuilder.create('field_renamer')
                .addInput('drop-down', 'convert-property','timestamp')
                .addInput('input', 'field-name','t')
                .build())
        .addSink(
            PipelineElementBuilder.create('dashboard_sink')
                .addInput('input', 'visualization-name','Demo')
                .build())
        .build();

    PipelineUtils.testPipeline(pipelineInput);

    AdapterUtils.deleteAdapter();
});