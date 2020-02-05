import {NumberConfig} from "../components/widgets/number/number-config.component";
import {DashboardWidgetSettings} from "../../core-model/dashboard/DashboardWidgetSettings";

export class WidgetRegistry {

    private static availableWidgets: Array<DashboardWidgetSettings> = [NumberConfig.getConfig()];

    static getAvailableWidgets(): Array<DashboardWidgetSettings> {
        return this.availableWidgets;
    }
}