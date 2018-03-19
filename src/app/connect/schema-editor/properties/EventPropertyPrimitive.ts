import {EventProperty} from './EventProperty';
import {RdfsClass} from '../../tsonld/RdfsClass';
import {RdfProperty} from '../../tsonld/RdfsProperty';


@RdfsClass('sp:EventPropertyPrimitive')
export class EventPropertyPrimitive extends EventProperty {

  constructor (propertyID: string, parent: EventProperty) {
    super(propertyID, parent);
  }

  @RdfProperty('sp:hasPropertyType')
  public runtimeType: string;

  @RdfProperty('sp:hasMeasurementUnit')
  public measurementUnit: string;

  @RdfProperty('sp:hasValueSpecification')
  public valueSpecification: string;

}
