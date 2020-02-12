import {WidgetConfig} from "../base/base-config";
import {DashboardWidgetSettings} from "../../../../core-model/dashboard/DashboardWidgetSettings";
import {WidgetConfigBuilder} from "../../../registry/widget-config-builder";
import {SchemaRequirementsBuilder} from "../../../sdk/schema-requirements-builder";
import {EpRequirements} from "../../../sdk/ep-requirements";

export class ImageConfig extends WidgetConfig {

    static readonly TITLE_KEY: string = "title-key";
    static readonly NUMBER_MAPPING_KEY: string = "number-mapping";

    constructor() {
        super();
    }

    getConfig(): DashboardWidgetSettings {
        return WidgetConfigBuilder.create("image", "image")
            .requiredSchema(SchemaRequirementsBuilder
                .create()
                .requiredPropertyWithUnaryMapping(ImageConfig.NUMBER_MAPPING_KEY, "Select property", "", EpRequirements.imageReq())
                .build())
            .requiredTextParameter(ImageConfig.TITLE_KEY, "Title", "The title")
            .build();
    }

}