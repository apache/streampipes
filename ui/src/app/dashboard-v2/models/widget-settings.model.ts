import {StaticProperty} from "../../connect/model/StaticProperty";
import {EventSchema} from "../../connect/schema-editor/model/EventSchema";

export interface WidgetSettings {
    name?: string;
    label?: string;
    icon?: string;
    requiredSchema?: EventSchema;
    config?: Array<StaticProperty>;
}