import { SocketConnectionDataModel } from '../../socket-connection-data-model.service';

export class NumberDataModel extends SocketConnectionDataModel {

    constructor($http, id) {
        super($http, id);
    }

	newData(message) {
		this.updateScope(message);
	}

};

NumberDataModel.$inject = ['$http'];
