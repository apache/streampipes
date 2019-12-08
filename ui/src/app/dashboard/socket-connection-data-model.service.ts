/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

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
			.then(msg => {
				let data = msg.data;
				var element = _.find(data.rows, elem => {
					return elem.doc.visualisation._id == self.visualisationId;
				});

				var websocketScheme;
				if(location.protocol === 'https:') {
					websocketScheme = "wss:";
				} else {
					websocketScheme = "ws:";
				}
				var inputTopic = '/topic/' + element.doc.visualisation['topic'];
				var brokerUrl = websocketScheme + "//" + location.host + "/streampipes/ws";

				self.client = Stomp.client(brokerUrl + inputTopic);

				// Uncomment these lines to get all the wesocket messages to the console
				//  client.debug = function (str) {
				//  	console.log(str);
				//  };

				// the client is notified when it is connected to the server.
				var onConnect = function (frame) {

					self.client.subscribe(inputTopic, function (message) {
						self.newData(JSON.parse(message.body));
					}, {'Sec-WebSocket-Protocol': 'v10.stomp, v11.stomp'});
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
