import {Injectable} from "@angular/core";
import {Observable} from "rxjs/Observable";

declare const Stomp: any;

@Injectable()
export class WebsocketService {

    constructor() {
    }

    connect(url, topic): Observable<any> {
        return new Observable<any>(observable => {
            let client = Stomp.client(url + "/topic/" +topic);

            var onConnect = (frame => {
                client.subscribe("/topic/" +topic, function (message) {
                    observable.next(JSON.parse(message.body));
                }, {'Sec-WebSocket-Protocol': 'v10.stomp, v11.stomp'});
            });
            client.connect("admin", "admin", onConnect);
        });
    }

}