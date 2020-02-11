import {NumberConfig} from "../components/widgets/number/number-config";
import {DashboardWidgetSettings} from "../../core-model/dashboard/DashboardWidgetSettings";
import {WidgetConfig} from "../components/widgets/base/base-config";
import {LineConfig} from "../components/widgets/line/line-config";

export class WidgetRegistry {

    private static availableWidgets: Array<WidgetConfig> = [
        new NumberConfig(),
        new LineConfig()
    ];

    static getAvailableWidgetTemplates(): Array<DashboardWidgetSettings> {
        let widgetTemplates = new Array<DashboardWidgetSettings>();
        this.availableWidgets.forEach(widget => widgetTemplates.push(widget.getConfig()));
        return widgetTemplates;
    }
}