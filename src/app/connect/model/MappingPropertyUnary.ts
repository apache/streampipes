import {RdfProperty} from '../../platform-services/tsonld/RdfsProperty';
import {RdfsClass} from '../../platform-services/tsonld/RdfsClass';
import {StaticProperty} from './StaticProperty';

@RdfsClass('sp:MappingPropertyUnary')
export class MappingPropertyUnary extends StaticProperty {

    @RdfProperty('sp:mapsFrom')
    public requirementSelector: string;

    @RdfProperty('sp:mapsFromOptions')
    public mapsFromOptions: string[];

    @RdfProperty('sp:hasPropertyScope')
    public propertyScope: string;

    @RdfProperty('sp:mapsTo')
    public selectedProperties: string[];

    constructor(id: string) {
        super();
        this.id = id;
    }

}
