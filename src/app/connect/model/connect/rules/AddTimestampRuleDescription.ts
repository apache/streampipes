import {RdfsClass} from '../../../tsonld/RdfsClass';
import {RdfId} from '../../../tsonld/RdfId';
import {RdfProperty} from '../../../tsonld/RdfsProperty';
import {TransformationRuleDescription} from './TransformationRuleDescription';

@RdfsClass('sp:AddTimestampRuleDescription')
export class AddTimestampRuleDescription extends TransformationRuleDescription {

    @RdfId
    public id: string;

    @RdfProperty('sp:runtimeKey')
    public runtimeKey: string;

    constructor(runtimeKey) {
        super();
        this.id = "http://streampipes.org/transformation_rule/" + Math.floor(Math.random() * 10000000) + 1;
        this.runtimeKey = runtimeKey;
    }

}