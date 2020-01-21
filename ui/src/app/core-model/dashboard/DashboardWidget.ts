import {RdfsClass} from "../../platform-services/tsonld/RdfsClass";
import {RdfId} from "../../platform-services/tsonld/RdfId";
import {RdfProperty} from "../../platform-services/tsonld/RdfsProperty";
import {EventSchema} from "../../connect/schema-editor/model/EventSchema";
import {DashboardWidgetSettings} from "./DashboardWidgetSettings";
import {DashboardWidgetDataConfig} from "./DashboardWidgetDataConfig";
import {VisualizablePipeline} from "./VisualizablePipeline";

@RdfsClass('sp:DashboardWidgetModel')
export class DashboardWidget {

    @RdfId
    public id: string;

    @RdfProperty('sp:hasDashboardWidgetSettings')
    dashboardWidgetSettings: DashboardWidgetSettings;

    @RdfProperty('sp:hasDashboardWidgetDataConfig')
    dashboardWidgetDataConfig: VisualizablePipeline;

    @RdfProperty('sp:hasCouchDbId')
    _id: string;

    @RdfProperty('sp:hasCouchDbRev')
    _ref:string;

    @RdfProperty('sp:hasDashboardWidgetId')
    widgetId:string;


    constructor(id: string) {
        this.id = id;
    }
}