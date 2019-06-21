import {RdfsClass} from '../../../platform-services/tsonld/RdfsClass';
import {RdfId} from '../../../platform-services/tsonld/RdfId';
import {RdfProperty} from '../../../platform-services/tsonld/RdfsProperty';

@RdfsClass("sp:Notification")
export class NotificationLd {

    @RdfId
    public id: string;

    @RdfProperty('sp:notificationTitle')
    public title: string;

    @RdfProperty('sp:notificationDescription')
    public description: string;

    @RdfProperty('sp:notificationAdditionalInformation')
    public additionalInformation: string;

    constructor(id: string) {
        this.id = id;
    }
}