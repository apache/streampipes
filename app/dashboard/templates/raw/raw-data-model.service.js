import SocketConnectionDataModel from '../../socket-connection-data-model.service.js'


export class RawDataModelextends extends SocketConnectionDataModel {

    constructor($http, id) {
        super($http, id);
        this.dataArray = [];
        this.dataArrayLength = 5;

    }



    newData(message) {
        if (this.dataArray.length > dataArrayLength - 1) {
            this.dataArray = this.dataArray.slice(Math.max(this.dataArray.length - this.dataArrayLength , 1));
        }

        this.dataArray.push(message);
        this.updateScope(dataArray);
    }

};

RawDataModel.$inject = ['$http'];
