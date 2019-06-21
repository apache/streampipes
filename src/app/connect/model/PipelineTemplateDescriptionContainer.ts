import { RdfId } from '../../platform-services/tsonld/RdfId';
import { RdfProperty } from '../../platform-services/tsonld/RdfsProperty';
import { RdfsClass } from '../../platform-services/tsonld/RdfsClass';
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