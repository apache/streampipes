export class WebsocketSettings {

    getBrokerUrl(): string {
        return this.getWebsocketScheme() + "//" + location.host + "/streampipes/ws";
    }

    getWebsocketScheme(): string {
        if (location.protocol === 'https:') {
            return "wss:";
        } else {
            return "ws:";
        }
    }
}