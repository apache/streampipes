import { RdfId } from '../tsonld/RdfId';
import { RdfsClass } from '../tsonld/RdfsClass';
import { RdfProperty } from '../tsonld/RdfsProperty';
import { StaticProperty } from './StaticProperty';

@RdfsClass('sp:PipelineTemplateInvocation')
export class PipelineTemplateInvocation {

    @RdfId
    public id: string;

    @RdfProperty('sp:name')
    public name: string;

    @RdfProperty('sp:hasStaticProperty')
    public list: StaticProperty[] = [];

    constructor(id: string) {
        this.id = id;
    }

}