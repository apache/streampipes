import {RdfsClass} from '../../../platform-services/tsonld/RdfsClass';
import {MessageLd} from './MessageLd';

@RdfsClass("sp:ErrorMessage")
export class ErrorMessageLd extends MessageLd {


    constructor(id: string) {
        super(id);
        this.success = false;
    }

}