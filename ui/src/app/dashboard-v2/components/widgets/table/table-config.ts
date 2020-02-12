import {WidgetConfig} from "../base/base-config";
import {DashboardWidgetSettings} from "../../../../core-model/dashboard/DashboardWidgetSettings";
import {WidgetConfigBuilder} from "../../../registry/widget-config-builder";
import {SchemaRequirementsBuilder} from "../../../sdk/schema-requirements-builder";
import {EpRequirements} from "../../../sdk/ep-requirements";

export class TableConfig extends WidgetConfig {

    static readonly TITLE_KEY: string = "title-key";
    static readonly SELECTED_PROPERTIES_KEYS: string = "selected-fields-key";

    constructor() {
        super();
    }

    getConfig(): DashboardWidgetSettings {
        return WidgetConfigBuilder.create("table", "table")
            .requiredSchema(SchemaRequirementsBuilder
                .create()
                .requiredPropertyWithNaryMapping(TableConfig.SELECTED_PROPERTIES_KEYS, "Select properties", "", EpRequirements.anyProperty())
                .build())
            .requiredTextParameter(TableConfig.TITLE_KEY, "Title", "The title")
            .build();
    }

}