import {WidgetConfig} from "./base-config";
import {DashboardWidgetSettings} from "../../../../core-model/dashboard/DashboardWidgetSettings";
import {WidgetConfigBuilder} from "../../../registry/widget-config-builder";
import {SchemaRequirementsBuilder} from "../../../sdk/schema-requirements-builder";
import {EpRequirements} from "../../../sdk/ep-requirements";

export abstract class BaseNgxLineConfig extends WidgetConfig {

    static readonly NUMBER_MAPPING_KEY: string = "number-mapping";
    static readonly TIMESTAMP_MAPPING_KEY: string = "timestamp-mapping";
    static readonly MIN_Y_AXIS_KEY: string = "min-y-axis-key";
    static readonly MAX_Y_AXIS_KEY: string = "max-y-axis-key";

    getConfig(): DashboardWidgetSettings {
        return WidgetConfigBuilder.createWithSelectableColorsAndTitlePanel(this.getWidgetName(), this.getWidgetLabel())
            .requiredSchema(SchemaRequirementsBuilder
                .create()
                .requiredPropertyWithUnaryMapping(BaseNgxLineConfig.TIMESTAMP_MAPPING_KEY, "Timestamp field", "", EpRequirements.timestampReq())
                .requiredPropertyWithUnaryMapping(BaseNgxLineConfig.NUMBER_MAPPING_KEY, "Number field", "", EpRequirements.numberReq())
                .build())
            .requiredIntegerParameter(BaseNgxLineConfig.MIN_Y_AXIS_KEY, "Y-axis range (min)", "")
            .requiredIntegerParameter(BaseNgxLineConfig.MAX_Y_AXIS_KEY, "Y-axis range (min)", "")
            .build();
    }

    abstract getWidgetName(): string;

    abstract getWidgetLabel(): string;
}