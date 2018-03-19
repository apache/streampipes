import { SocketConnectionDataModel } from '../../socket-connection-data-model.service.js'

export class TrafficLightDataModel extends SocketConnectionDataModel {

    constructor($http, id) {
        super($http, id);
    }

    newData(message) {
        this.updateScope(message);
    }

};

TrafficLightDataModel.$inject = ['$http'];
