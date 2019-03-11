import {RdfsClass} from '../../tsonld/RdfsClass';
import {MessageLd} from './MessageLd';

@RdfsClass("sp:successMessage")
export class SuccessMessageLd extends MessageLd {


    constructor(id: string) {
        super(id);
        this.success = true;
    }

}