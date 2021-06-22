import {PipelineElementInput} from './PipelineElementInput';

export class PipelineInput {
    pipelineName: string;
    dataSource: string;

    processingElement: PipelineElementInput;

    dataSink: PipelineElementInput;
}