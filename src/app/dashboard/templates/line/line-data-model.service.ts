import { SocketConnectionDataModel } from '../../socket-connection-data-model.service'

export class LineDataModel extends SocketConnectionDataModel {

	constructor($http, id) {
        super($http, id);
    }

	newData(message) {
		this.updateScope(message);
	}
	
};

LineDataModel.$inject = ['$http'];
