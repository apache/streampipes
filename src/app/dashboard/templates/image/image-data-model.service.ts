import { SocketConnectionDataModel } from '../../socket-connection-data-model.service.js'

export class ImageDataModel extends SocketConnectionDataModel {

    constructor($http, id) {
        super($http, id);
    }

    newData(message) {
        this.updateScope(message);
    }

};

ImageDataModel.$inject = ['$http'];
