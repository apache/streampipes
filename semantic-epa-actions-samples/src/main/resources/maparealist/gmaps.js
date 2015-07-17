var client;
var mapData;
var map;
var taxiCoordinates = 0, poly = {};



function connectStomp(brokerUrl, inputTopic, latitudeNw, longitudeNw, latitudeSe, longitudeSe, description2) {
	
	var service = new google.maps.DirectionsService();
	var path = {};
	var areas = new Array(500);
	var count = new Array();
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
				
				var obj = j["bestCells"];
				
				$(obj).each(function() {		
					var latNw = this.cellOptions1.latitudeNW;
					var lngNw = this.cellOptions1.longitudeNW;
					var latSe = this.cellOptions1.latitudeSE;
					var lngSe = this.cellOptions1.longitudeSE;
					
					var cellX = this.cellOptions1.cellX;
					var cellY = this.cellOptions1.cellY;
					
					var description = this.profitability;
					console.log(latNw);
					console.log(lngNw);
					console.log(latSe);
					console.log(lngSe);
					
					console.log(description);
					
					var countValue = {};
					
					if (areas[cellX] != undefined)
						{
							console.log(areas[cellX][cellY]);
							if (areas[cellX][cellY] != undefined)
								{
									areas[cellX][cellY].maps.setMap(null);
									if (areas[cellX][cellY].label != undefined) areas[cellX][cellY].label.setMap(null);
								}
						}
					else areas[cellX] = new Array(500);
						areas[cellX][cellY] = {};
						areas[cellX][cellY].maps = new google.maps.Rectangle({
							    strokeColor: '#FF0000',
							    strokeOpacity: getOpacity(description),
							    strokeWeight: 2,
							    fillColor: '#FF0000',
							    fillOpacity: getOpacity(description),
							    map: map,
							    bounds: new google.maps.LatLngBounds(
							      new google.maps.LatLng(latNw, lngNw),
							      new google.maps.LatLng(latSe, lngSe))
							  });
						countValue.cellx = cellX;
						countValue.celly = cellY;
						
						count.push(countValue);
						
						if (count.length > 300)
							{
								areas[count[0].cellx][count[0].celly].maps.setMap(null);
								if (areas[cellX][cellY].label != undefined) areas[count[0].cellx][count[0].celly].label.setMap(null);
								count.shift();
							}
						
						
						areas[cellX][cellY].label = new MapLabel({
					          text: Math.round(description * 100) / 100,
					          position: new google.maps.LatLng(latNw,lngNw),
					          map: map,
					          fontSize: 8,
					          align: 'left'
					        });
						
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
	 var script2 = document.createElement('script');
	  script2.type = 'text/javascript';
	  script2.src = 'http://google-maps-utility-library-v3.googlecode.com/svn/trunk/maplabel/src/maplabel-compiled.js';
	  document.body.appendChild(script2);
	
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

function getOpacity(description) {
	return 0.2*description;
}

function getRandomColor() {
    var letters = '0123456789ABCDEF'.split('');
    var color = '#';
    for (var i = 0; i < 6; i++ ) {
        color += letters[Math.floor(Math.random() * 16)];
    }
    return color;
}

//Define the overlay, derived from google.maps.OverlayView
function Label(opt_options) {
 // Initialization
 this.setValues(opt_options);

 // Label specific
 var span = this.span_ = document.createElement('span');
 span.style.cssText = 'position: relative; left: -50%; top: -8px; ' +
                      'white-space: nowrap; border: 1px solid blue; ' +
                      'padding: 2px; background-color: white';

 var div = this.div_ = document.createElement('div');
 div.appendChild(span);
 div.style.cssText = 'position: absolute; display: none';
};
Label.prototype = new google.maps.OverlayView;

// Implement onAdd
Label.prototype.onAdd = function() {
 var pane = this.getPanes().overlayLayer;
 pane.appendChild(this.div_);

 // Ensures the label is redrawn if the text or position is changed.
 var me = this;
 this.listeners_ = [
   google.maps.event.addListener(this, 'position_changed',
       function() { me.draw(); }),
   google.maps.event.addListener(this, 'text_changed',
       function() { me.draw(); })
 ];
};

// Implement onRemove
Label.prototype.onRemove = function() {
 this.div_.parentNode.removeChild(this.div_);

 // Label is removed from the map, stop updating its position/text.
 for (var i = 0, I = this.listeners_.length; i < I; ++i) {
   google.maps.event.removeListener(this.listeners_[i]);
 }
};

// Implement draw
Label.prototype.draw = function() {
 var projection = this.getProjection();
 var position = projection.fromLatLngToDivPixel(this.get('position'));

 var div = this.div_;
 div.style.left = position.x + 'px';
 div.style.top = position.y + 'px';
 div.style.display = 'block';

 this.span_.innerHTML = this.get('text').toString();
};
