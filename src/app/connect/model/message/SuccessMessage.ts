import {RdfsClass} from '../../../platform-services/tsonld/RdfsClass';
import {MessageLd} from './MessageLd';

@RdfsClass("sp:SuccessMessage")
export class SuccessMessageLd extends MessageLd {


    constructor(id: string) {
        super(id);
        this.success = true;
    }

}