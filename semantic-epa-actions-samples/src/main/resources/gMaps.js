var client;
var mapData;
var map;

function connectStomp(url, inputTopic, latitude, longitude, description) {
		var destination;
		
		var login = 'admin';
		var passcode = 'admin';
		client = Stomp.client(url);
		// this allows to display debug logs directly on the web page
		client.debug = function (str) {
			console.log(str);
		};
		
		console.log(passcode);
		// the client is notified when it is connected to the server.
		var onconnect = function (frame) {
			
			console.log("connected to Stomp");			
			
			client.subscribe(inputTopic, function (message) {
				
				var j = jQuery.parseJSON(message.body);
				//console.log(message.body);
				var latitude = j[latitude];
				var longitude = j[longitude];
				var description = j[description];
				var myLatlng = new google.maps.LatLng(latitude, longitude);
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


function buildGoogleMap(url, inputTopic, latitude, longitude, description) {
	ready();
	connectStomp(url, inputTopic, latitude, longitude, description);
	
}
function initialize() {
	  var mapOptions = {
	    zoom: 8,
	    center: new google.maps.LatLng(-34.397, 150.644)
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