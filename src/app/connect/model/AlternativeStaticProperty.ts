import {RdfsClass} from '../../platform-services/tsonld/RdfsClass';
import {StaticProperty} from './StaticProperty';
import {RdfProperty} from '../../platform-services/tsonld/RdfsProperty';

@RdfsClass('sp:StaticPropertyAlternative')
export class AlternativeStaticProperty extends StaticProperty {

    @RdfProperty('sp:isSelected')
    public selected: boolean;

    @RdfProperty('sp:hasStaticProperty')
    public staticProperty: StaticProperty;

    constructor(id: string) {
        super();
        this.id = id;
    }

}