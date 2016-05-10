var client;
var mapData;
var map;
var taxiCoordinates = 0, poly = {};



function connectStomp(brokerUrl, inputTopic, latitude, longitude, description2) {
	
	var service = new google.maps.DirectionsService();
	var path = {};
	
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
				var description = j["medallion"];
				var myLatLng = new google.maps.LatLng(lat, lng);
				console.log("desc " +description);
				//if (path.getLength() === 0) 
				if (!path.hasOwnProperty(description))
					{
						console.log("Setting object" +description);
						path[description] = new google.maps.MVCArray();
					 	path[description].push(myLatLng);
					 	poly[description] = new google.maps.Polyline({ map: map, strokeColor: getRandomColor() });
					 	poly[description].setPath(path[description]);
					}
				else
					{
					service.route({
				        origin:  path[description].getAt(path[description].getLength() - 1),
				        destination: myLatLng,
				        travelMode: google.maps.DirectionsTravelMode.DRIVING
				      }, function(result, status) {
				        if (status == google.maps.DirectionsStatus.OK) {
				          for (var i = 0, len = result.routes[0].overview_path.length;
				              i < len; i++) {
				            path[description].push(result.routes[0].overview_path[i]);
				          }
				        }
				        
				      });
					}						
				//map.panTo(myLatLng);
				
				
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

function getRandomColor() {
    var letters = '0123456789ABCDEF'.split('');
    var color = '#';
    for (var i = 0; i < 6; i++ ) {
        color += letters[Math.floor(Math.random() * 16)];
    }
    return color;
}