//TODO check if this service is still needed
// and if it works ;)
httpInterceptor.$inject = ['$log'];

export default function httpInterceptor ($log) {

	//httpInterceptor.$inject = ["$rootScope", "$q", "$timeout", "$state"];
	var httpInterceptor = function($rootScope, $q, $timeout, $state) {
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
						$state.go("login");
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
	};
	return httpInterceptor;
};
