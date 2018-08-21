import {RdfId} from '../../tsonld/RdfId';
import {RdfProperty} from '../../tsonld/RdfsProperty';
import {RdfsClass} from '../../tsonld/RdfsClass';
import {TransformationRuleDescription} from './rules/TransformationRuleDescription';

@RdfsClass('sp:AdapterDescription')
export class AdapterDescription {

  @RdfId
  public id: string;

  @RdfProperty('http://www.w3.org/2000/01/rdf-schema#label')
  public label: string;

  @RdfProperty('http://www.w3.org/2000/01/rdf-schema#description')
  public description: string;

  @RdfProperty('sp:couchDBId')
  public couchDbId: string;

  @RdfProperty('sp:userName')
  public userName: string;

  @RdfProperty('sp:rules')
  public rules: TransformationRuleDescription[];

  constructor(id: string) {
    this.id = id;
  }

}
