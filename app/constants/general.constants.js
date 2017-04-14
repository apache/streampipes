export default {
        url: "http://localhost",
        port: "8080",
        contextPath : "/semantic-epa-backend",
        api : "/api/v2",
		streamEndpointOptions : {
			endpoint: ["Dot", {radius:12}],
			connectorStyle: {strokeStyle: "#BDBDBD", outlineColor : "#9E9E9E", lineWidth: 5},
			connector: ["Bezier", { curviness:80 }],
			isSource: true,
			maxConnections: -1,
			anchor:"Right",
			type : "token",
			connectorOverlays: [
				["Arrow", {width: 30, length: 30, location: 0.5, id: "arrow", paintStyle: {fillStyle: "#BDBDBD",
					strokeStyle: "#9E9E9E",
					lineWidth: 2}}],
			]
		},

		sepaEndpointOptions : {
			endpoint: ["Dot", {radius:12}],
			connectorStyle: {strokeStyle: "#BDBDBD", outlineColor : "#9E9E9E", lineWidth: 5},
			connector: ["Bezier", { curviness:80 }],
			isSource: true,
			maxConnections: -1,
			anchor: "Right",
			type : "empty",
			connectorOverlays: [
				["Arrow", {width: 30, length: 30, location: 0.5, id: "arrow", paintStyle: {fillStyle: "#BDBDBD",
					strokeStyle: "#9E9E9E",
					lineWidth: 2}}],
			]
		},

		leftTargetPointOptions : {
			endpoint: ["Dot", {radius:12}],
			type : "empty",
			anchor: "Left",
			isTarget: true
		}
	};
