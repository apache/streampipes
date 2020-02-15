import {NumberConfig} from "../components/widgets/number/number-config";
import {DashboardWidgetSettings} from "../../core-model/dashboard/DashboardWidgetSettings";
import {WidgetConfig} from "../components/widgets/base/base-config";
import {LineConfig} from "../components/widgets/line/line-config";
import {TableConfig} from "../components/widgets/table/table-config";
import {GaugeConfig} from "../components/widgets/gauge/gauge-config";
import {ImageConfig} from "../components/widgets/image/image-config";
import {AreaConfig} from "../components/widgets/area/area-config";

export class WidgetRegistry {

    private static availableWidgets: Array<WidgetConfig> = [
        new NumberConfig(),
        new LineConfig(),
        new TableConfig(),
        new GaugeConfig(),
        new ImageConfig(),
        new AreaConfig()
    ];

    static getAvailableWidgetTemplates(): Array<DashboardWidgetSettings> {
        let widgetTemplates = new Array<DashboardWidgetSettings>();
        this.availableWidgets.forEach(widget => widgetTemplates.push(widget.getConfig()));
        return widgetTemplates;
    }
}