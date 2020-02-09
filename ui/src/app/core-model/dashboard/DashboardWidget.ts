import {RdfsClass} from "../../platform-services/tsonld/RdfsClass";
import {RdfId} from "../../platform-services/tsonld/RdfId";
import {RdfProperty} from "../../platform-services/tsonld/RdfsProperty";
import {EventSchema} from "../../connect/schema-editor/model/EventSchema";
import {DashboardWidgetSettings} from "./DashboardWidgetSettings";
import {DashboardWidgetDataConfig} from "./DashboardWidgetDataConfig";
import {VisualizablePipeline} from "./VisualizablePipeline";
import {UnnamedStreamPipesEntity} from "../../connect/model/UnnamedStreamPipesEntity";

@RdfsClass('sp:DashboardWidgetModel')
export class DashboardWidget extends UnnamedStreamPipesEntity {

    @RdfProperty('sp:hasDashboardWidgetSettings')
    dashboardWidgetSettings: DashboardWidgetSettings;

    @RdfProperty('sp:hasDashboardWidgetDataConfig')
    dashboardWidgetDataConfig: VisualizablePipeline;

    @RdfProperty('sp:couchDbId')
    _id: string;

    @RdfProperty('sp:couchDbRev')
    _ref:string;

    @RdfProperty('sp:hasDashboardWidgetId')
    widgetId:string;

    constructor() {
        super();
    }
}