import {RdfId} from '../tsonld/RdfId';
import {RdfsClass} from '../tsonld/RdfsClass';
import {StaticProperty} from './StaticProperty';
import {RdfProperty} from '../tsonld/RdfsProperty';

@RdfsClass('sp:URI')
export class URI extends StaticProperty {

    @RdfId
    public id: string;

    @RdfProperty('sp:tmp')
    public tmp: string;

    constructor(id: string) {
        super();
        this.id = id;
        this.tmp = "ff";
    }
}