import { RdfId } from '../tsonld/RdfId';
import { RdfProperty } from '../tsonld/RdfsProperty';
import { RdfsClass } from '../tsonld/RdfsClass';
import { InvocableStreamPipesEntity } from "./InvocableStreamPipesEntity";

@RdfsClass('sp:DataSinkInvocation')
export class DataSinkInvocation {

    @RdfId
    public id: string;

    @RdfProperty('http://www.w3.org/2000/01/rdf-schema#label')
    public label: string;

    constructor(id: string) {
        //super(id);
        this.id = id;
    }

}
