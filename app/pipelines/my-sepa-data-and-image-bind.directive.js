export default function mySepaDataAndImageBind() {
	return {
		restrict: 'A',
		link: function(scope, elem, attrs){
			scope.addImageOrTextIcon(elem, scope.sepa);
			elem.data("JSON", scope.sepa);
			elem.data("pipeline", scope.pipeline);
			elem.attr({'data-toggle' : "tooltip", 'data-placement': "top", 'title' : scope.sepa.name});
			elem.tooltip();
		}
	}
};
