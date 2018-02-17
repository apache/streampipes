imageBind.$inject = ['pipelineElementIconService'];

export default function imageBind(pipelineElementIconService) {
        return {
            restrict: 'A',
            link: function (scope, elem, attrs) {
                if (attrs.imageBind == 'block') {
                    pipelineElementIconService.addImageOrTextIcon(elem, scope.element, false, 'block');
                } else if (attrs.imageBind == 'draggable') {
                    pipelineElementIconService.addImageOrTextIcon(elem, scope.element, false, 'draggable');
                }

            }
        }
}; 
