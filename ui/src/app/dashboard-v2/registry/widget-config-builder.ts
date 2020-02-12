import {FreeTextStaticProperty} from "../../connect/model/FreeTextStaticProperty";
import {CollectedSchemaRequirements} from "../sdk/collected-schema-requirements";
import {EventSchema} from "../../connect/schema-editor/model/EventSchema";
import {DashboardWidgetSettings} from "../../core-model/dashboard/DashboardWidgetSettings";
import {Vocabulary} from "../sdk/model/vocabulary";
import {Datatypes} from "../sdk/model/datatypes";
import {ColorPickerStaticProperty} from "../../connect/model/ColorPickerStaticProperty";

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
        let fst: FreeTextStaticProperty = this.prepareStaticProperty(id, label, description, Datatypes.String.toUri())
        this.widget.config.push(fst);
        return this;
    }

    requiredColorParameter(id: string, label: string, description: string, defaultColor?: string): WidgetConfigBuilder {
        let csp = new ColorPickerStaticProperty();
        csp.internalName = id;
        csp.label = label;
        csp.description = description;
        if (defaultColor) {
            csp.selectedColor = defaultColor;
        }
        this.widget.config.push(csp);
        return this;
    }


    requiredIntegerParameter(id: string, label: string, description: string): WidgetConfigBuilder {
        let fst: FreeTextStaticProperty = this.prepareStaticProperty(id, label, description, Datatypes.Integer.toUri())
        this.widget.config.push(fst);
        return this;
    }

    requiredFloatParameter(id: string, label: string, description: string): WidgetConfigBuilder {
        let fst: FreeTextStaticProperty = this.prepareStaticProperty(id, label, description, Datatypes.Float.toUri())
        this.widget.config.push(fst);
        return this;
    }

    requiredSchema(collectedSchemaRequirements: CollectedSchemaRequirements): WidgetConfigBuilder {
        this.widget.requiredSchema = collectedSchemaRequirements.getEventSchema();
        this.widget.config = this.widget.config.concat(collectedSchemaRequirements.getRequiredMappingProperties());

        return this;
    }

    prepareStaticProperty(id: string, label: string, description: string, datatype: string) {
        let fst: FreeTextStaticProperty = new FreeTextStaticProperty();
        fst.internalName = id;
        fst.label = label;
        fst.description = description;
        fst.requiredDatatype = datatype;

        return fst;
    }

    build(): DashboardWidgetSettings {
        return this.widget;
    }

}