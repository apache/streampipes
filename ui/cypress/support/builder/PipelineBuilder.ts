import {UserInput} from '../model/UserInput';
import {PipelineInput} from '../model/PipelineInput';
import {PipelineElementInput} from '../model/PipelineElementInput';

export class PipelineBuilder {
    pipeline: PipelineInput;

    constructor(name: string) {
        this.pipeline = new PipelineInput();
        this.pipeline.pipelineName = name;
    }

    public static create(name: string) {
        return new PipelineBuilder(name);
    }

    public addSource(source: string) {
        this.pipeline.dataSource = source;

        return this;
    }

    public addProcessingElement(processingElement: PipelineElementInput) {
        this.pipeline.processingElement = processingElement;

        return this;
    }

    public addSink(sink: PipelineElementInput) {
        this.pipeline.dataSink = sink;

        return this;
    }

    build() {
        return this.pipeline;
    }

}
