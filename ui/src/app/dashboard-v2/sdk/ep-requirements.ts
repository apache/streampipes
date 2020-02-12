import {EventProperty} from "../../connect/schema-editor/model/EventProperty";
import {EventPropertyPrimitive} from "../../connect/schema-editor/model/EventPropertyPrimitive";
import {Datatypes} from "./model/datatypes";

export class EpRequirements {

    private static ep(): EventPropertyPrimitive {
        let ep = new EventPropertyPrimitive(undefined, undefined);
        return ep;
    }

    static anyProperty(): EventProperty {
        return EpRequirements.ep();
    }

    static timestampReq(): EventProperty {
        return EpRequirements.domainPropertyReq("http://schema.org/DateTime");
    }

    static numberReq(): EventProperty {
        return EpRequirements.datatypeReq(Datatypes.Number);
    }

    static stringReq(): EventProperty {
        return EpRequirements.datatypeReq(Datatypes.String);
    }

    static integerReq(): EventProperty {
        return EpRequirements.datatypeReq(Datatypes.Integer);
    }

    static domainPropertyReq(domainProperty: string): EventPropertyPrimitive {
        let eventProperty = EpRequirements.ep();
        eventProperty.setDomainProperty(domainProperty);
        return eventProperty;

    }

    static datatypeReq(datatype: Datatypes): EventPropertyPrimitive {
        let eventProperty = EpRequirements.ep();
        eventProperty.setRuntimeType(datatype.toUri());
        return eventProperty;
}


}