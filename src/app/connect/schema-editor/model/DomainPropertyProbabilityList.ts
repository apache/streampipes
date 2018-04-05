import {RdfsClass} from '../../tsonld/RdfsClass';
import {RdfProperty} from '../../tsonld/RdfsProperty';
import {RdfId} from '../../tsonld/RdfId';
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