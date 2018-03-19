import { SocketConnectionDataModel } from '../../socket-connection-data-model.service'

export class TableDataModel extends SocketConnectionDataModel {

	dataArray: any;
	dataArrayLength: any;

    constructor($http, id) {
        super($http, id);
        this.dataArray = [];
        this.dataArrayLength = 5;
    }

	newData(message) {
		if (this.dataArray.length > this.dataArrayLength - 1) {
			this.dataArray = this.dataArray.slice(Math.max(this.dataArray.length - this.dataArrayLength , 1));
		}

		this.dataArray.push(message);
		this.updateScope(this.dataArray);
	
	}
};

TableDataModel.$inject = ['$http'];
