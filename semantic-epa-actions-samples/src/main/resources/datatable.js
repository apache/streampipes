var client;

var t;

	function connectStomp(brokerUrl, inputTopic, numberOfRows, columnNames) {
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
				
				var j = jQuery.parseJSON(message.body);
				console.log(message.body);
				if ($("#liveTable tr").length > numberOfRows)
					{
						$("#liveTable tr").last().remove();
					}

				var str = "<tr>";
				$(columnNames).each(function(index, value) {
					str = str +"<td>" +j[value] +"</td>";
				});
				str += "</tr>";
				$("#liveTable tbody").prepend(str);
			});			
		};
		
		client.connect(login, passcode, onconnect);
		
		var error_callbck = function(error) {
		client.connect(login, passcode, onconnect);
		};
	}	


	function buildTable(brokerUrl, inputTopic, numberOfRows, columnNames) {
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
		
			connectStomp(brokerUrl, inputTopic, numberOfRows, columnNames);
	}
	