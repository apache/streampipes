import {RdfId} from '../../platform-services/tsonld/RdfId';
import {RdfProperty} from '../../platform-services/tsonld/RdfsProperty';
import {RdfsClass} from '../../platform-services/tsonld/RdfsClass';
import {StaticProperty} from './StaticProperty';

@RdfsClass('sp:MappingPropertyUnary')
export class MappingPropertyUnary extends StaticProperty {

    @RdfId
    public id: string;

    @RdfProperty('sp:elementName')
    public elementName: string;

    @RdfProperty('http://www.w3.org/2000/01/rdf-schema#label')
    public label: string;

    @RdfProperty('http://www.w3.org/2000/01/rdf-schema#description')
    public description: string;

    @RdfProperty('sp:internalName')
    public internalName: string;

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
