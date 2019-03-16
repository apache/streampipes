import {RdfsClass} from '../../tsonld/RdfsClass';
import {RdfId} from '../../tsonld/RdfId';
import {RdfProperty} from '../../tsonld/RdfsProperty';
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