import {FreeTextStaticProperty} from "../../connect/model/FreeTextStaticProperty";
import {CollectedSchemaRequirements} from "../sdk/collected-schema-requirements";
import {EventSchema} from "../../connect/schema-editor/model/EventSchema";
import {DashboardWidgetSettings} from "../../core-model/dashboard/DashboardWidgetSettings";

export class WidgetConfigBuilder {

    private widget: DashboardWidgetSettings;

    private constructor(widgetName: string, widgetLabel: string) {
        this.widget = new DashboardWidgetSettings();
        this.widget.widgetLabel = widgetLabel;
        this.widget.widgetName = widgetName;
        this.widget.config = [];
    }

    static create(widgetName: string, widgetLabel: string): WidgetConfigBuilder {
        return new WidgetConfigBuilder(widgetName, widgetLabel);
    }

    requiredTextParameter(id: string, label: string, description: string): WidgetConfigBuilder {
        let fst: FreeTextStaticProperty = new FreeTextStaticProperty();
        fst.internalName = id;
        fst.label = label;
        fst.description = description;
        this.widget.config.push(fst);
        return this;
    }

    requiredSchema(collectedSchemaRequirements: CollectedSchemaRequirements): WidgetConfigBuilder {
        this.widget.requiredSchema = collectedSchemaRequirements.getEventSchema();
        this.widget.config = this.widget.config.concat(collectedSchemaRequirements.getRequiredMappingProperties());

        return this;
    }

    build(): DashboardWidgetSettings {
        return this.widget;
    }

}