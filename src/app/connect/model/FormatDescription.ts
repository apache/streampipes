import {RdfId} from '../tsonld/RdfId';
import {RdfProperty} from '../tsonld/RdfsProperty';
import {RdfsClass} from '../tsonld/RdfsClass';
import {StaticProperty} from './StaticProperty';

@RdfsClass('sp:FormatDescription')
export class FormatDescription {

  @RdfId
  public id: string;

  @RdfProperty('http://www.w3.org/2000/01/rdf-schema#label')
  public label: string;

  @RdfProperty('http://www.w3.org/2000/01/rdf-schema#description')
  public description: string;

  @RdfProperty('sp:hasUri')
  public uri: string;

  @RdfProperty('sp:config')
  public config: StaticProperty[];

  constructor(id: string) {
    this.id = id;
  }
}
