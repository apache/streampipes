import {RdfsClass} from '../../platform-services/tsonld/RdfsClass';
import {StaticProperty} from './StaticProperty';
import {RdfProperty} from '../../platform-services/tsonld/RdfsProperty';

@RdfsClass('sp:StaticPropertyAlternative')
export class AlternativeStaticProperty extends StaticProperty {

    @RdfProperty('sp:isSelected')
    private selected: boolean;

    @RdfProperty('sp:hasStaticProperty')
    private staticProperty: StaticProperty;

    constructor(id: string) {
        super();
        this.id = id;
    }

}