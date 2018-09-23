export class StatusMessage {

    public success: boolean;
    public notifications: Array<Notification>;

    constructor(success: boolean, notifications: Array<Notification>) {
        this.success = success;
        this.notifications = notifications;
    }
}
