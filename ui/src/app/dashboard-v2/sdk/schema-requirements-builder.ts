import {EventProperty} from "../../connect/schema-editor/model/EventProperty";
import {StaticProperty} from "../../connect/model/StaticProperty";
import {CollectedSchemaRequirements} from "./collected-schema-requirements";
import {MappingPropertyUnary} from "../../connect/model/MappingPropertyUnary";
import {MappingPropertyNary} from "../../connect/model/MappingPropertyNary";

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
        let mp = this.makeMappingProperty(internalId, label, description, new MappingPropertyUnary());

        this.staticProperties.push(mp);
        this.requiredEventProperties.push(eventProperty);

        return this;
    }

    requiredPropertyWithNaryMapping(internalId: string, label: string, description: string, eventProperty: EventProperty): SchemaRequirementsBuilder {
        eventProperty.setRuntimeName(internalId);
        let mp = this.makeMappingProperty(internalId, label, description, new MappingPropertyNary());

        this.staticProperties.push(mp);
        this.requiredEventProperties.push(eventProperty);

        return this;
    }

    makeMappingProperty(internalId: string, label: string, description: string, sp: StaticProperty): StaticProperty {
        sp.internalName = internalId;
        sp.label = label;
        sp.description = description;
        return sp;
    }

    build() {
        return new CollectedSchemaRequirements(this.requiredEventProperties, this.staticProperties);
    }
}