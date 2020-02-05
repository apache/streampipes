import {WidgetConfigBuilder} from "../../../registry/widget-config-builder";
import {SchemaRequirementsBuilder} from "../../../sdk/schema-requirements-builder";
import {EpRequirements} from "../../../sdk/ep-requirements";
import {DashboardWidgetSettings} from "../../../../core-model/dashboard/DashboardWidgetSettings";

export class NumberConfig {

    constructor() {

    }

    static getConfig(): DashboardWidgetSettings {
        return WidgetConfigBuilder.create("number", "number")
            .requiredSchema(SchemaRequirementsBuilder
                .create()
                .requiredPropertyWithUnaryMapping("number-mapping", "Select property", "", EpRequirements.numberReq())
                .build())
            .requiredTextParameter("hi", "hi", "hi")
            .build();
    }

}