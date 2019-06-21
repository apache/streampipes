import { RdfId } from '../../platform-services/tsonld/RdfId';
import { RdfProperty } from '../../platform-services/tsonld/RdfsProperty';
import { RdfsClass } from '../../platform-services/tsonld/RdfsClass';
import { BoundPipelineElement } from "./BoundPipelineElement";


@RdfsClass('sp:PipelineTemplateDescription')
export class PipelineTemplateDescription {

    @RdfId
    public id: string;

    @RdfProperty('http://www.w3.org/2000/01/rdf-schema#description')
    public description: string;

    @RdfProperty('http://www.w3.org/2000/01/rdf-schema#label')
    public label: string;

    @RdfProperty('sp:internalName')
    public internalName: string;

    @RdfProperty('sp:hasAppId')
    public appId: string;

    @RdfProperty('sp:isConnectedTo')
    public connectedTo: BoundPipelineElement[] = [];


    constructor(id: string) {
        this.id = id;
    }

}