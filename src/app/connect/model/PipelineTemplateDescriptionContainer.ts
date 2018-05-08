import { RdfId } from '../tsonld/RdfId';
import { RdfProperty } from '../tsonld/RdfsProperty';
import { RdfsClass } from '../tsonld/RdfsClass';
import { PipelineTemplateDescription } from './PipelineTemplateDescription';

@RdfsClass('sp:PipelineTemplateDescriptionContainer')
export class PipelineTemplateDescriptionContainer {

    @RdfId
    public id: string;

    @RdfProperty('sp:list')
    public list: PipelineTemplateDescription[] = [];

    constructor(id: string) {
        this.id = id;
    }

}