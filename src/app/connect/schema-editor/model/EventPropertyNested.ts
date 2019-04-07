import {EventProperty} from './EventProperty';
import {RdfsClass} from '../../../platform-services/tsonld/RdfsClass';
import {RdfProperty} from '../../../platform-services/tsonld/RdfsProperty';

@RdfsClass('sp:EventPropertyNested')
export class EventPropertyNested extends EventProperty {

    @RdfProperty('sp:hasEventProperty')
    eventProperties: EventProperty[];

    constructor(propertyID: string, parent: EventProperty) {
        super(propertyID, parent);
        this.eventProperties = new Array(0);
    }

    public copy(): EventProperty {
        const result = new EventPropertyNested(this.id, null);
        result.id = this.id;
        result.label = this.label;
        result.description = this.description;
        result.runTimeName = this.runTimeName;
        result.domainProperty = this.domainProperty;

        for (let ep of this.eventProperties) {
            result.eventProperties.push(ep.copy());
        }

        return result;
    }


}
