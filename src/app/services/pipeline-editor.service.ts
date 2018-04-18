export class PipelineEditorService {

    JsplumbBridge: any;

    constructor(JsplumbBridge) {
        this.JsplumbBridge = JsplumbBridge;
    }

    getCoordinates(ui, currentZoomLevel) {

        var newLeft = this.getDropPositionX(ui.helper, currentZoomLevel);
        var newTop = this.getDropPositionY(ui.helper, currentZoomLevel);
        return {
            'x': newLeft,
            'y': newTop
        };
    }

    isConnected(element) {
        if (this.JsplumbBridge.getConnections({source: element}).length < 1 && this.JsplumbBridge.getConnections({target: element}).length < 1) {
            return false;
        }
        return true;
    }

    isFullyConnected(element, json) {
        return json.payload.inputStreams == null || this.JsplumbBridge.getConnections({target: $(element)}).length == json.payload.inputStreams.length;
    }

    getDropPositionY(helper, currentZoomLevel) {
        var newTop;
        var helperPos = helper.offset();
        var divPos = $('#assembly').offset();
        newTop = (helperPos.top - divPos.top) + (1 - currentZoomLevel) * ((helperPos.top - divPos.top) * 2);
        return newTop;
    }

    getDropPositionX(helper, currentZoomLevel) {
        var newLeft;
        var helperPos = helper.offset();
        var divPos = $('#assembly').offset();
        newLeft = (helperPos.left - divPos.left) + (1 - currentZoomLevel) * ((helperPos.left - divPos.left) * 2);
        return newLeft;
    }

}

//PipelineEditorService.$inject = ['JsplumbBridge'];