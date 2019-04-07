import {RdfId} from '../../../platform-services/tsonld/RdfId';
import {RdfProperty} from '../../../platform-services/tsonld/RdfsProperty';
import {RdfsClass} from '../../../platform-services/tsonld/RdfsClass';
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