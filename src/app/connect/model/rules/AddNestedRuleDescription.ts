import {RdfsClass} from '../../tsonld/RdfsClass';
import {RdfId} from '../../tsonld/RdfId';
import {RdfProperty} from '../../tsonld/RdfsProperty';
import {TransformationRuleDescription} from './TransformationRuleDescription';

@RdfsClass('sp:CreateNestedRuleDescription')
export class AddNestedRuleDescription extends TransformationRuleDescription {

  @RdfId
  public id: string;

  @RdfProperty('sp:runtimeKey')
  public runtimeKey: string;

  constructor(runtimeKey: string) {
    super();
    this.id = "http://streampipes.org/transformation_rule/" + Math.floor(Math.random() * 10000000) + 1;
    this.runtimeKey = runtimeKey;
  }

}
