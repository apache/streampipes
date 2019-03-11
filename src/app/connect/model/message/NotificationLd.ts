import {RdfsClass} from '../../tsonld/RdfsClass';
import {RdfId} from '../../tsonld/RdfId';
import {RdfProperty} from '../../tsonld/RdfsProperty';

@RdfsClass("sp:notification")
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