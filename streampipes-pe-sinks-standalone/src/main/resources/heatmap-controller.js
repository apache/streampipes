var client;
var mapData;
var map;
var heatmap;
var mPoints;
var queue = new Queue();

function connectStomp(brokerUrl, inputTopic, latitude, longitude, maxPoints) {
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
		console.log(latitude);
		console.log(longitude);
		
		var counter = 0;
		// the client is notified when it is connected to the server.
		var onconnect = function (frame) {
			
			console.log("connected to Stomp");			
			console.log("destination: " +inputTopic);
			client.subscribe(inputTopic, function (message) {
				var positions = new Array();
				var j = jQuery.parseJSON(message.body);
				console.log(message.body);
				
				$.each(j, function(index, item) {
					var lat = item[latitude];
					var lng = item[longitude];
					positions.push(new OpenLayers.LonLat(lng, lat));
				});
				
				updateHeatmap(positions);
				
			});			
		};
		
		client.connect(login, passcode, onconnect);
		
		var error_callbck = function(error) {
		client.connect(login, passcode, onconnect);
		};
	}	


function buildGoogleMap(brokerUrl, inputTopic, latitude, longitude, maxPoints) {
	map = new OpenLayers.Map('container');
	map.Z_INDEX_BASE.Overlay = 200; 
	layer = new OpenLayers.Layer.OSM();
	
    heatmap = new OpenLayers.Layer.Heatmap( "Heatmap Layer", map, layer, {visible: true, radius:12}, {maxSize:800, isBaseLayer: false, opacity: 0.3, projection: new OpenLayers.Projection("EPSG:4326")});
	map.addLayers([layer, heatmap]);
	map.setCenter(
		new OpenLayers.LonLat(-73.948975, 40.680638).transform(
			new OpenLayers.Projection("EPSG:4326"),
			map.getProjectionObject()
			), 15
	); 
   map.zoomIn();
   heatmap.setDataSet(resetData());
	
	setTimeout(function() {
	connectStomp(brokerUrl, inputTopic, latitude, longitude, maxPoints);
	}, 3000);	
}

function resetData() {

	var testData={max: 20, data: [{lat: 40.680638, lon:-73.948975, count: 1}]};
	var transformedTestData = { max: testData.max , data: [] },
			data = testData.data,
			datalen = data.length,
			nudata = [];

	while(datalen--){
        nudata.push({
            lonlat: new OpenLayers.LonLat(data[datalen].lon, data[datalen].lat),
            count: data[datalen].count
        });
    }

    transformedTestData.data = nudata;
	return transformedTestData;
}

function updateHeatmap(tempVisitors)
{
	if (heatmap.getVisibility()) {
		resetData();
		for(var i = 1; i < tempVisitors.length; i++)
		{
			heatmap.addDataPoint(tempVisitors[i], 5);
		}
		runDataDecay();
	}
}

function runDataDecay()
{
          heatmap.decayDataPoints();
		  heatmap.updateLayer();
}
