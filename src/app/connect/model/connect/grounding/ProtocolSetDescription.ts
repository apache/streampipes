import {RdfsClass} from '../../../tsonld/RdfsClass';
import {ProtocolDescription} from './ProtocolDescription';

@RdfsClass('sp:ProtocolSetDescription')
export class ProtocolSetDescription extends ProtocolDescription {

    constructor(id: string) {
        super(id);
    }

}