var spMarketplace = angular.module('spMarketplace', ['ngMaterial','ngMdIcons']);
	
spMarketplace.controller('MarketplaceCtrl', function($rootScope, $scope, $timeout, $log, $location, $http, restApi) {
		
		$scope.currentElements = {};
		
		 $scope.tabs = [
			            {
			                  title : 'Actions',
			                  type: 'action'
			            },
			            {
			                  title : 'Sepas',
			                  type: 'sepa'
			            },
			            {
			                  title : 'Sources',
			                  type: 'source'
			                },
			            ];

		$scope.loadCurrentElements = function(type) {
			if (type == 'source')  { $scope.loadAvailableSources();  }
			else if (type == 'sepa') { $scope.loadAvailableSepas();  }
			else if (type == 'action') { $scope.loadAvailableActions(); }
		}

	    $scope.loadAvailableActions = function() {
	        restApi.getAvailableActions()
	            .success(function (actions) {
	            	$scope.currentElements = actions;
	            })
	            .error(function (error) {
	                $scope.status = 'Unable to load actions: ' + error.message;
	            });
	    }
	    
	    $scope.loadAvailableSepas = function () {
	        restApi.getAvailableSepas()
	            .success(function (sepas) {
	            	$scope.currentElements = sepas;
	            })
	            .error(function (error) {
	                $scope.status = 'Unable to load sepas: ' + error.message;
	            });
	    }
	    
	    $scope.loadAvailableSources = function () {
	        restApi.getAvailableSources()
	            .success(function (sources) {
	            	$scope.currentElements = sources;
	            })
	            .error(function (error) {
	                $scope.status = 'Unable to load sources: ' + error.message;
	            });
	    }
	    
	    $scope.toggleFavorite = function(element, type) {
			   if (type == 'action') $scope.toggleFavoriteAction(element, type);
			   else if (type == 'source') $scope.toggleFavoriteSource(element, type);
			   else if (type == 'sepa') $scope.toggleFavoriteSepa(element, type);
		   }
	    
	    $scope.toggleFavoriteAction = function (action, type) {
			   if (action.favorite) {
				   restApi.removePreferredAction(action.elementId).success(function (msg) {
					$scope.showToast(msg.notifications[0].title);   
				   }).error(function(error) {
					   $scope.showToast(error.data.name);  
				   }).then(function() {
					   $scope.loadCurrentElements(type);  
				   })
			   }
			   else {
				   restApi.addPreferredAction(action.elementId).success(function (msg) {
					   $scope.showToast(msg.notifications[0].title);     
				   }).error(function(error) {
					   $scope.showToast(error.notifications[0].title);   
				   }).then(function() {
					   $scope.loadCurrentElements(type);
				   })
			   }
		   }
		   
		   $scope.toggleFavoriteSepa = function (sepa, type) {
			   if (sepa.favorite) {
				   restApi.removePreferredSepa(sepa.elementId).success(function (msg) {
					$scope.showToast(msg.notifications[0].title);   
				   }).error(function(error) {
					   $scope.showToast(error.notifications[0].title);   
				   }).then(function() {
					   $scope.loadCurrentElements(type);
				   })
			   }
			   else {
				   restApi.addPreferredSepa(sepa.elementId).success(function (msg) {
					   $scope.showToast(msg.notifications[0].title);     
				   }).error(function(error) {
					   $scope.showToast(error.notifications[0].title);   
				   }).then(function() {
					   $scope.loadCurrentElements(type);
				   })
			   }
		   }
		   
		   $scope.toggleFavoriteSource = function (source, type) {
			   if (source.favorite) {
				   restApi.removePreferredSource(source.elementId).success(function (msg) {
					$scope.showToast(msg.notifications[0].title);   
				   }).error(function(error) {
					   $scope.showToast(error.notifications[0].title);   
				   }).then(function() {
					   $scope.loadCurrentElements(type); 
				   })
			   }
			   else {
				   restApi.addPreferredSource(source.elementId).success(function (msg) {
					   $scope.showToast(msg.notifications[0].title);     
				   }).error(function(error) {
					   $scope.showToast(error.notifications[0].title);   
				   }).then(function() {
					   $scope.loadCurrentElements(type); 
				   })
			   }
		   }
	     
	    $scope.showToast = function(string) {
		    $mdToast.show(
		      $mdToast.simple()
		        .content(string)
		        .position("right")
		        .hideDelay(3000)
		    );
	   };	
	   
	   $scope.showAlert = function(ev, title, content) {

		    $mdDialog.show(
		      $mdDialog.alert()
		        .parent(angular.element(document.querySelector('#topDiv')))
		        .clickOutsideToClose(true)
		        .title(title)
		        .content(angular.toJson(content, true))
		        .ariaLabel('JSON-LD')
		        .ok('Done')
		        .targetEvent(ev)
		    );
	   };	   
	});