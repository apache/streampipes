var chart;
var client;

var barData = [
            {label: 'A', values: [{time: 1370044800, y: 20}, {time: 1370044801, y: 20}]},
            {label: 'B', values: [{time: 1370044800, y: 20}, {time: 1370044801, y: 20}]},
            {label: 'C', values: [{time: 1370044800, y: 20}, {time: 1370044801, y: 20}]},
            {label: 'D', values: [{time: 1370044800, y: 20}, {time: 1370044801, y: 20}]},
            {label: 'E', values: [{time: 1370044800, y: 20}, {time: 1370044801, y: 20}]}
        ],
        length = 40;

var nextTime = (function() {
    var currentTime = parseInt(new Date().getTime() / 1000);
    return function() { return currentTime++; }
})();

function buildBarChart(brokerUrl, inputTopic, listPropertyName, key, value) {
	 chart = $('#container').epoch({
	        type: 'time.bar',
	        data: barData,
	        axes: ['bottom', 'left'],
	        fps:24
	    }); 
	
	setTimeout(function() {
	connectStomp(brokerUrl, inputTopic, listPropertyName, key, value);
	}, 2000);	
}


function connectStomp(brokerUrl, inputTopic, listPropertyName, key, value) {

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
			//console.log(message.body);
			var marray = j["rows"];
			//var key = marray[0].key;
			var dataObj = new Array();
			var currentTime = nextTime();
			
			$.each(marray, function(index,element) {
				var dobj = {};
			  dobj.time = currentTime;
			  dobj.y = element.value;
			  if (barData[index] == undefined) 
				  {
				  	barData[index] = {};
				  	barData[index].values = [];
				  }
			  barData[index].values.push(dobj);
			  barData[index].label = element.key;
			}); 
			if (marray.length < 3)
				{
					for(var i = 0; i < (3-marray.length);i++)
						{
						  var dobj = {};
						  dobj.time = currentTime;
						  dobj.y = 0;
						  barData[2].values.push(dobj);
						}
				}
			//chart.push(newData);	
			chart.update(barData);
		});			
	};
	
	client.connect(login, passcode, onconnect);
	
	var error_callbck = function(error) {
	client.connect(login, passcode, onconnect);
	};
}	
  
  
  