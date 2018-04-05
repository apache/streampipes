import {RdfId} from '../tsonld/RdfId';
import {RdfProperty} from '../tsonld/RdfsProperty';
import {RdfsClass} from '../tsonld/RdfsClass';
import {FormatDescription} from './FormatDescription';
import {ProtocolDescription} from './ProtocolDescription';
import {DataSetDescription} from './DataSetDescription';

@RdfsClass('sp:AdapterDescription')
export class AdapterDescription {

  @RdfId
  public id: string;

  @RdfProperty('http://www.w3.org/2000/01/rdf-schema#label')
  public label: string;

  @RdfProperty('sp:couchDBId')
  public couchDbId: string;

  @RdfProperty('sp:hasProtocol')
  public protocol: ProtocolDescription;

  @RdfProperty('sp:hasFormat')
  public format: FormatDescription;

  @RdfProperty("sp:hasDataSet")
  public dataSet: DataSetDescription;

  constructor(id: string) {
    this.id = id;
  }

}
