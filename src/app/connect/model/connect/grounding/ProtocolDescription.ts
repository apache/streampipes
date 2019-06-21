import {RdfId} from '../../../../platform-services/tsonld/RdfId';
import {RdfProperty} from '../../../../platform-services/tsonld/RdfsProperty';
import {RdfsClass} from '../../../../platform-services/tsonld/RdfsClass';
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

  @RdfProperty('sp:hasAppId')
  public appId: string;

  @RdfProperty('sp:iconUrl')
  public iconUrl: string;

  @RdfProperty('sp:hasUri')
  public uri: string;

  @RdfProperty('sp:sourceType')
  public sourceType: string;

  @RdfProperty('sp:config')
  public config: StaticProperty[] = [];

  @RdfProperty('sp:hasAdapterType')
  public category: string[] = [];

  constructor(id: string) {
    this.id = id;
    this.edit = false;
  }

}
