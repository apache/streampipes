import {RdfId} from '../tsonld/RdfId';
import {RdfProperty} from '../tsonld/RdfsProperty';
import {RdfsClass} from '../tsonld/RdfsClass';
import {StaticProperty} from './StaticProperty';

@RdfsClass('sp:MappingPropertyUnary')
export class MappingPropertyUnary extends StaticProperty {

    @RdfId
    public id: string;

    @RdfProperty('http://www.w3.org/2000/01/rdf-schema#label')
    public label: string;

    @RdfProperty('sp:internalName')
    public internalName: string;

    @RdfProperty('sp:mapsTo')
    public mapsTo: Object;

    constructor(id: string) {
        super();
        this.id = id;
    }

}
