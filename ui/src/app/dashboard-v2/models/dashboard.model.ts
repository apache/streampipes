import {GridsterConfig, GridsterItem} from "angular-gridster2";
import {StaticProperty} from "../../connect/model/StaticProperty";
import {WidgetSettings} from "./widget-settings.model";
import {WidgetData} from "./widget-data.model";
import {VisualizablePipeline} from "../../core-model/dashboard/VisualizablePipeline";

export interface DashboardConfig extends GridsterConfig {}

export interface DashboardItem extends GridsterItem {}

export interface DashboardWidget extends DashboardItem {
    widgetId: string;
}

export interface ConfiguredWidget {
    widgetId: string;
    widgetSettings: WidgetSettings;
    widgetDataConfig: VisualizablePipeline;
    widgetType: string;
}

export interface Dashboard {
    id?: string;
    name?: string;
    widgets?: Array<DashboardWidget>;
}