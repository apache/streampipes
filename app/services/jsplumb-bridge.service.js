export class JsplumbBridge {

    constructor($timeout) {
        this.$timeout = $timeout;
    }

    setZoom(scale) {
        jsPlumb.setZoom(scale);
    }

    repaintEverything() {
        jsPlumb.repaintEverything(true);
    }

    deleteEveryEndpoint() {
        jsPlumb.deleteEveryEndpoint();
    }

    setContainer(container) {
        jsPlumb.setContainer(container);
    }

    unbind(element) {
        jsPlumb.unbind(element);
    }

    bind(event, fn) {
        return jsPlumb.bind(event, fn);
    }

    selectEndpoints() {
        return jsPlumb.selectEndpoints();
    }
    
    selectEndpoints(endpoint) {
        return jsPlumb.selectEndpoints(endpoint);
    }
    
    detach(connection) {
        jsPlumb.detach(connection);
    }

    getConnections(filter) {
        return jsPlumb.getConnections(filter);
    }
    
    addEndpoint(element, options) {
        console.log("adding endpoint");
        this.$timeout(() => {
            var test = jsPlumb.addEndpoint(element, options);
            console.log(jsPlumb.selectEndpoints());
            console.log(test);
        });
        this.repaintEverything();
    }
    
    connect(connection) {
        jsPlumb.connect(connection);
    }

    removeAllEndpoints(element) {
        jsPlumb.removeAllEndpoints(element);
    }

    registerEndpointTypes(typeInfo) {
        jsPlumb.registerEndpointTypes(typeInfo);
    }

    draggable(element, option) {
        jsPlumb.draggable(element, option);
    }
    
    setSuspendDrawing(bool1, bool2) {
        jsPlumb.setSuspendDrawing(bool1, bool2);
    }

    setSuspendDrawing(bool1) {
        jsPlumb.setSuspendDrawing(bool1);
    }
}

JsplumbBridge.$inject=['$timeout'];