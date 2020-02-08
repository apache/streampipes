import {WidgetConfigBuilder} from "../../../registry/widget-config-builder";
import {SchemaRequirementsBuilder} from "../../../sdk/schema-requirements-builder";
import {EpRequirements} from "../../../sdk/ep-requirements";
import {DashboardWidgetSettings} from "../../../../core-model/dashboard/DashboardWidgetSettings";

export class NumberConfig {

    static TITLE_KEY: string = "hi";
    static NUMBER_MAPPING_KEY: string = "number-mapping";

    constructor() {

    }

    static getConfig(): DashboardWidgetSettings {
        return WidgetConfigBuilder.create("number", "number")
            .requiredSchema(SchemaRequirementsBuilder
                .create()
                .requiredPropertyWithUnaryMapping(this.TITLE_KEY, "Select property", "", EpRequirements.numberReq())
                .build())
            .requiredTextParameter(this.NUMBER_MAPPING_KEY, "hi", "hi")
            .build();
    }

}