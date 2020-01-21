import {WidgetConfigBuilder} from "../../../registry/widget-config-builder";
import {WidgetSettings} from "../../../models/widget-settings.model";
import {SchemaRequirementsBuilder} from "../../../sdk/schema-requirements-builder";
import {EpRequirements} from "../../../sdk/ep-requirements";

export class NumberConfig {

    constructor() {

    }

    static getConfig(): WidgetSettings {
        return WidgetConfigBuilder.create("number", "number")
            .requiredSchema(SchemaRequirementsBuilder
                .create()
                .requiredPropertyWithUnaryMapping("number-mapping", "Select property", "", EpRequirements.numberReq())
                .build())
            .requiredTextParameter("hi", "hi", "hi")
            .build();
    }

}