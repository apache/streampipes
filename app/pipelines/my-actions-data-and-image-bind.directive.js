export default function myActionDataAndImageBind() {
	return {
		restrict: 'A',
		link: function(scope, elem, attrs){
			scope.addImageOrTextIcon(elem, scope.action);
			elem.data("JSON", scope.action);
			elem.data("pipeline", scope.pipeline);
			elem.attr({'data-toggle' : "tooltip", 'data-placement': "top", 'title' : scope.action.name});
			elem.tooltip();
		}
	}
};
