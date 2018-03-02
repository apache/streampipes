import SocketConnectionDataModel from '../../socket-connection-data-model.service.js'

export class TrafficlightDataModelextends extends SocketConnectionDataModel {

    constructor($http, id) {
        super($http, id);
    }

    newData(message) {
        this.updateScope(message);
    }

};

TrafficlightDataModel.$inject = ['$http'];
