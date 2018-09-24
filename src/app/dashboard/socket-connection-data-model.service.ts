import { WidgetDataModel} from "./widget-data-model.service";
import * as _ from 'lodash';

declare const Stomp: any;

export class SocketConnectionDataModel extends WidgetDataModel {

	$http: any;
	visualisationId: any;
	client: any;
	WidgetDataModel: any;

	constructor($http, visualisationId) {
        super();
	    this.$http = $http;
        this.visualisationId = visualisationId;
        this.client = {};
	}

	init() {
		
		//TODO find better solution
		var login = 'admin';
		var passcode = 'admin';
		var self = this;

		this.$http.get('/dashboard/_all_docs?include_docs=true')
			.success(function(data) {

				var element = _.find(data.rows, elem => {
					return elem.doc.visualisation._id == self.visualisationId;
				});
				
				var brokerUrl = element.doc.visualisation['broker'];
				var inputTopic = '/topic/' + element.doc.visualisation['pipelineId'];

				self.client = Stomp.client(brokerUrl + inputTopic);

				// Uncomment these lines to get all the wesocket messages to the console
				//  client.debug = function (str) {
				//  	console.log(str);
				//  };

				// the client is notified when it is connected to the server.
				var onConnect = function (frame) {

					self.client.subscribe(inputTopic, function (message) {
						self.newData(JSON.parse(message.body));
					});
				};

				self.client.connect(login, passcode, onConnect);
			});
	};

	destroy() {
		if (this.WidgetDataModel) {
			this.WidgetDataModel.prototype.destroy.call(this);
        }

		this.client.disconnect(() => {
			console.log("Disconnected websocket connection");
		});
	};


	updateScope(data) {
		this.widgetScope.widgetData = data;
		this.widgetScope.$apply(() => {
		});
	}

	newData(message) {
		// to be overridden by subclasses
	}
};

SocketConnectionDataModel.$inject = ['$http'];
