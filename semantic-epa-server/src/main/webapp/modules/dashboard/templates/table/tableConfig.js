'use strict';

angular.module('streamPipesApp')
    .directive('spTableWidgetConfig',function () {
        return {
						restrict: 'E',
            //replace: true,
						templateUrl: 'modules/dashboard/templates/table/tableConfig.html',
						//template: '<h1>This is awesome!</h1>',
						scope: {
							wid: '='
						}
						//controller: function ($scope) {
								//$scope.widget = Widgets.get($scope.widgetId);

					//var selectedProperties = [];
					//$scope.selectedVis.schema.eventProperties.forEach(function(entry) {
						//if (entry.isSelected) {
							//selectedProperties.push(entry);
						//}
					//});

					//console.log('Yeahh');
								//var lala = $scope.widget;
					
				
						//}
            //link: function postLink(scope) {
                //scope.$watch('data', function (data) {
                    //if (data) {
                        //scope.items = data;
                    //}
                //});
            //}
				};
    });
