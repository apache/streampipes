angular
    .module('streamPipesApp')
    .directive('marketplace', function(restApi, $mdDialog) {
    	return {
    		restrict : 'E',
    		templateUrl : 'modules/add/directives/marketplace.tmpl.html',
    		scope : {
    			
    		},
    		link: function($scope, element, attrs) {
    			 
    			
    			$scope.installLabel = "Install";
    			$scope.uninstalling = false;
    			$scope.availableApps = [];
    			
    			$scope.loadAvailableApps = function () {
    		        restApi.getAvailableApps()
    		            .success(function (apps) {
    		            	$scope.availableApps = apps;
    		            })
    		            .error(function (error) {
    		                $scope.status = 'Unable to load actions: ' + error.message;
    		            });
    		    }
    			    			
    			$scope.elementTextIcon = function (string){
    		        var result ="";
    		        if (string.length <= 4){
    		            result = string;
    		        }else {
    		            var words = string.split(" ");
    		            words.forEach(function(word, i){
    		                result += word.charAt(0);
    		            });
    		        }
    		        return result.toUpperCase();
    		    }
    			
    			$scope.showInstallationDialog = function(app) {
                	$mdDialog.show({
             	   	      controller: InstallationController,
             	   	      templateUrl: 'modules/add/directives/installationDialog.tmpl.html',
             	   	      parent: angular.element(document.body),
             	   	      clickOutsideToClose:true,
             	   	      scope:$scope,
             	   	      restApi:restApi,
             	   	      preserveScope:true,
             		   	  locals : {
             		   		  app : app,
             		      }
             	   	    })
                }
    			
    			$scope.uninstallApp = function(bundle) {
    				$scope.uninstalling = true;
    				restApi.uninstallApp(bundle)
    		        .success(function (message) {
    		        	$scope.uninstalling = false;
    		        	$scope.loadAvailableApps();
    		        	console.log(message);
    		        })
    		        .error(function (error) {
    		            $scope.status = 'Unable to uninstall: ' + error.message;
    		        });
    			}
    			
    			 $scope.showUninstallConfirmDialog = function(ev, app) {
    	                var confirm = $mdDialog.confirm()
    	                      .title('Uninstall element?')
    	                      .textContent('The element will be permanently removed from the target pod.')
    	                      .targetEvent(ev)
    	                      .ok('Uninstall')
    	                      .cancel('Cancel');
    	                $mdDialog.show(confirm).then(function() {
    	                  $scope.uninstallApp(app);
    	                }, function() {
    	               
    	                });
    	              };
    			
    			$scope.loadAvailableApps();
    		}
    	}
    });

function InstallationController($scope, restApi, $mdDialog, app) {
	$scope.app = app;
	$scope.installing = false;
	$scope.installed = false;
	$scope.installationMessage = {};
	$scope.availableTargetPods;
	
	$scope.hide = function() {
  		$mdDialog.hide();
  	};
  	
  	$scope.cancel = function() {
  	    $mdDialog.cancel();
  	};
  	
  	var loadTargetPods = function () {
        restApi.getTargetPods()
            .success(function (pods) {
            	$scope.availableTargetPods = pods;
            })
            .error(function (error) {
                $scope.status = 'Unable to load pods: ' + error.message;
            });
    }
  	
  	loadTargetPods();
  	
  	$scope.installApp = function(bundle) {
		$scope.installing = true;
		$scope.installLabel = "Installing..."
		restApi.installApp(bundle)
        .success(function (message) {
        	$scope.installing = false;
        	$scope.installed=true;
        	$scope.installLabel = "Installed";
        	$scope.installationMessage = message;
        	console.log(message);
        })
        .error(function (error) {
            $scope.status = 'Unable to load actions: ' + error.message;
        });
	}
}