import { SocketConnectionDataModel } from '../../socket-connection-data-model.service.js'

export class GaugeDataModel extends SocketConnectionDataModel {

	constructor($http, id) {
        super($http, id);
    }

	newData(message) {
		this.updateScope(message);
	}

};

GaugeDataModel.$inject = ['$http'];
