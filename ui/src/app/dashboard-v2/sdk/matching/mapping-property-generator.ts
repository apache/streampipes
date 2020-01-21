import {EventProperty} from "../../../connect/schema-editor/model/EventProperty";
import {PropertyMatch} from "./property-match";

export class MappingPropertyGenerator {

    private selector: string = "s0";
    private separator: string = "::";

    constructor(private requiredEventProperty: EventProperty, private providedEventProperties: Array<EventProperty>) {

    }

    computeMatchingProperties(): Array<string> {
        let mapsFromOptions: Array<string> = [];

        this.providedEventProperties.forEach(ep => {
           if (new PropertyMatch().match(this.requiredEventProperty, ep)) {
               mapsFromOptions.push(this.makeSelector(ep.runtimeName));
           }
        });

        return mapsFromOptions;
    }

    makeSelector(runtimeName: string): string {
        return this.selector + this.separator + runtimeName;
    }
}