'use strict';
import angular from 'npm/angular';
//import ngMaterial from 'npm/angular-material';
//import ngMdIcons from 'npm/angular-material-icons';
//import ngRoute from 'npm/angular-route';
//import ngCookies from 'npm/angular-cookies';
//import angularLoadingBar from 'npm/angular-loading-bar';
//import useravatar from 'lib/useravatar';
//import schemaForm from 'npm/angular-schema-form';
import uiRouter from 'npm/angular-ui-router';
//import ngPrettyJson from 'npm/ng-prettyjson';
//import uiTree from 'npm/angular-ui-tree';
//import ng-context-menu from '';
//import ngFileUpload from 'npm/ng-file-upload';
//import duScroll from 'npm/angular-scroll';
//import angularjs-dropdown-multiselect from '';
//import rtPopup from 'npm/angular-rt-popup';
//import angularClipboard from 'npm/angular-clipboard';
//import ngSanitize from 'npm/angular-sanitize';
//import btfordMarkdown from 'npm/angular-markdown-directive';

import spServices from './services/services.module'
import delme from './delme'

import spCore from './core/core.module'
import spLayout from './layout/layout.module'
import spLogin from './login/login.module'

//import restApi from './services/rest-api.service'
//import authService from './services/auth.service'
//import spServices from './services/services.module'


const MODULE_NAME = 'streamPipesApp';

export default angular
	.module(MODULE_NAME, [
		 //'ngMaterial', 
															 //'ngMdIcons', 
															 delme,
															 spServices,
															 spCore,
															 spLayout,
															 spLogin,
															 //'spConstants',
															 //'sp-services',
                               //'ngRoute', 
                               //'ngCookies', 
                               //'angularLoadingBar', 
                               //'useravatar', 
                               //'schemaForm', 
															 uiRouter, 
                               //'ngPrettyJson', 
                               //'uiTree', 
                               //'ng-context-menu', 
                               //'ngFileUpload', 
                               //'duScroll', 
                               //'angularjs-dropdown-multiselect', 
                               //'rtPopup', 
                               //'angularClipboard',
                               //'ngSanitize',
			//'btfordMarkdown'
		])
	.run(function($rootScope, $location, restApi, authService, $state, $urlRouter, objectProvider) {

		//$location.path("/setup");
		var bypass;
		
		if (!$location.path().startsWith("/login") && !$location.path().startsWith("/sso")) {
			restApi.configured().success(function(msg) {
				if (msg.configured)
				{
					authService.authenticate;
				}
				else {
					$rootScope.authenticated = false;
					$state.go("streampipes.setup");
				}
			});
		}
		

		$rootScope.$on('$stateChangeStart',
			function(event, toState, toParams, fromState, fromParams){
			console.log(toState.name);
				var isLogin = toState.name === "streampipes.login";
				var isSetup = toState.name === "streampipes.setup";
				var isExternalLogin = (toState.name === "sso" || toState.name === "ssosuccess");
				var isRegister = toState.name === "streampipes.register";
				console.log("Setup: " +isSetup +", Login: " +isLogin);
				if(isLogin || isSetup || isRegister || isExternalLogin){
					return;
				}
				else if($rootScope.authenticated === false) {
					event.preventDefault();
					console.log("logging in event prevent");
					$state.go('streampipes.login');
				}

			})

		$rootScope.$on("$routeChangeStart", function(event, nextRoute, currentRoute) {
			authService.authenticate;
		});
		$rootScope.state = new objectProvider.State();
		$rootScope.state.sources = false;
		$rootScope.state.sepas = false;
		$rootScope.state.actions = false;
		$rootScope.state.adjustingPipelineState = false;
		$rootScope.state.adjustingPipeline = {};

	})
	
//TODO refactor ==================================================================================================
	.controller('SsoCtrl', function($rootScope, $scope, $timeout, $log, $location, $http, $state, $stateParams) {
		 //console.log($stateParams.target);
		$http.get("/semantic-epa-backend/api/v2/admin/authc").success(function(data){
			console.log(data);
					 if (!data.success) window.top.location.href = "http://localhost:8080/semantic-epa-backend/#/streampipes/login/" +$stateParams.target;
					 else $state.go("ssosuccess");
				})

	})
	.controller('LoginCtrl', function($rootScope, $scope, $timeout, $log, $location, $http, $state, $stateParams) {
		$scope.loading = false;
		$scope.authenticationFailed = false;
		$rootScope.title = "ProaSense";

		$scope.logIn = function() {
			$scope.authenticationFailed = false;
			$scope.loading = true;
			$http.post("/semantic-epa-backend/api/v2/admin/login", $scope.credentials)
				.then(
				function(response) { // success
					$scope.loading = false;
					if (response.data.success)
					{
						$rootScope.username = response.data.info.authc.principal.username;
						$rootScope.email = response.data.info.authc.principal.email;
						$rootScope.authenticated = true;
						if ($stateParams.target != "") {
							console.log("going to " +$stateParams.target);
								$state.go($stateParams.target);
						}
						else if ($rootScope.appConfig == 'ProaSense') $state.go("home");
						else $state.go("streampipes");
					}
					else
					{
						$rootScope.authenticated = false;
						$scope.authenticationFailed = true;
					}

				}, function(response) { // error
					console.log(response);
					$scope.loading = false;
					$rootScope.authenticated = false;
					$scope.authenticationFailed = true;
				}
			)
		};
	})
	.controller('SetupCtrl', function($rootScope, $scope, $timeout, $log, $location, $http, restApi, $mdToast) {

		$scope.installationFinished = false;
		$scope.installationSuccessful = false;
		$scope.installationResults = [{}];
		$scope.loading = false;


		$scope.setup = {
			couchDbHost: 'localhost',
			couchDbProtocol : 'http',
			couchDbPort : 5984,
			couchDbUserDbName : 'users',
			couchDbPipelineDbName : 'pipeline',
			couchDbConnectionDbName : 'connection',
			couchDbMonitoringDbName : 'monitoring',
			couchDbNotificationDbName : 'notification',
			sesameUrl: 'http://localhost:8080/openrdf-sesame',
			sesameDbName : 'test-6',
			kafkaProtocol : 'http',
			kafkaHost : $location.host(),
			kafkaPort : 9092,
			zookeeperProtocol : 'http',
			zookeeperHost : $location.host(),
			zookeeperPort : 2181,
			jmsProtocol : 'tcp',
			jmsHost : $location.host(),
			jmsPort : 61616,
			adminUsername: '',
			adminEmail: '' ,
			adminPassword: '',
			streamStoryUrl : '',
			panddaUrl : '',
			hippoUrl : '',
			humanInspectionReportUrl : '',
			humanMaintenanceReportUrl : '',
			appConfig : 'StreamPipes',
			marketplaceUrl : '',
			podUrls : []
		};

		$scope.configure = function() {
			$scope.loading = true;
			$http.post("/semantic-epa-backend/api/v2/setup/install", $scope.setup).success(function(data) {
				$scope.installationFinished = true;
				$scope.installationResults = data;
				$scope.loading = false;
			}).error(function(data) {
				$scope.loading = false;
				$scope.showToast("Fatal error, contact administrator");
			});
		}

		$scope.showToast = function(string) {
			$mdToast.show(
				$mdToast.simple()
					.content(string)
					.position("right")
					.hideDelay(3000)
			);
		};

		$scope.addPod = function(podUrls) {
			if (podUrls == undefined) podUrls = [];
			podUrls.push("localhost");
		}

		$scope.removePod = function(podUrls, index) {
			podUrls.splice(index, 1);
		}

	})
	.controller('SettingsCtrl', function($rootScope, $scope, $timeout, $log, $location, $http, restApi, $mdToast) {

		$scope.loading = false;

		$scope.setup = {};

		$scope.loadConfig = function() {
			restApi.getConfiguration().success(function(msg) {
				$scope.setup = msg;
			});
		}

		$scope.configure = function() {
			$scope.loading = true;
			restApi.updateConfiguration($scope.setup).success(function(data) {
				$rootScope.appConfig = $scope.setup.appConfig;
				$scope.loading = false;
				$scope.showToast(data.notifications[0].title);
			}).error(function(data) {
				$scope.loading = false;
				$scope.showToast("Fatal error, contact administrator");
			});
		}
		
		$scope.addPod = function(podUrls) {
			if (podUrls == undefined) podUrls = [];
			podUrls.push("localhost");
		}
		
		$scope.removePod = function(podUrls, index) {
			podUrls.splice(index, 1);
		}

		$scope.showToast = function(string) {
			$mdToast.show(
				$mdToast.simple()
					.content(string)
					.position("right")
					.hideDelay(3000)
			);
		};

		$scope.loadConfig();

	})
	
	.filter('capitalize', function() {
		return function(input) {
			if (input!=null) {
				console.log(input);
				input = input.replace(/_/g, " ");
						var stringArr = input.split(" ");
						var result = "";
						var cap = stringArr.length;
						for(var x = 0; x < cap; x++) {
							stringArr[x] = stringArr[x].toLowerCase();
							if(x === cap - 1) {
								result += stringArr[x].substring(0,1).toUpperCase() + stringArr[x].substring(1);
							} else {
								result += stringArr[x].substring(0,1).toUpperCase() + stringArr[x].substring(1) + " ";
							}
						}
					return result;
				}
		}
	});
//TODO refactor ==================================================================================================
