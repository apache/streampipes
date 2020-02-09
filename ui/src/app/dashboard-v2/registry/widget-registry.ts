import {NumberConfig} from "../components/widgets/number/number-config.component";
import {DashboardWidgetSettings} from "../../core-model/dashboard/DashboardWidgetSettings";
import {WidgetConfig} from "../components/widgets/base/base-config";

export class WidgetRegistry {

    private static availableWidgets: Array<WidgetConfig> = [new NumberConfig()];

    static getAvailableWidgetTemplates(): Array<DashboardWidgetSettings> {
        let widgetTemplates = new Array<DashboardWidgetSettings>();
        this.availableWidgets.forEach(widget => widgetTemplates.push(widget.getConfig()));
        return widgetTemplates;
    }
}