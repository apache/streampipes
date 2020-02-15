import {WidgetConfigBuilder} from "../../../registry/widget-config-builder";
import {SchemaRequirementsBuilder} from "../../../sdk/schema-requirements-builder";
import {EpRequirements} from "../../../sdk/ep-requirements";
import {DashboardWidgetSettings} from "../../../../core-model/dashboard/DashboardWidgetSettings";
import {WidgetConfig} from "../base/base-config";

export class NumberConfig extends WidgetConfig {

    static readonly NUMBER_MAPPING_KEY: string = "number-mapping";

    constructor() {
        super();
    }

    getConfig(): DashboardWidgetSettings {
        return WidgetConfigBuilder.createWithSelectableColorsAndTitlePanel("number", "number")
            .requiredSchema(SchemaRequirementsBuilder
                .create()
                .requiredPropertyWithUnaryMapping(NumberConfig.NUMBER_MAPPING_KEY, "Select property", "", EpRequirements.numberReq())
                .build())
            .build();
    }

}