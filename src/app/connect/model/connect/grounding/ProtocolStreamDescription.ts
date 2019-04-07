import {RdfsClass} from '../../../../platform-services/tsonld/RdfsClass';
import {ProtocolDescription} from './ProtocolDescription';

@RdfsClass('sp:ProtocolStreamDescription')
export class ProtocolStreamDescription extends ProtocolDescription {

    constructor(id: string) {
        super(id);
    }

}
