import angular from 'npm/angular';
import spConstants from '../constants/'
import restApi from './rest-api.service'

export default angular.module('sp.services', [])
.factory('restApi', restApi)
.constant("apiConstants", {
        url: "http://localhost",
        port: "8080",
        contextPath : "/semantic-epa-backend",
        api : "/api/v2",
		streamEndpointOptions : {
			endpoint: ["Dot", {radius:12}],
			connectorStyle: {strokeStyle: "#BDBDBD", outlineColor : "#9E9E9E", lineWidth: 5},
			connector: "Straight",
			isSource: true,
			anchor:"Right",
			type : "token",
			connectorOverlays: [
				["Arrow", {width: 20, length: 10, location: 0.5, id: "arrow"}],
			]
		},

		sepaEndpointOptions : {
			endpoint: ["Dot", {radius:12}],
			connectorStyle: {strokeStyle: "#BDBDBD", outlineColor : "#9E9E9E", lineWidth: 5},
			connector: "Straight",
			isSource: true,
			anchor: "Right",
			type : "empty",
			connectorOverlays: [
				["Arrow", {width: 25, length: 20, location: 0.5, id: "arrow"}],
			]
		},

		leftTargetPointOptions : {
			endpoint: ["Dot", {radius:12}],
			type : "empty",
			anchor: "Left",
			isTarget: true
		}
	})
.service('authService', function authService ($http, $rootScope, $location, $state) {
		console.log($location.path());

		var promise = $http.get("/semantic-epa-backend/api/v2/admin/authc")
			.then(
				function(response) {
					if (response.data.success == false)
			{
				$rootScope.authenticated = false;
				$http.get("/semantic-epa-backend/api/v2/setup/configured")
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
				$http.get("/semantic-epa-backend/api/v2/setup/configured")
					.then(function(response) {
						if (response.data.configured) 
					{
						console.log(response.data.appConfig);
						$rootScope.appConfig = response.data.appConfig;
					}
					});
				$http.get("/semantic-epa-backend/api/v2/users/" +$rootScope.email +"/notifications")
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
					$http.get("/semantic-epa-backend/api/v2/setup/configured")
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
})
	.name;

