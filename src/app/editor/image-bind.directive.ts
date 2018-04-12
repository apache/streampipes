imageBind.$inject = ['PipelineElementIconService'];

export default function imageBind(PipelineElementIconService) {
        return {
            restrict: 'A',
            link: function (scope, elem, attrs) {
                if (attrs.imageBind == 'block') {
                    PipelineElementIconService.addImageOrTextIcon(elem, scope.element, false, 'block');
                } else if (attrs.imageBind == 'draggable') {
                    console.log(scope.element);
                    PipelineElementIconService.addImageOrTextIcon(elem, scope.element, false, 'draggable');
                }

            }
        }
}; 
