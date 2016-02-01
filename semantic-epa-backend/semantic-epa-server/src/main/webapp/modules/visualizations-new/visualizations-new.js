angular.module('streamPipesApp')
.controller('VizCtrl', function($rootScope, $scope, $timeout, $log, $location, $http, restApi, $mdToast, $animate, $mdDialog, $interval, $sce) {

	$scope.runningVisualizations;
	$scope.dashboardReady = false;
	
	var widgetDefinitions = [
	                         
	                         ];
	
	var defaultWidgets = [];
	
	$scope.trustSrc = function(src) {
	    return $sce.trustAsResourceUrl(src);
	}
	
	$scope.getPipelines = function(){
        restApi.getRunningVisualizations()
            .success(function(visualizations){
               preloadVisualizations(visualizations);
            })
            .error(function(msg){
                console.log(msg);
            });
    };
    
    var preloadVisualizations = function(visualizations) {
    	angular.forEach(visualizations, function(viz) {
    		widgetDefinitions.push({"name" : viz.pipelineName, "style" : { "min-width" : "40%" }, "template" : '<iframe ng-src="' +viz.consumerUrl +'" style="border:0px;min-height:500px;width:600px;" width:"100%" height:"100%" layout="row" layout-align="center center"></iframe>'})
    		
    	});
    	console.log(widgetDefinitions);
    	defaultWidgets = widgetDefinitions;
    	
    	$scope.dashboardOptions = {
    	          widgetButtons: true,
    	          widgetDefinitions: widgetDefinitions,
    	          defaultWidgets: defaultWidgets
    	        };
    	$scope.dashboardReady = true;
    	$('iframe').iFrameResize( [{log:true}] );
    }
    
	
	
    var defaultWidgets = _.map(widgetDefinitions, function (widgetDef) {
        return {
          name: widgetDef.name
        };
      });
        

    $scope.getPipelines();
    
    $scope.dashboardOptions = {
	          widgetButtons: true,
	          widgetDefinitions: widgetDefinitions,
	          defaultWidgets: defaultWidgets
	        };
        

//        $scope.percentage = 5;
//        $interval(function () {
//          $scope.percentage = ($scope.percentage + 10) % 100;
//        }, 1000);
});
