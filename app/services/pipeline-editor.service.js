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