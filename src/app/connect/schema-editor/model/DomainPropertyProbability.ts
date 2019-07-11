import {RdfsClass} from '../../../platform-services/tsonld/RdfsClass';
import {RdfProperty} from '../../../platform-services/tsonld/RdfsProperty';
import {RdfId} from '../../../platform-services/tsonld/RdfId';

@RdfsClass('sp:DomainPropertyProbability')
export class DomainPropertyProbability {

    private static serialVersionUID = -3994041794693686406;

    @RdfId
    public id: string;

    @RdfProperty('sp:domainProperty')
    public domainProperty: String;


    @RdfProperty('sp:probability')
    public probability: String;

    constructor () {
    }
}