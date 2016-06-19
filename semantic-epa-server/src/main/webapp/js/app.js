'use strict';

angular
    .module('streamPipesApp', ['ngMaterial', 
                               'ngMdIcons', 
                               'ngRoute', 
                               'ngCookies', 
                               'angular-loading-bar', 
                               'useravatar', 
                               'schemaForm', 
                               'ui.router', 
                               'ngPrettyJson', 
                               'ui.tree', 
                               'ng-context-menu', 
                               'ngFileUpload', 
                               'duScroll', 
                               'ui.dashboard', 
                               'angularjs-dropdown-multiselect', 
                               'rt.popup', 
                               'angular-clipboard',
                               'ngSanitize',
                               'btford.markdown'])
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
	
    .controller('TopNavCtrl', function($scope, $rootScope, restApi, $sce, $http, $state) {
    	
    	$scope.panddaUrl = "";
    	$scope.streamStoryUrl = "";
    	$scope.hippoUrl = "";
    	$scope.humanMaintenanceReportUrl = "";
    	$scope.humanInspectionReportUrl = "";
    	
    	$scope.trustSrc = function(src) {
    	    return $sce.trustAsResourceUrl(src);
    	  }
    	
    	$scope.loadConfig = function() {
			restApi.getConfiguration().success(function(msg) {
				$scope.panddaUrl = msg.panddaUrl;
				$scope.streamStoryUrl = msg.streamStoryUrl;
				$scope.hippoUrl = msg.hippoUrl;
				$scope.humanInspectionReportUrl = msg.humanInspectionReportUrl;
				$scope.humanMaintenanceReportUrl = msg.humanMaintenanceReportUrl;
			});
		}
    	
    	$scope.logout = function() {
	        $http.get("/semantic-epa-backend/api/v2/admin/logout").then(function() {
		        $scope.user = undefined;
		        $rootScope.authenticated = false;
		        $state.go("streampipes.login");
	        });
	      };	

		$scope.loadConfig();


	})
	.controller('AppCtrl', function ($rootScope, $scope, $q, $timeout, $mdSidenav, $mdUtil, $log, $location, $http, $cookies, $cookieStore, restApi, $state) {

		$rootScope.unreadNotifications = [];
		$rootScope.title = "StreamPipes";

		$scope.toggleLeft = buildToggler('left');
		$rootScope.userInfo = {
			Name : "D",
			Avatar : null
		};

		$rootScope.go = function ( path ) {
			//$location.path( path );
			$state.go(path);
			$mdSidenav('left').close();
		};

	      $scope.logout = function() {
	        $http.get("/semantic-epa-backend/api/v2/admin/logout").then(function() {
		        $scope.user = undefined;
		        $rootScope.authenticated = false;
		        $state.go("streampipes.login");
	        });
	      };	      
	      
        $scope.menu = [
           {
             link : 'streampipes',
             title: 'Editor',
             icon: 'action:ic_dashboard_24px' 
           },
           {
             link : 'streampipes.pipelines',
             title: 'Pipelines',
             icon: 'av:ic_play_arrow_24px'
           },
           {
             link : 'streampipes.visualizations',
             title: 'Visualizations',
             icon: 'editor:ic_insert_chart_24px'
           }
//           },
//           {
//               link : 'streampipes.marketplace',
//               title: 'Marketplace',
//               icon: 'maps:ic_local_mall_24px'
//           }
         ];
         $scope.admin = [
           {
             link : 'streampipes.myelements',
             title: 'My Elements',
             icon: 'image:ic_portrait_24px'
           },
           {
			   link : 'streampipes.add',
			   title: 'Import Elements',
			   icon: 'content:ic_add_24px'
		   },
			 {
				 link : 'streampipes.sensors',
				 title: 'Pipeline Element Generator',
				 icon: 'social:ic_share_24px'
            },
			 {
				 link : 'streampipes.ontology',
				 title: 'Knowledge Management',
				 icon: 'social:ic_share_24px'
           },
           {
             link : 'streampipes.settings',
             title: 'Settings',
             icon: 'action:ic_settings_24px'
           }
         ];
        
        function buildToggler(navID) {
            var debounceFn =  $mdUtil.debounce(function(){
                $mdSidenav(navID)
                    .toggle();
            },300);
            return debounceFn;
        }
    })
    .controller('LeftCtrl', function ($scope, $timeout, $mdSidenav, $log) {
        $scope.close = function () {
            $mdSidenav('left').close();
        };
    })

	.controller('RegisterCtrl', function ($scope, $timeout, $mdSidenav, $mdUtil, $log, $http) {

		$scope.loading = false;
		$scope.registrationFailed = false;
		$scope.registrationSuccess = false;
		$scope.errorMessage = "";
	

		$scope.roles = [{"name" : "System Administrator", "internalName" : "SYSTEM_ADMINISTRATOR"},
			{"name" : "Manager", "internalName" : "MANAGER"},
			{"name" : "Operator", "internalName" : "OPERATOR"},
			{"name" : "Demo User", "internalName" : "USER_DEMO"}];

		$scope.selectedRole = $scope.roles[0].internalName;

		$scope.register = function() {
			var payload = {};
			payload.username = $scope.username;
			payload.password = $scope.password;
			payload.email = $scope.email;
			payload.role = $scope.selectedRole;
			$scope.loading = true;
			$scope.registrationFailed = false;
			$scope.registrationSuccess = false;
			$scope.errorMessage = "";

			$http.post("/semantic-epa-backend/api/v2/admin/register", payload)
				.then(
				function(response) {
					$scope.loading = false;
					if (response.data.success)
					{
						$scope.registrationSuccess = true;
					}
					else
					{
						$scope.registrationFailed = true;
						$scope.errorMessage = response.data.notifications[0].title;
					}

				}, function(response) { // error

					$scope.loading = false;
					$scope.registrationFailed = true;
				}
			)
		};
	})
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
			appConfig : 'StreamPipes'
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


