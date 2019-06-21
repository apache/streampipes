import { RdfId } from '../../platform-services/tsonld/RdfId';
import { RdfProperty } from '../../platform-services/tsonld/RdfsProperty';
import { RdfsClass } from '../../platform-services/tsonld/RdfsClass';

@RdfsClass('sp:ApplicationLink')
export class ApplicationLink {

    @RdfId
    public id: string;

    @RdfProperty('http://www.w3.org/2000/01/rdf-schema#label')
    public name: string;

    @RdfProperty('http://www.w3.org/2000/01/rdf-schema#description')
    public description: string;

    @RdfProperty('sp:applicationUrl')
    public applicationUrl: string;

    @RdfProperty('sp:applicationDescription')
    public applicationDescription: string;

    @RdfProperty('sp:iconUrl')
    public iconUrl: string;

    @RdfProperty('sp:applicationLinkType')
    public applicationLinkType: string;


    constructor(id: string) {
        this.id = id;
    }
}
