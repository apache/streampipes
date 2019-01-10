import {RdfsClass} from '../../../tsonld/RdfsClass';
import {RdfId} from '../../../tsonld/RdfId';
import {RdfProperty} from '../../../tsonld/RdfsProperty';
import {TransformationRuleDescription} from './TransformationRuleDescription';

@RdfsClass('sp:RemoveDuplicatesRuleDescription')
export class RemoveDuplicatesRuleDescription extends TransformationRuleDescription {

    @RdfId
    public id: string;

    @RdfProperty('sp:filterTimeWindow')
    public filterTimeWindow: number;

    constructor(filterTimeWindow) {
        super();
        this.id = "http://streampipes.org/transformation_rule/" + Math.floor(Math.random() * 10000000) + 1;
        this.filterTimeWindow = filterTimeWindow;
    }

}