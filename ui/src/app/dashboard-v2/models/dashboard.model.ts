import {GridsterConfig, GridsterItem} from "angular-gridster2";
import {StaticProperty} from "../../connect/model/StaticProperty";
import {WidgetData} from "./widget-data.model";
import {VisualizablePipeline} from "../../core-model/dashboard/VisualizablePipeline";

export interface DashboardConfig extends GridsterConfig {}

export interface DashboardItem extends GridsterItem {
    widgetId: string;
}

export interface Dashboard {
    id?: string;
    name?: string;
    widgets?: Array<DashboardItem>;
}