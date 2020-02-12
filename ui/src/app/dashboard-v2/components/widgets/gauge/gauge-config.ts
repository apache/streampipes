import {WidgetConfig} from "../base/base-config";
import {DashboardWidgetSettings} from "../../../../core-model/dashboard/DashboardWidgetSettings";
import {WidgetConfigBuilder} from "../../../registry/widget-config-builder";
import {SchemaRequirementsBuilder} from "../../../sdk/schema-requirements-builder";
import {EpRequirements} from "../../../sdk/ep-requirements";

export class GaugeConfig extends WidgetConfig {

    static readonly TITLE_KEY: string = "title-key";
    static readonly NUMBER_MAPPING_KEY: string = "number-mapping";
    static readonly COLOR_KEY: string = "color-key";
    static readonly MIN_KEY: string = "min-key";
    static readonly MAX_KEY: string = "max-key";

    constructor() {
        super();
    }

    getConfig(): DashboardWidgetSettings {
        return WidgetConfigBuilder.create("gauge", "gauge")
            .requiredSchema(SchemaRequirementsBuilder
                .create()
                .requiredPropertyWithUnaryMapping(GaugeConfig.NUMBER_MAPPING_KEY, "Select property", "", EpRequirements.numberReq())
                .build())
            .requiredTextParameter(GaugeConfig.TITLE_KEY, "Title", "The title")
            .requiredIntegerParameter(GaugeConfig.MIN_KEY, "Min Y axis value", "")
            .requiredIntegerParameter(GaugeConfig.MAX_KEY, "Max Y axis value", "")
            .requiredColorParameter(GaugeConfig.COLOR_KEY, "Color", "The background color", "#000000")
            .build();
    }

}