export default {
        url: "http://localhost",
        port: "8080",
        contextPath : "/semantic-epa-backend",
        api : "/api/v2",
		streamEndpointOptions : {
			endpoint: ["Dot", {radius:12}],
			connectorStyle: {strokeStyle: "#BDBDBD", outlineColor : "#9E9E9E", lineWidth: 5},
			connector: "Straight",
			isSource: true,
			maxConnections: -1,
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
	};
