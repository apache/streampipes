import { RdfId } from '../../platform-services/tsonld/RdfId';
import { RdfProperty } from '../../platform-services/tsonld/RdfsProperty';
import { RdfsClass } from '../../platform-services/tsonld/RdfsClass';
import { InvocableStreamPipesEntity } from "./InvocableStreamPipesEntity";

@RdfsClass('sp:BoundPipelineElement')
export class BoundPipelineElement {

    @RdfId
    public id: string;

    @RdfProperty('sp:hasPipelineElementTemplate')
    private template: InvocableStreamPipesEntity;

    @RdfProperty('sp:isConnectedTo')
    public connectedTo: BoundPipelineElement[] = [];

    constructor(id: string) {
        this.id = id;
    }

}