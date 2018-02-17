declare const jsPlumb: any;

export class JsplumbBridge {

    $timeout: any;

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

    // TODO: Overloading Functions?
    selectEndpoints(endpoint?) {
        if(endpoint === undefined) {
            return jsPlumb.selectEndpoints();
        }
        return jsPlumb.selectEndpoints(endpoint);
    }

    detach(connection) {
        jsPlumb.detach(connection);
    }

    getConnections(filter) {
        return jsPlumb.getConnections(filter);
    }

    addEndpoint(element, options) {
        jsPlumb.addEndpoint(element, options);
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

    // TODO: Overloading Functions?
    setSuspendDrawing(bool1, bool2?) {
        if(bool2 === undefined) {
            jsPlumb.setSuspendDrawing(bool1);
        } else {
            jsPlumb.setSuspendDrawing(bool1, bool2);
        }
    }
}

//JsplumbBridge.$inject = ['$timeout'];