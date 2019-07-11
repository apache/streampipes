import {RdfsClass} from '../../../platform-services/tsonld/RdfsClass';
import {RdfProperty} from '../../../platform-services/tsonld/RdfsProperty';
import {RdfId} from '../../../platform-services/tsonld/RdfId';
import {DomainPropertyProbability} from './DomainPropertyProbability';

@RdfsClass('sp:DomainPropertyProbabilityList')
export class DomainPropertyProbabilityList {

    @RdfId
    public id: string;

    @RdfProperty('sp:list')
    public list: Array<DomainPropertyProbability>;

    @RdfProperty('sp:runtimeName')
    public runtimeName: String;

    constructor () {
        this.list = [];
    }
}