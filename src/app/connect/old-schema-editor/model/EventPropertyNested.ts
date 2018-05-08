import {EventProperty} from './EventProperty';
import {RdfsClass} from '../../tsonld/RdfsClass';
import {RdfProperty} from '../../tsonld/RdfsProperty';

@RdfsClass('sp:EventPropertyNested')
export class EventPropertyNested extends EventProperty {

  @RdfProperty('sp:hasEventProperty')
  eventProperties: EventProperty[];

  constructor(propertyID: string, parent: EventProperty) {
    super(propertyID, parent);
    this.eventProperties = new Array(0);
  }


}
