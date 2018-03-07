import { SocketConnectionDataModel } from '../../socket-connection-data-model.service.js'

export class VerticalbarDataModel extends SocketConnectionDataModel {

    constructor($http, id) {
        super($http, id);
    }

    newData(message) {
        this.updateScope(message);
    }

};

VerticalbarDataModel.$inject = ['$http'];
