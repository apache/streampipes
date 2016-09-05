 angular
    .module('streamPipesApp')
 	.factory('imageChecker', function(){
		var imageChecker = {};

		imageChecker.imageExists = function(url, callback) {
			if (url == null || url === 'undefined'){
				callback(false);
				return;
			}
			var img = new Image();
			img.onload = function() { callback(true); };
			img.onerror = function() { callback(false); };
			img.src = url;
		};
		imageChecker.imageExistsBoolean = function(url){
			if (url == null || url === 'undefined'){
				return false;
			}
			var img = new Image();
			img.onload = function() { callback(true); };
			img.onerror = function() { callback(false); };
			img.src = url;
		}

		return imageChecker;
	}).factory('httpInterceptor', ['$log', function($log) {

		var httpInterceptor = ["$rootScope", "$q", "$timeout", "$state", function($rootScope, $q, $timeout, $state) {
			return function(promise) {
				console.log("intercept");
				return promise.then(
					function(response) {
						return response;
					},
					function(response) {
						if (response.status == 401) {
							$rootScope.$broadcast("InvalidToken");
							$rootScope.sessionExpired = true;
							console.log("GOING ");
							$state.go("streampipes.login");
							$timeout(function() {$rootScope.sessionExpired = false;}, 5000);
						} else if (response.status == 403) {
							$rootScope.$broadcast("InsufficientPrivileges");
						} else {
							// Here you could handle other status codes, e.g. retry a 404
						}
						return $q.reject(response);
					}
				);
			};
		}];
		return httpInterceptor;
	}]);
