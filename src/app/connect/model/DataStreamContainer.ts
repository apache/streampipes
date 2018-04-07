import {RdfId} from '../tsonld/RdfId';
import {RdfProperty} from '../tsonld/RdfsProperty';
import {RdfsClass} from '../tsonld/RdfsClass';
import { DataSetDescription } from './DataSetDescription';

@RdfsClass('sp:DataStreamContainer')
export class DataSetContainer {

    @RdfId
    public id: string;

    @RdfProperty('sp:list')
    public list: DataSetDescription[] = [];


    constructor(id: string) {
        this.id = id;
    }

}