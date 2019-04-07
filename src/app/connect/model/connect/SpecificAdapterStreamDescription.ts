import {RdfsClass} from '../../../platform-services/tsonld/RdfsClass';
import { AdapterStreamDescription } from './AdapterStreamDescription';

@RdfsClass('sp:SpecificAdapterStreamDescription')
export class SpecificAdapterStreamDescription extends AdapterStreamDescription {

    constructor(id: string) {
        super(id)
    }

}