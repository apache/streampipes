var client;
var mapData;
var map;
var taxiCoordinates = 0, poly = {};



function connectStomp(brokerUrl, inputTopic, latitudeNw, longitudeNw, latitudeSe, longitudeSe, description2) {
	
	var service = new google.maps.DirectionsService();
	var path = {};
	var areas = new Array(500);
	var testi = 0;
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
				var latNw = j.cellOptions[latitudeNw];
				var lngNw = j.cellOptions[longitudeNw];
				var latSe = j.cellOptions[latitudeSe];
				var lngSe = j.cellOptions[longitudeSe];
				
				var cellX = j.cellOptions.cellX;
				var cellY = j.cellOptions.cellY;
				
				var description = j[description2];
				console.log(latNw);
				console.log(lngNw);
				console.log(latSe);
				console.log(lngSe);
				
				if (areas[cellX] != undefined)
					{
						console.log(areas[cellX][cellY]);
						if (areas[cellX][cellY] != undefined)
							areas[cellX][cellY].setMap(null);
					}
				else areas[cellX] = new Array(500);
					areas[cellX][cellY] = new google.maps.Rectangle({
						    strokeColor: '#FF0000',
						    strokeOpacity: 0.8,
						    strokeWeight: 2,
						    fillColor: '#FF0000',
						    fillOpacity: 0.35,
						    map: map,
						    bounds: new google.maps.LatLngBounds(
						      new google.maps.LatLng(latNw, lngNw),
						      new google.maps.LatLng(latSe, lngSe))
						  });
					
					var marker = new google.maps.Marker({
				          position: new google.maps.LatLng(latNw,lngNw),
				          visible: false,
				          map: map
				        });
					
					var ibOptions = {
					          content: description
					           ,boxStyle: {
					              border: "1px solid black"
					             ,background: "white"
					             ,textAlign: "center"
					             ,fontSize: "8pt"
					             ,width: "86px"  // has to be set manually
					             ,opacity: 1.0
					             ,zIndex: -100
					            }
					           ,disableAutoPan: true
					           ,pixelOffset: new google.maps.Size(-43, -25) // set manually
					           ,position: marker.getPosition()
					           ,closeBoxURL: ""
					           ,pane: "floatPane"
					           ,enableEventPropagation: true
					           ,zIndex:-1
					        };

					        var ibLabel = new InfoBox(ibOptions);
					        google.maps.event.addListener(areas[cellX][cellY],'mouseover',function() {
					            ibLabel.open(map, marker);
					          });
					          google.maps.event.addListener(areas[cellX][cellY],'mouseout',function() {
					            ibLabel.close(map, marker);
					          });
				
			});			
		};
		
		client.connect(login, passcode, onconnect);
		
		var error_callbck = function(error) {
		client.connect(login, passcode, onconnect);
		};
	}	


function buildGoogleMap(brokerUrl, inputTopic, latitudeNw, longitudeNw, latitudeSe, longitudeSe, description) {
	ready();
	setTimeout(function() {
	connectStomp(brokerUrl, inputTopic, latitudeNw, longitudeNw, latitudeSe, longitudeSe, description);
	}, 2000);	
}
function initialize() {
	  var mapOptions = {
	    zoom: 10,
	    center: new google.maps.LatLng(41.474937, -74.913585)
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