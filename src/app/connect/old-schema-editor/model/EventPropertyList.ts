import {EventProperty} from './EventProperty';
import {RdfsClass} from '../../tsonld/RdfsClass';
import {RdfProperty} from '../../tsonld/RdfsProperty';
//  extends EventProperty

@RdfsClass('sp:EventPropertyList')
export class EventPropertyList extends EventProperty {

  @RdfProperty('sp:hasEventProperty')
  eventProperties: EventProperty[];

  constructor(propertyID: string, parent: EventProperty) {
    super(propertyID, parent);
    this.eventProperties = new Array(0);
  }
}
