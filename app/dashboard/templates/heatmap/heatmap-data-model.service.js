import SocketConnectionDataModel from '../../socket-connection-data-model.service.js'


export class HeatmapDataModel extends SocketConnectionDataModel {

    constructor($http, id) {
        super($http, id);
    }

    newData(message) {
        this.updateScope(message);
    }
    
};

HeatmapDataModel.$inject = ['$http'];
