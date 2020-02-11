import {WidgetConfig} from "../base/base-config";
import {DashboardWidgetSettings} from "../../../../core-model/dashboard/DashboardWidgetSettings";
import {WidgetConfigBuilder} from "../../../registry/widget-config-builder";
import {SchemaRequirementsBuilder} from "../../../sdk/schema-requirements-builder";
import {EpRequirements} from "../../../sdk/ep-requirements";

export class LineConfig extends WidgetConfig {

    static readonly TITLE_KEY: string = "title";
    static readonly NUMBER_MAPPING_KEY: string = "number-mapping";
    static readonly TIMESTAMP_MAPPING_KEY: string = "timestamp-mapping";
    static readonly MIN_Y_AXIS_KEY: string = "min-y-axis-key";
    static readonly MAX_Y_AXIS_KEY: string = "max-y-axis-key";

    getConfig(): DashboardWidgetSettings {
        return WidgetConfigBuilder.create("line", "line")
            .requiredSchema(SchemaRequirementsBuilder
                .create()
                .requiredPropertyWithUnaryMapping(LineConfig.TIMESTAMP_MAPPING_KEY, "Timestamp field", "", EpRequirements.timestampReq())
                .requiredPropertyWithUnaryMapping(LineConfig.NUMBER_MAPPING_KEY, "Number field", "", EpRequirements.numberReq())
                .build())
            .requiredTextParameter(LineConfig.TITLE_KEY, "hi", "hi")
            .requiredIntegerParameter(LineConfig.MIN_Y_AXIS_KEY, "Y-axis range (min)", "")
            .requiredIntegerParameter(LineConfig.MAX_Y_AXIS_KEY, "Y-axis range (min)", "")
            .build();
    }

}