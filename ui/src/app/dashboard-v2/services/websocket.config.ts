import {InjectableRxStompConfig} from "@stomp/ng2-stompjs";
import {WebsocketSettings} from "./websocket.settings";

export const streamPipesStompConfig: InjectableRxStompConfig = {

    brokerURL: new WebsocketSettings().getBrokerUrl(),

    connectHeaders: {
        login: 'admin',
        passcode: 'admin'
    },

    heartbeatIncoming: 0,
    heartbeatOutgoing: 20000,

    reconnectDelay: 200,

};