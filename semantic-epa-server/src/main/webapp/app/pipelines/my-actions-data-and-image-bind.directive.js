export default function myActionDataAndImageBind() {
	return {
		restrict: 'A',
		link: function(scope, elem, attrs){
			scope.addImageOrTextIcon(elem, scope.pipeline.action);
			elem.data("JSON", scope.pipeline.action);
			elem.data("pipeline", scope.pipeline);
			elem.attr({'data-toggle' : "tooltip", 'data-placement': "top", 'title' : scope.pipeline.action.name});
			elem.tooltip();
		}
	}
};
