import { SocketConnectionDataModel } from '../../socket-connection-data-model.service.js'


export class MapDataModel extends SocketConnectionDataModel {

    constructor($http, id) {
        super($http, id);
    }

    newData(message) {
        this.updateScope(message);
    }

};

MapDataModel.$inject = ['$http'];
