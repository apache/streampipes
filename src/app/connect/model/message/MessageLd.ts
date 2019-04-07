import {RdfsClass} from '../../../platform-services/tsonld/RdfsClass';
import {RdfId} from '../../../platform-services/tsonld/RdfId';
import {RdfProperty} from '../../../platform-services/tsonld/RdfsProperty';
import {NotificationLd} from './NotificationLd';

@RdfsClass("sp:Message")
export class MessageLd {

    @RdfId
    public id: string;

    @RdfProperty('sp:messsageSuccess')
    public success:boolean;

    @RdfProperty('sp:messageElementName')
    public elementName: string;

    @RdfProperty('sp:notifications')
    public notifications: NotificationLd[] = [];

    constructor(id: string) {
        this.id = id;
    }
}