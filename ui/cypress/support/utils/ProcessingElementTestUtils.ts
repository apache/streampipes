import {FileManagementUtils} from './FileManagementUtils';
import {AdapterUtils} from './AdapterUtils';
import {PipelineElementInput} from '../model/PipelineElementInput';
import {PipelineUtils} from './PipelineUtils';
import {DataLakeUtils} from './DataLakeUtils';
import {GenericAdapterBuilder} from '../builder/GenericAdapterBuilder';
import {PipelineBuilder} from '../builder/PipelineBuilder';
import {PipelineElementBuilder} from '../builder/PipelineElementBuilder';

export class ProcessingElementTestUtils {

    public static testElement(testName: string, inputFile: string, expectedResultFile: string, processor: PipelineElementInput) {
        // Test
        FileManagementUtils.addFile(inputFile);

        const dataLakeIndex = testName.toLowerCase();

        const adapterName = testName.toLowerCase();

        // Build adapter
        const adapterInput = GenericAdapterBuilder
            .create('File_Set')
            .setName(adapterName)
            .setTimestampProperty('timestamp')
            .addProtocolInput('input', 'interval-key','0')
            .setFormat('csv')
            .addFormatInput('input', 'delimiter',';')
            .addFormatInput('checkbox', 'header','check')
            .build();

        AdapterUtils.addGenericSetAdapter(adapterInput);

        // Build Pipeline
        const pipelineInput = PipelineBuilder.create(testName)
            .addSource(adapterName)
            .addProcessingElement(processor)
            .addSink(
                PipelineElementBuilder.create('data_lake')
                    .addInput('input', 'db_measurement',dataLakeIndex)
                    .build())
            .build();

        PipelineUtils.addPipeline(pipelineInput);

        // // Wait
        it('Wait till data is stored', function () {
            cy.wait(10000)
        });

        DataLakeUtils.checkResults(dataLakeIndex, 'cypress/fixtures/' + expectedResultFile);

        PipelineUtils.deletePipeline();

        AdapterUtils.deleteAdapter();

        FileManagementUtils.deleteFile();
    }
}