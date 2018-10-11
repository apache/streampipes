import { RdfsClass } from '../../../tsonld/RdfsClass';
import { RdfId } from '../../../tsonld/RdfId';
import { RdfProperty } from '../../../tsonld/RdfsProperty';
import { TransformationRuleDescription } from './TransformationRuleDescription';

@RdfsClass('sp:UnitTransformRuleDescription')
export class UnitTransformRuleDescription extends TransformationRuleDescription {

    @RdfId
    public id: string;

    @RdfProperty('sp:eventPropertyId')
    public eventPropertyId: string;

    @RdfProperty('sp:fromUnit')
    public fromUnitRessourceURL: string;

    @RdfProperty('sp:toUnit')
    public toUnitRessourceURL: string;



    constructor(eventPropertyId: string, fromUnitRessourceURL: string, toUnitRessourceURL: string) {
        super();
        this.id = "http://streampipes.org/transformation_rule/" + Math.floor(Math.random() * 10000000) + 1;
        this.eventPropertyId = eventPropertyId;
        this.fromUnitRessourceURL = fromUnitRessourceURL;
        this.toUnitRessourceURL = toUnitRessourceURL;
    }

}