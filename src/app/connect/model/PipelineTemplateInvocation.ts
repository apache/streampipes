import { RdfId } from '../../platform-services/tsonld/RdfId';
import { RdfsClass } from '../../platform-services/tsonld/RdfsClass';
import { RdfProperty } from '../../platform-services/tsonld/RdfsProperty';
import { StaticProperty } from './StaticProperty';

@RdfsClass('sp:PipelineTemplateInvocation')
export class PipelineTemplateInvocation {

    @RdfId
    public id: string;

    @RdfProperty('sp:hasName')
    public name: string;

    @RdfProperty('sp:hasDataSetId')
    public dataSetId: string;

    @RdfProperty('sp:internalName')
    public pipelineTemplateId: string;

    @RdfProperty('sp:hasStaticProperty')
    public list: StaticProperty[] = [];

    constructor(id: string) {
        this.id = id;
    }

}