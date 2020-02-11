import {WidgetConfigBuilder} from "../../../registry/widget-config-builder";
import {SchemaRequirementsBuilder} from "../../../sdk/schema-requirements-builder";
import {EpRequirements} from "../../../sdk/ep-requirements";
import {DashboardWidgetSettings} from "../../../../core-model/dashboard/DashboardWidgetSettings";
import {WidgetConfig} from "../base/base-config";

export class NumberConfig extends WidgetConfig {

    static readonly TITLE_KEY: string = "title-key";
    static readonly NUMBER_MAPPING_KEY: string = "number-mapping";
    static readonly COLOR_KEY: string = "color-key";

    constructor() {
        super();
    }

    getConfig(): DashboardWidgetSettings {
        return WidgetConfigBuilder.create("number", "number")
            .requiredSchema(SchemaRequirementsBuilder
                .create()
                .requiredPropertyWithUnaryMapping(NumberConfig.NUMBER_MAPPING_KEY, "Select property", "", EpRequirements.numberReq())
                .build())
            .requiredTextParameter(NumberConfig.TITLE_KEY, "Title", "The title")
            .requiredColorParameter(NumberConfig.COLOR_KEY, "Background color", "The background color")
            .build();
    }

}