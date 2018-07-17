import {EventProperty} from './EventProperty';
import {Injectable} from '@angular/core';
import {RdfsClass} from '../../tsonld/RdfsClass';
import {RdfProperty} from '../../tsonld/RdfsProperty';
import {RdfId} from '../../tsonld/RdfId';

@Injectable()
@RdfsClass('sp:EventSchema')
export class EventSchema {

    private static serialVersionUID = -3994041794693686406;

    @RdfId
    public id: string;

    @RdfProperty('sp:hasEventProperty')
    public eventProperties: Array<EventProperty>;


    constructor () {
        this.eventProperties = new Array(0);
    }

    public copy(): EventSchema {
        const newEventSchema = new EventSchema();

        for (let ep of this.eventProperties) {
            newEventSchema.eventProperties.push(ep.copy());
        }

        return newEventSchema;
    }

}

