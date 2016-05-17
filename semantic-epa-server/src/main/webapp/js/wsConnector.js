function connect() {
		
		var login = 'admin';
		var passcode = 'admin';
		var url = $('#brokerUrl').val();
		var destination = '/topic/' +$('#brokerTopic').val();
		var client = Stomp.client(url);
		client.debug = function (str) {
			$("#debug").append(str + "\n");
		};
		var onconnect = function (frame) {
			client.debug("connected to Stomp");
			client.subscribe(destination, function (message) {
				var j = jQuery.parseJSON(message.body);
				$('#brokerMessages').val($('#brokerMessages').val()+JSON.stringify(j));  
				console.log(j);
			});

		};
		client.connect(login, passcode, onconnect);
}	