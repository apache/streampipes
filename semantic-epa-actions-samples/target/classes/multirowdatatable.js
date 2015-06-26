var client;
var t;

	function connectStomp(brokerUrl, inputTopic, replace, listProperty, columnNames) {
		var destination;
		
		var login = 'admin';
		var passcode = 'admin';
		client = Stomp.client(brokerUrl);
		// this allows to display debug logs directly on the web page
		client.debug = function (str) {
			console.log(str);
		};
		
		console.log(passcode);
		console.log(brokerUrl);
		// the client is notified when it is connected to the server.
		var onconnect = function (frame) {
			
			console.log("connected to Stomp");			
			console.log("destination: " +inputTopic);
			client.subscribe(inputTopic, function (message) {
				var str = "";
				var j = jQuery.parseJSON(message.body);
				console.log(message.body);
				var obj = j[listProperty];
				console.log(obj);
				$(j.list).each(function(index, v) {
					str += "<tr>";
					$(columnNames).each(function(index, value) {
						str = str +"<td>" +v[value] +"</td>";
					});
					str += "</tr>";
					
				});		
				$("#liveTable tbody").html(str);
			});
				
		};
		
		client.connect(login, passcode, onconnect);
		
		var error_callbck = function(error) {
		client.connect(login, passcode, onconnect);
		};
	}	


	function buildTable(brokerUrl, inputTopic, replace, listProperty, columnNames) {
		setTimeout(function() {
			var table = $("<table id='liveTable' cellspacing='0' width='100%' class='table table-striped'>").appendTo($('#container'));
			var header = "<thead><tr>";
			
			$(columnNames).each(function(index, value) {
				header = header + "<th>" +value +"</th>";
			});
			header += "</tr></thead><tbody>";
			table.append(header);
			table.append("</tbody></table>")
			
			}, 2000);
		
			connectStomp(brokerUrl, inputTopic, replace, listProperty, columnNames);
	}
	