import {RdfId} from '../../../tsonld/RdfId';
import {RdfProperty} from '../../../tsonld/RdfsProperty';
import {RdfsClass} from '../../../tsonld/RdfsClass';
import {StaticProperty} from '../../StaticProperty';

@RdfsClass('sp:ProtocolDescription')
export class ProtocolDescription {

  @RdfId
  public id: string;

  public edit: boolean;

  @RdfProperty('http://www.w3.org/2000/01/rdf-schema#label')
  public label: string;

  @RdfProperty('http://www.w3.org/2000/01/rdf-schema#description')
  public description: string;

  @RdfProperty('sp:iconUrl')
  public iconUrl: string;

  @RdfProperty('sp:hasUri')
  public uri: string;

  @RdfProperty('sp:sourceType')
  public sourceType: string;

  @RdfProperty('sp:config')
  public config: StaticProperty[] = [];

  constructor(id: string) {
    this.id = id;
    this.edit = false;
  }

}
