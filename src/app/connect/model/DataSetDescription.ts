import {RdfId} from '../../platform-services/tsonld/RdfId';
import {RdfProperty} from '../../platform-services/tsonld/RdfsProperty';
import {RdfsClass} from '../../platform-services/tsonld/RdfsClass';
import {FormatDescription} from './connect/grounding/FormatDescription';
import {ProtocolDescription} from './connect/grounding/ProtocolDescription';
import {EventSchema} from '../schema-editor/model/EventSchema';

@RdfsClass('sp:DataSet')
export class DataSetDescription {

  @RdfId
  public id: string;

  @RdfProperty('http://www.w3.org/2000/01/rdf-schema#label')
  public label: string;

  @RdfProperty('http://www.w3.org/2000/01/rdf-schema#description')
  public description: string;

  @RdfProperty('sp:hasUri')
  public uri: string;

  @RdfProperty('sp:hasSchema')
  public eventSchema: EventSchema;

  constructor(id: string) {
    this.id = id;
  }

}