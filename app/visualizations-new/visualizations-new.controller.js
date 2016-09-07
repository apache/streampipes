VizCtrl.$inject = ['$rootScope', '$scope', '$log', '$location', '$http', 'restApi', '$mdToast', '$animate', '$mdDialog', '$interval', '$sce', '$timeout'];

export default function VizCtrl($rootScope, $scope, $log, $location, $http, restApi, $mdToast, $animate, $mdDialog, $interval, $sce, $timeout) {


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
    		widgetDefinitions.push({"name" : viz.pipelineName, "consumerUrl" : viz.consumerUrl, "size" : {"width" :"50%", "height" : "100%"}, "style" : { }, "template" : '<iframe ng-src="' +viz.consumerUrl +'" style="border:0px;width:100%;" scrolling="yes"></iframe>'})
    		
    	});
    	console.log(widgetDefinitions);
    	defaultWidgets = widgetDefinitions;
    	
    	$scope.dashboardOptions = {
    	          widgetButtons: true,
    	          widgetDefinitions: widgetDefinitions,
    	          defaultWidgets: defaultWidgets
    	        };
    	$scope.dashboardReady = true;
    
    	 $timeout($scope.resize, 1000);
    }
	
    var defaultWidgets = _.map(widgetDefinitions, function (widgetDef) {
        return {
          name: widgetDef.name
        };
      });
        

    $scope.getPipelines();
    
    $scope.resize = function() {
    	angular.element("iframe").iFrameResize({log                     : true,                  // Enable console logging
    	    enablePublicMethods     : true,                  // Enable methods within iframe hosted page
    	    heightCalculationMethod : 'max',
    	    checkOrigin : false,
    	    resizedCallback : function(msg) {
    	    	console.log(msg);
    	    	console.log("Hallo");
    	    },
    	    messageCallback : function(msg) {
    	    	console.log(msg);
    	    }});
    	console.log(angular.element("iframe"));
    	console.log("ressized2");
    	
    	//angular.element("iframe").first().parentElement.style.height="200px";
    	
    }
        
    $scope.dashboardOptions = {
	          widgetButtons: true,
	          widgetDefinitions: widgetDefinitions,
	          defaultWidgets: defaultWidgets
	        };
        

//        $scope.percentage = 5;
//        $interval(function () {
//          $scope.percentage = ($scope.percentage + 10) % 100;
//        }, 1000);
};
