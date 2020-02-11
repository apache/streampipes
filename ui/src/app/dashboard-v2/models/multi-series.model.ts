import {DashboardItem} from "./dashboard.model";

export interface MultiSeries {
    name?: string;
    series?: Array<MultiSeriesEntry>;
}

export interface MultiSeriesEntry {
    name: string;
    value: any;
}