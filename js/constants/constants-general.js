 angular
    .module('streamPipesApp')
    .constant("apiConstants", {
        url: "http://localhost",
        port: "8080",
        contextPath : "/semantic-epa-backend",
        api : "/api/v2",
		streamEndpointOptions : {
			endpoint: ["Dot", {radius:12}],
			connectorStyle: {strokeStyle: "#BDBDBD", outlineColor : "#9E9E9E", lineWidth: 5},
			connector: "Straight",
			isSource: true,
			anchor:"Right",
			type : "token",
			connectorOverlays: [
				["Arrow", {width: 20, length: 10, location: 0.5, id: "arrow"}],
			]
		},

		sepaEndpointOptions : {
			endpoint: ["Dot", {radius:12}],
			connectorStyle: {strokeStyle: "#BDBDBD", outlineColor : "#9E9E9E", lineWidth: 5},
			connector: "Straight",
			isSource: true,
			anchor: "Right",
			type : "empty",
			connectorOverlays: [
				["Arrow", {width: 25, length: 20, location: 0.5, id: "arrow"}],
			]
		},

		leftTargetPointOptions : {
			endpoint: ["Dot", {radius:12}],
			type : "empty",
			anchor: "Left",
			isTarget: true
		}
	}).filter('startsWithLetter', function () {
	    return function (items, fromLetter, toLetter) {
	        var filtered = [];
	        for (var i = 0; i < items.length; i++) {
	            var item = items[i];
	            var firstLetter = item.name.substring(0, 1).toLowerCase();
	            if ((!fromLetter || firstLetter >= fromLetter)
	                && (!toLetter || firstLetter <= toLetter)) {
	                filtered.push(item);
	            }
	        }
	        return filtered;
	    };
	});