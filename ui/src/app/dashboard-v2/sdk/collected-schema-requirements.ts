import {StaticProperty} from "../../connect/model/StaticProperty";
import {EventProperty} from "../../connect/schema-editor/model/EventProperty";
import {EventSchema} from "../../connect/schema-editor/model/EventSchema";

export class CollectedSchemaRequirements {

    constructor(private requiredEventProperties: Array<EventProperty>, private requiredMappingProperties: Array<StaticProperty>) {

    }

    getEventSchema(): EventSchema {
        let eventSchema = new EventSchema();
        eventSchema.eventProperties = this.requiredEventProperties;
        return eventSchema;
    }

    getRequiredMappingProperties(): Array<StaticProperty> {
        return this.requiredMappingProperties;
    }
}