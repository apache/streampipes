import {RdfsClass} from '../../../platform-services/tsonld/RdfsClass';
import { AdapterSetDescription } from './AdapterSetDescription';

@RdfsClass('sp:SpecificAdapterSetDescription')
export class SpecificAdapterSetDescription extends AdapterSetDescription {

    constructor(id: string) {
        super(id)
        this.appId = id;
    }

}