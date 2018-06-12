import { RdfId } from '../tsonld/RdfId';
import { RdfProperty } from '../tsonld/RdfsProperty';
import { RdfsClass } from '../tsonld/RdfsClass';
import { ApplicationLink } from "./ApplicationLink";

@RdfsClass('sp:StaticProperty')
export class NamedStreamPipesEntity {

    @RdfId
    @RdfProperty('sp:hasUri')
    public uri: string;

    @RdfProperty('http://www.w3.org/2000/01/rdf-schema#label')
    public name: string;

    @RdfProperty('http://www.w3.org/2000/01/rdf-schema#description')
    public description: string;

    @RdfProperty('sp:iconUrl')
    public iconUrl: string;

    @RdfProperty('sp:hasApplicationLink')
    public applicationLinks: Array<ApplicationLink>;

    constructor(uri: string) {
      this.uri = uri;
    }
}
