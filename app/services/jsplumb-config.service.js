export class JsplumbConfigService {

    constructor() {
    }

    getEditorConfig() {
        return this.makeConfig(this.makeSettings(12, 5, 30, 30, 2, 80));
    }

    getPreviewConfig() {
        return this.makeConfig(this.makeSettings(6, 2, 15, 15, 1, 40));
    }

    makeConfig(settings) {
        let config = {};
        config.streamEndpointOptions = this.makeStreamEndpointOptions(settings);
        config.sepaEndpointOptions = this.makeSepaEndpointOptions(settings);
        config.leftTargetPointOptions = this.makeLeftTargetPointOptions(settings);
        return config;
    }

    makeSettings(dotRadius, lineWidth, arrowWidth, arrowLength, arrowLineWidth, curviness) {
        let settings = {};
        settings.dotRadius = dotRadius;
        settings.lineWidth = lineWidth;
        settings.arrowWidth = arrowWidth;
        settings.arrowLength = arrowLength;
        settings.arrowLineWidth = arrowLineWidth;
        settings.curviness = curviness;
        return settings;
    }

    makeStreamEndpointOptions(settings) {
        return {
            endpoint: ["Dot", {radius: settings.dotRadius}],
            connectorStyle: {strokeStyle: "#BDBDBD", outlineColor: "#9E9E9E", lineWidth: settings.lineWidth},
            connector: ["Bezier", {curviness: settings.curviness}],
            isSource: true,
            maxConnections: -1,
            anchor: "Right",
            type: "token",
            connectorOverlays: [
                ["Arrow", {
                    width: settings.arrowWidth, length: settings.arrowLength, location: 0.5, id: "arrow", paintStyle: {
                        fillStyle: "#BDBDBD",
                        strokeStyle: "#9E9E9E",
                        lineWidth: settings.arrowLineWidth
                    }
                }],
            ]
        }
    }

    makeSepaEndpointOptions(settings) {
        return {
            endpoint: ["Dot", {radius: settings.dotRadius}],
            connectorStyle: {
                strokeStyle: "#BDBDBD", outlineColor: "#9E9E9E", lineWidth: settings.lineWidth
            },
            connector: ["Bezier", {curviness: settings.curviness}],
            isSource: true,
            maxConnections: -1,
            anchor: "Right",
            type: "empty",
            connectorOverlays: [
                ["Arrow", {
                    width: settings.arrowWidth, length: settings.arrowLength, location: 0.5, id: "arrow", paintStyle: {
                        fillStyle: "#BDBDBD",
                        strokeStyle: "#9E9E9E",
                        lineWidth: settings.arrowLineWidth
                    }
                }],
            ]
        }
    }

    makeLeftTargetPointOptions(settings) {
        return {
            endpoint: ["Dot", {radius: settings.dotRadius}],
            type: "empty",
            anchor: "Left",
            isTarget: true
        }
    }

}

JsplumbConfigService.$inject = [];