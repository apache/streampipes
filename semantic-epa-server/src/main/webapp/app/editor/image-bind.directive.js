imageBind.$inject = [];

export default function imageBind() {
        return {
            restrict: 'A',
            link: function (scope, elem, attrs) {
                if (attrs.imageBind == 'block') {
                    scope.addImageOrTextIcon(elem, scope.element, false, 'block');
                } else if (attrs.imageBind == 'draggable') {
                    scope.addImageOrTextIcon(elem, scope.element, false, 'draggable');
                }

            }
        }
}; 
