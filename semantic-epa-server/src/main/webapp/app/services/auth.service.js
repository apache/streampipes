authService.$inject = ['$http', '$rootScope', '$location', '$state', 'restApi'];

export default function authService ($http, $rootScope, $location, $state, restApi) {
		console.log($location.path());

		//var promise = $http.get("/semantic-epa-backend/api/v2/admin/authc")

		var promise = restApi.getAuthc()
			.then(
				function(response) {
					if (response.data.success == false)
			{
				$rootScope.authenticated = false;
				//$http.get("/semantic-epa-backend/api/v2/setup/configured")
				restApi.configured()
					.then(function(response) {
						if (response.data.configured) 
					{
						console.log(response.data.appConfig);
						$rootScope.appConfig = response.data.appConfig;
						if (!$location.path().startsWith("/sso") && !$location.path().startsWith("/streampipes/login")) {
							console.log("configured 769");
							$state.go("streampipes.login")//$location.path("/login");
						}
					}
					else $state.go("streampipes.setup")
					})
			}
			else {
				$rootScope.username = response.data.info.authc.principal.username;
				$rootScope.email = response.data.info.authc.principal.email;
				$rootScope.authenticated = true;
				//$http.get("/semantic-epa-backend/api/v2/setup/configured")
				restApi.configured()
					.then(function(response) {
						if (response.data.configured) 
					{
						console.log(response.data.appConfig);
						$rootScope.appConfig = response.data.appConfig;
					}
					});
				//$http.get("/semantic-epa-backend/api/v2/users/" +$rootScope.email +"/notifications")
				restApi.getNotifications()
					.success(function(notifications){
						$rootScope.unreadNotifications = notifications
						//console.log($rootScope.unreadNotifications);
					})
					.error(function(msg){
						console.log(msg);
					});

			}
				},
				function(response) {
					$rootScope.username = undefined;
					$rootScope.authenticated = false;
					//$http.get("/semantic-epa-backend/api/v2/setup/configured")
				restApi.configured()
						.then(function(conf) {
							if (conf.data.configured) {
								console.log("configured 805");
								$state.go("streampipes.login")
							}
							else $state.go("streampipes.setup")
						})
				});

			return {
				authenticate: promise
			};
};
