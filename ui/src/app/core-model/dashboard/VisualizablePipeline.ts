import {RdfsClass} from "../../platform-services/tsonld/RdfsClass";
import {EventSchema} from "../../connect/schema-editor/model/EventSchema";
import {RdfId} from "../../platform-services/tsonld/RdfId";
import {RdfProperty} from "../../platform-services/tsonld/RdfsProperty";

@RdfsClass('sp:VisualizablePipeline')
export class VisualizablePipeline {

    @RdfId
    public id: string;

    @RdfProperty('sp:hasSchema')
    schema: EventSchema;

    @RdfProperty('sp:hasPipelineId')
    pipelineId: string;

    @RdfProperty('sp:hasCouchDbId')
    _id: string;

    @RdfProperty('sp:hasCouchDbRev')
    _ref:string;

    @RdfProperty('sp:hasVisualizationName')
    visualizationName:string;

    @RdfProperty('sp:hasTopic')
    topic:string;

    constructor(id: string) {
        this.id = id;
    }

}