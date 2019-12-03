import {GridsterConfig, GridsterItem} from "angular-gridster2";
import {StaticProperty} from "../../connect/model/StaticProperty";

export interface DashboardConfig extends GridsterConfig {}

export interface DashboardItem extends GridsterItem {}

export interface DashboardWidget extends DashboardItem {
    settings?: Array<StaticProperty>
    widgetType: string;
}

export interface Dashboard {
    id?: string;
    name?: string;
    widgets?: Array<DashboardWidget>;
}