import {RdfsClass} from '../../../tsonld/RdfsClass';
import {RdfId} from '../../../tsonld/RdfId';
import {RdfProperty} from '../../../tsonld/RdfsProperty';
import {TransformationRuleDescription} from './TransformationRuleDescription';

@RdfsClass('sp:AddValueTransformationRuleDescription')
export class AddValueTransformationRuleDescription extends TransformationRuleDescription {

    @RdfId
    public id: string;

    @RdfProperty('sp:runtimeKey')
    public runtimeKey: string;

    @RdfProperty('sp:staticValue')
    public staticValue: string;

    constructor(runtimeKey, staticValue) {
        super();
        this.id = "http://streampipes.org/transformation_rule/" + Math.floor(Math.random() * 10000000) + 1;
        this.runtimeKey = runtimeKey;
        this.staticValue = staticValue;
    }

}