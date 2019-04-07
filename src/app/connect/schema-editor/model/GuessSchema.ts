import { RdfsClass } from '../../../platform-services/tsonld/RdfsClass';
import { RdfProperty } from '../../../platform-services/tsonld/RdfsProperty';
import { RdfId } from '../../../platform-services/tsonld/RdfId';
import { EventSchema } from './EventSchema';
import { DomainPropertyProbabilityList } from './DomainPropertyProbabilityList';

@RdfsClass('sp:GuessSchema')
export class GuessSchema {
  private static serialVersionUID = -3994041794693686406;

  @RdfId
  public id: string;

  @RdfProperty('sp:hasEventSchema')
  public eventSchema: EventSchema;

  @RdfProperty('sp:propertyProbabilityList')
  public eventProperties: Array<DomainPropertyProbabilityList>;

  constructor() {}
}
