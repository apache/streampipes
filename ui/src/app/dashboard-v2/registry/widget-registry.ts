import {NumberConfig} from "../components/widgets/number/number-config.component";
import {WidgetSettings} from "../models/widget-settings.model";

export class WidgetRegistry {

    private static availableWidgets: Array<WidgetSettings> = [NumberConfig.getConfig()];

    static getAvailableWidgets(): Array<WidgetSettings> {
        return this.availableWidgets;
    }
}