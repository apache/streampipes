var chart;
var client;

function buildGauge(brokerUrl, inputTopic, variableName, min, max) {
	chart = $('#gaugeChart').epoch({
	    type: 'time.gauge',
	    value: 0.0,
	    fps: 10,
	    format: function(v) { return v.toFixed(2); },
	    domain: [min, max]
	  });
	
	setTimeout(function() {
	connectStomp(brokerUrl, inputTopic, variableName);
	}, 2000);	
}


function connectStomp(brokerUrl, inputTopic, variableName) {
	
	var destination;
	var login = 'admin';
	var passcode = 'admin';
	
	client = Stomp.client(brokerUrl);
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
			var propertyValue = j[variableName];

			chart.update(propertyValue);	
		});			
	};
	
	client.connect(login, passcode, onconnect);
	
	var error_callbck = function(error) {
	client.connect(login, passcode, onconnect);
	};
}	
  
  
  