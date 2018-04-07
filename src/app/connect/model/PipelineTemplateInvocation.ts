import { RdfId } from '../tsonld/RdfId';
import { RdfsClass } from '../tsonld/RdfsClass';
import { RdfProperty } from '../tsonld/RdfsProperty';
import { StaticProperty } from './StaticProperty';

@RdfsClass('sp:DataProcessorInvocation')
export class DataProcessorInvocation {

    @RdfId
    public id: string;

    constructor(id: string) {
        this.id = id;
    }

}