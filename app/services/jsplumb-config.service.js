jsplumbConfigService.$inject = [];

export default function jsplumbConfigService() {

    var jsplumbConfigService = {};

    jsplumbConfigService.getEditorConfig = function () {
        return makeConfig(makeSettings(12, 5, 30, 30, 2, 80));
    }

    jsplumbConfigService.getPreviewConfig = function () {
        return makeConfig(makeSettings(6, 2, 15, 15, 1, 40));
    }

    var makeConfig = function(settings) {
        var config = {};
        config.streamEndpointOptions = makeStreamEndpointOptions(settings);
        config.sepaEndpointOptions = makeSepaEndpointOptions(settings);
        config.leftTargetPointOptions = makeLeftTargetPointOptions(settings);
        return config;
    }

    var makeSettings = function(dotRadius, lineWidth, arrowWidth, arrowLength, arrowLineWidth, curviness) {
        var settings = {};
        settings.dotRadius = dotRadius;
        settings.lineWidth = lineWidth;
        settings.arrowWidth = arrowWidth;
        settings.arrowLength = arrowLength;
        settings.arrowLineWidth = arrowLineWidth;
        settings.curviness = curviness;
        return settings;
    }

    var makeStreamEndpointOptions = function (settings) {
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

    var makeSepaEndpointOptions = function (settings) {
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

    var makeLeftTargetPointOptions = function (settings) {
        return {
            endpoint: ["Dot", {radius: settings.dotRadius}],
            type: "empty",
            anchor: "Left",
            isTarget: true
        }
    }

    return jsplumbConfigService;
}