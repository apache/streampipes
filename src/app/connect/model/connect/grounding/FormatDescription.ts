import {RdfId} from '../../../../platform-services/tsonld/RdfId';
import {RdfProperty} from '../../../../platform-services/tsonld/RdfsProperty';
import {RdfsClass} from '../../../../platform-services/tsonld/RdfsClass';
import {StaticProperty} from '../../StaticProperty';

@RdfsClass('sp:FormatDescription')
export class FormatDescription {

  public edit: boolean;

  @RdfId
  public id: string;

  @RdfProperty('http://www.w3.org/2000/01/rdf-schema#label')
  public label: string;

  @RdfProperty('sp:hasAppId')
  public appId: string;

  @RdfProperty('http://www.w3.org/2000/01/rdf-schema#description')
  public description: string;

  @RdfProperty('sp:hasUri')
  public uri: string;

  @RdfProperty('sp:config')
  public config: StaticProperty[] = [];

  constructor(id: string) {
    this.id = id;
    this.edit = false;
  }
}
