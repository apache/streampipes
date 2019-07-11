import {RdfsClass} from '../../../../platform-services/tsonld/RdfsClass';
import {RdfId} from '../../../../platform-services/tsonld/RdfId';
import {RdfProperty} from '../../../../platform-services/tsonld/RdfsProperty';
import {TransformationRuleDescription} from './TransformationRuleDescription';

@RdfsClass('sp:RenameRuleDescription')
export class RenameRuleDescription extends TransformationRuleDescription {

  @RdfId
  public id: string;

  @RdfProperty('sp:oldRuntimeKey')
  public oldRuntimeKey: string;

  @RdfProperty('sp:newRuntimeKey')
  public newRuntimeKey: string;


  constructor(oldRuntimeKey: string, newRuntimeKey) {
    super();
    this.id = "http://streampipes.org/transformation_rule/" + Math.floor(Math.random() * 10000000) + 1;
    this.oldRuntimeKey = oldRuntimeKey;
    this.newRuntimeKey = newRuntimeKey;
  }

}
