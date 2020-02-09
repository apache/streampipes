import {GridsterConfig, GridsterItem} from "angular-gridster2";

export interface DashboardConfig extends GridsterConfig {}

export interface DashboardItem extends GridsterItem {
    widgetId: string;
    id: string;
}

export interface Dashboard {
    id?: string;
    name?: string;
    description?: string;
    widgets?: Array<DashboardItem>;
    _id?: string;
    _rev?: string;
}