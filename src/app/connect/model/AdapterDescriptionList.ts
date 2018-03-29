import {RdfId} from '../tsonld/RdfId';
import {RdfProperty} from '../tsonld/RdfsProperty';
import {RdfsClass} from '../tsonld/RdfsClass';
import {AdapterDescription} from './AdapterDescription';

@RdfsClass('sp:AdapterDescriptionList')
export class AdapterDescriptionList {

    @RdfId
    public id: string;

    @RdfProperty('sp:list')
    public list: AdapterDescription[] = [];


    constructor(id: string) {
        this.id = id;
    }

}