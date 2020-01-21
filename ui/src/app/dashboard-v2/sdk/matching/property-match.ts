import {EventProperty} from "../../../connect/schema-editor/model/EventProperty";
import {EventPropertyPrimitive} from "../../../connect/schema-editor/model/EventPropertyPrimitive";
import {PrimitivePropertyMatch} from "./primitive-property-match";

export class PropertyMatch {

    match(requirement: EventProperty, offer: EventProperty): boolean {
        if (requirement instanceof EventPropertyPrimitive && offer instanceof EventPropertyPrimitive) {
            return new PrimitivePropertyMatch().match(requirement, offer);
        } else {
            return false;
        }
    }
}