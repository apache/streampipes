import { SocketConnectionDataModel } from '../../socket-connection-data-model.service.js'

export class HtmlDataModel extends SocketConnectionDataModel {

    constructor($http, id) {
        super($http, id);
    }

    newData(message) {
        this.updateScope(message);
    }

};

HtmlDataModel.$inject = ['$http'];
