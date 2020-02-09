import {DashboardWidgetSettings} from "../../../../core-model/dashboard/DashboardWidgetSettings";

export abstract class WidgetConfig {

    abstract getConfig(): DashboardWidgetSettings;
}