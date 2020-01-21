import {EventPropertyPrimitive} from "../../../connect/schema-editor/model/EventPropertyPrimitive";
import {DatatypeMatch} from "./datatype-match";
import {DomainPropertyMatch} from "./domain-property-match";

export class PrimitivePropertyMatch {

    match(requirement: EventPropertyPrimitive, offer: EventPropertyPrimitive) {
        if (requirement != undefined) {
            return new DatatypeMatch().match(requirement.runtimeType, offer.runtimeType) &&
                new DomainPropertyMatch().match(requirement.domainProperty, offer.domainProperty);
        } else {
            return true;
        }
    }
}