export default function myStreamDataAndImageBind(){
	return {
		restrict: 'A',
		link: function(scope, elem, attrs){
			scope.addImageOrTextIcon(elem, scope.stream);
			elem.data("JSON", scope.stream);
			elem.data("pipeline", scope.pipeline);
			elem.attr({'data-toggle' : "tooltip", 'data-placement': "top", 'title' : scope.stream.name});
			elem.tooltip();
		}
	}
};
