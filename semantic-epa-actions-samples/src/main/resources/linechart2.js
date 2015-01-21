var randomData;
var client;

function connectStomp(url, inputTopic, variableName) {
		var destination;
		
		var login = 'admin';
		var passcode = 'admin';
		client = Stomp.client(url);
		// this allows to display debug logs directly on the web page
		client.debug = function (str) {
			//console.log(str);
		};
		
		console.log(passcode);
		// the client is notified when it is connected to the server.
		var onconnect = function (frame) {
			
			console.log("connected to Stomp");			
			
			client.subscribe(inputTopic, function (message) {
				if (Math.random() < 0.5) {
				
				var j = jQuery.parseJSON(message.body);
				//console.log(message.body);
				console.log(j[variableName]);
				var point = [ (new Date()).getTime(), parseInt(j[variableName]) ];
			    var shift = randomData.data.length > 20;
			    randomData.addPoint(point, true, shift);
				}
				
			});			
		};
		
		client.connect(login, passcode, onconnect);
		
		var error_callbck = function(error) {
		client.connect(login, passcode, onconnect);
		};
	}	


function buildLineChart(chartTitle, url, inputTopic, variableName) {
connectStomp(url, inputTopic, variableName);
setTimeout(function() {
        Highcharts.setOptions({
            global: {
                useUTC: false
            }
        });

        $('#container').highcharts({
            chart: {
                type: 'spline',
                animation: Highcharts.svg, // don't animate in old IE
                marginRight: 10,
                events: {
                    load: function () {

                    	randomData = this.series[0];
                    }
                }
            },
            title: {
                text: chartTitle
            },
            xAxis: {
                type: 'datetime',
                tickPixelInterval: 150
            },
            yAxis: {
                title: {
                    text: 'Value'
                },
                plotLines: [{
                    value: 0,
                    width: 1,
                    color: '#808080'
                }]
            },
            tooltip: {
                formatter: function () {
                    return '<b>' + this.series.name + '</b><br/>' +
                        Highcharts.dateFormat('%Y-%m-%d %H:%M:%S', this.x) + '<br/>' +
                        Highcharts.numberFormat(this.y, 2);
                }
            },
            legend: {
                enabled: false
            },
            exporting: {
                enabled: false
            },
            series : [ {
                name : 'Data',
                  data : [ ]
                } ]
        });
    	}, 2000);
}