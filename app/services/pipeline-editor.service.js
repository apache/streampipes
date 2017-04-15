pipelineEditorService.$inject = ['$rootScope', 'jsplumbService', 'apiConstants'];

export default function pipelineEditorService($rootScope, jsplumbService, apiConstants) {

    var pipelineEditorService = {};

    pipelineEditorService.getCoordinates = function(ui, currentZoomLevel) {

        var newLeft = getDropPositionX(ui.helper, currentZoomLevel);
        var newTop = getDropPositionY(ui.helper, currentZoomLevel);
        return {
            'x': newLeft,
            'y': newTop
        };
    }

    pipelineEditorService.isConnected = function(element, jsplumb) {
        if (jsplumb.getConnections({source: element}).length < 1 && jsplumb.getConnections({target: element}).length < 1) {
            return false;
        }
        return true;
    }

    pipelineEditorService.isFullyConnected = function(element, jsplumb) {
        return $(element).data("JSON").inputStreams == null || jsplumb.getConnections({target: $(element)}).length == $(element).data("JSON").inputStreams.length;
    }

    var getDropPositionY = function(helper, currentZoomLevel) {
        var newTop;
        var helperPos = helper.offset();
        var divPos = $('#assembly').offset();
        newTop = (helperPos.top - divPos.top) + (1 - currentZoomLevel) * ((helperPos.top - divPos.top) * 2);
        return newTop;
    }

    var getDropPositionX = function(helper, currentZoomLevel) {
        var newLeft;
        var helperPos = helper.offset();
        var divPos = $('#assembly').offset();
        newLeft = (helperPos.left - divPos.left) + (1 - currentZoomLevel) * ((helperPos.left - divPos.left) * 2);
        return newLeft;
    }

    return pipelineEditorService;
}