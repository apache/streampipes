import {RdfId} from '../tsonld/RdfId';
import {RdfProperty} from '../tsonld/RdfsProperty';
import {RdfsClass} from '../tsonld/RdfsClass';
import {FormatDescription} from './FormatDescription';
import {ProtocolDescription} from './ProtocolDescription';

@RdfsClass('sp:AdapterDescription')
export class AdapterDescription {

  @RdfId
  public id: string;

  @RdfProperty('http://www.w3.org/2000/01/rdf-schema#label')
  public label: string;

  @RdfProperty('sp:couchDBId')
  public couchDbId: string;

  @RdfProperty('sp:userName')
  public userName: string;

  @RdfProperty('sp:hasProtocol')
  public protocol: ProtocolDescription;

  @RdfProperty('sp:hasFormat')
  public format: FormatDescription;

  constructor(id: string) {
    this.id = id;
  }

}
