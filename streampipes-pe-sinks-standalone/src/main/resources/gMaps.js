var client;
var mapData;
var map;


function connectStomp(brokerUrl, inputTopic, latitude, longitude, description) {
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
				var lat = j[latitude];
				var lng = j[longitude];
				var description = j[description];
				var myLatlng = new google.maps.LatLng(lat, lng);
				var marker = new google.maps.Marker({
				      position: myLatlng,
				      map: map,
				      title: description
				  });
				map.panTo(myLatlng);
				
				
			});			
		};
		
		client.connect(login, passcode, onconnect);
		
		var error_callbck = function(error) {
		client.connect(login, passcode, onconnect);
		};
	}	


function buildGoogleMap(brokerUrl, inputTopic, latitude, longitude, description) {
	ready();
	setTimeout(function() {
	connectStomp(brokerUrl, inputTopic, latitude, longitude, description);
	}, 2000);	
}
function initialize() {
	  var mapOptions = {
	    zoom: 10,
	    center: new google.maps.LatLng(8.40366, 49.007862)
	  };
	  map = new google.maps.Map(document.getElementById('container'),
	      mapOptions);
	}


var ready;

ready = function() {
  var script = document.createElement('script');
  script.type = 'text/javascript';
  script.src = 'https://maps.googleapis.com/maps/api/js?v=3.exp&' + 'libraries=places&'+'callback=initialize';
  document.body.appendChild(script);
};