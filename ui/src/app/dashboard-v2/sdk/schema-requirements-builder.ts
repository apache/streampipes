import {EventProperty} from "../../connect/schema-editor/model/EventProperty";
import {StaticProperty} from "../../connect/model/StaticProperty";
import {CollectedSchemaRequirements} from "./collected-schema-requirements";
import {MappingPropertyUnary} from "../../connect/model/MappingPropertyUnary";

export class SchemaRequirementsBuilder {

    private requiredEventProperties: Array<EventProperty>;
    private staticProperties: Array<StaticProperty>;

    private constructor() {
        this.requiredEventProperties = [];
        this.staticProperties = [];
    }

    static create(): SchemaRequirementsBuilder {
        return new SchemaRequirementsBuilder();
    }

    requiredPropertyWithUnaryMapping(internalId: string, label: string, description: string, eventProperty: EventProperty): SchemaRequirementsBuilder {
        eventProperty.setRuntimeName(internalId);
        let mp = new MappingPropertyUnary(internalId);
        mp.label = label;
        mp.description = description;
        mp.internalName = internalId;

        this.staticProperties.push(mp);
        this.requiredEventProperties.push(eventProperty);

        return this;
    }

    build() {
        return new CollectedSchemaRequirements(this.requiredEventProperties, this.staticProperties);
    }
}