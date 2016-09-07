HomeCtrl.$inject = [ '$scope'];

export default function HomeCtrl($scope) {
	
	$scope.componentInfo = [{"title" : "StreamStory", "description" : "Data Stream Analysis", "iconText" : "ST", "link" : "streamstory", "color" : "#009688"},
	                        {"title" : "StreamPipes", "description" : "Stream Processing Pipelines", "iconText" : "SP", "link" : "streampipes", "color" : "#2196F3"},
	                        {"title" : "Pandda", "description" : "Decision Configuration", "iconText" : "PA", "link" : "pandda", "color" : "#F50057"},
	                        {"title" : "Hippo", "description" : "KPI Modeling", "iconText" : "HI", "link" : "hippo", "color" : "#FF5722"}]
};
