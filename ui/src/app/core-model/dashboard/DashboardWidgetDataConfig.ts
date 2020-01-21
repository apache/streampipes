import {RdfId} from "../../platform-services/tsonld/RdfId";
import {RdfProperty} from "../../platform-services/tsonld/RdfsProperty";
import {StaticProperty} from "../../connect/model/StaticProperty";
import {EventSchema} from "../../connect/schema-editor/model/EventSchema";
import {RdfsClass} from "../../platform-services/tsonld/RdfsClass";

@RdfsClass('sp:DashboardWidgetDataConfig')
export class DashboardWidgetDataConfig {
    @RdfId
    public id: string;

    @RdfProperty('sp:hasVisualizationName')
    visualizationName: string;

    @RdfProperty('sp:hasTopic')
    topic: string;

    @RdfProperty('sp:hasSchema')
    schema: EventSchema;

    constructor(id: string) {
        this.id = id;
    }
}