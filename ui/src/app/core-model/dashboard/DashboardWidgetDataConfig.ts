import {RdfId} from "../../platform-services/tsonld/RdfId";
import {RdfProperty} from "../../platform-services/tsonld/RdfsProperty";
import {StaticProperty} from "../../connect/model/StaticProperty";
import {EventSchema} from "../../connect/schema-editor/model/EventSchema";
import {RdfsClass} from "../../platform-services/tsonld/RdfsClass";
import {UnnamedStreamPipesEntity} from "../../connect/model/UnnamedStreamPipesEntity";

@RdfsClass('sp:DashboardWidgetDataConfig')
export class DashboardWidgetDataConfig extends UnnamedStreamPipesEntity {

    @RdfProperty('sp:hasVisualizationName')
    visualizationName: string;

    @RdfProperty('sp:hasTopic')
    topic: string;

    @RdfProperty('sp:hasSchema')
    schema: EventSchema;

    constructor() {
        super();
    }
}