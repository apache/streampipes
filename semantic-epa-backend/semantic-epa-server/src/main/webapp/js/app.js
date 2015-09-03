'use strict';

angular
    .module('streamPipesApp', ['ngMaterial', 'ngMdIcons', 'ngRoute', 'ngCookies', 'angular-loading-bar', 'useravatar', 'schemaForm'])
    .constant("apiConstants", {
        url: "http://localhost",
        port: "8080",
        contextPath : "/semantic-epa-backend",
        api : "/api/v2"
    })
    .run(function($rootScope, $location, restApi, authService) {
    	
    	restApi.configured().success(function(msg) {
        			if (msg.configured)
        				{
        					authService.authenticate();
        				}
        			else {
        					$rootScope.authenticated = false;
        					$location.path("/setup");
        				}
        });
    	
	    $rootScope.$on("$routeChangeStart", function(event, nextRoute, currentRoute) {
	        authService.authenticate();
	    });
    })
    .config(function($mdIconProvider) {
	
		$mdIconProvider
	      .iconSet('action', '/semantic-epa-backend/img/svg/svg-sprite-action.svg', 24)
	      .iconSet('alert', '/semantic-epa-backend/img/svg/svg-sprite-alert.svg', 24)
	      .iconSet('av', '/semantic-epa-backend/img/svg/svg-sprite-av.svg', 24)
	      .iconSet('communication', '/semantic-epa-backend/img/svg/svg-sprite-communication.svg', 24)
	      .iconSet('content', '/semantic-epa-backend/img/svg/svg-sprite-content.svg', 24)
	      .iconSet('device', '/semantic-epa-backend/img/svg/svg-sprite-device.svg', 24)
	      .iconSet('editor', '/semantic-epa-backend/img/svg/svg-sprite-editor.svg', 24)
	      .iconSet('file', '/semantic-epa-backend/img/svg/svg-sprite-file.svg', 24)
	      .iconSet('hardware', '/semantic-epa-backend/img/svg/svg-sprite-hardware.svg', 24)
	      .iconSet('image', '/semantic-epa-backend/img/svg/svg-sprite-image.svg', 24)
	      .iconSet('maps', '/semantic-epa-backend/img/svg/svg-sprite-maps.svg', 24)
	      .iconSet('navigation', '/semantic-epa-backend/img/svg/svg-sprite-navigation.svg', 24)
	      .iconSet('notification', '/semantic-epa-backend/img/svg/svg-sprite-notification.svg', 24)
	      .iconSet('social', '/semantic-epa-backend/img/svg/svg-sprite-social.svg', 24)
	      .iconSet('toggle', '/semantic-epa-backend/img/svg/svg-sprite-toggle.svg', 24)
	      .iconSet('avatars', '/semantic-epa-backend/img/svg/avatar-icons.svg', 24)
	      .defaultIconSet('/semantic-epa-backend/img/svg/svg-sprite-action.svg', 24);
	})
    .config(["$httpProvider", function($httpProvider) {
    	$httpProvider.defaults.withCredentials = true;
    	$httpProvider.interceptors.push('httpInterceptor');
    }])
	.config(function($routeProvider) {
        $routeProvider
        	.when('/', {
        		controller: 'AppCtrl',
    			 resolve:{
                     'AuthData':function(authService){
                       return authService.authenticate;
                     }
    			 }
        	})
            .when('/pipelines', {
                templateUrl : 'modules/pipelines/pipelines.html',
                controller  : 'PipelineCtrl'         
            })
            .when('/', {
                templateUrl : 'modules/editor/editor.html',
                controller  : 'EditorCtrl'
            })
            .when('/visualizations', {
                templateUrl : 'modules/visualizations/visualizations.html',
                controller  : 'AppCtrl'
            })
            .when('/marketplace', {
                templateUrl : 'modules/marketplace/marketplace.html',
                controller  : 'MarketplaceCtrl'
            })
             .when('/ontology', {
                templateUrl : 'ontology.html',
                controller  : 'AppCtrl'
            })
            .when('/myelements', {
                templateUrl : 'modules/myelements/myelements.html',
                controller  : 'MyElementsCtrl'
            })
             .when('/tutorial', {
                templateUrl : 'tutorial.html',
                controller  : 'AppCtrl'
            })
             .when('/login', {
                templateUrl : 'login.html',
                controller  : 'LoginCtrl'
            })
             .when('/register', {
                templateUrl : 'register.html',
                controller  : 'RegisterCtrl'
            })
             .when('/add', {
                templateUrl : 'modules/add/add.html',
                controller  : 'AddCtrl'
            })
             .when('/setup', {
                templateUrl : 'setup.html',
                controller  : 'SetupCtrl'
            })
            .when('/error', {
                templateUrl : 'error.html',
                controller  : 'SetupCtrl'
            })
            .when('/settings', {
                templateUrl : 'settings.html',
                controller  : 'SettingsCtrl'
            })
            .when('/create', {
                templateUrl : 'modules/create/create.html',
                controller  : 'CreateCtrl'
            })
            .when('/notifications', {
                templateUrl : 'modules/notifications/notifications.html',
                controller  : 'RecommendationCtrl'
            })
	})
    .controller('AppCtrl', function ($rootScope, $scope, $q, $timeout, $mdSidenav, $mdUtil, $log, $location, $http, $cookies, $cookieStore, restApi) {
       
    	$rootScope.unreadNotifications = [];
    	
    	$scope.toggleLeft = buildToggler('left');
    	$rootScope.userInfo = {
    			Name : "D",
    			Avatar : null
    	};
    	    
		$rootScope.go = function ( path ) {
			  $location.path( path );
		};

	      $scope.logout = function() {
	        $http.get("/semantic-epa-backend/api/v2/admin/logout").then(function() {
		        $scope.user = undefined;
		        $rootScope.authenticated = false;
		        $location.path("/login");
	        });
	      };	      
	      
        $scope.menu = [
           {
             link : '/',
             title: 'Editor',
             icon: 'action:ic_dashboard_24px' 
           },
           {
             link : '/pipelines',
             title: 'Pipelines',
             icon: 'av:ic_play_arrow_24px'
           },
           {
             link : '/visualizations',
             title: 'Visualizations',
             icon: 'editor:ic_insert_chart_24px'
           },
           {
               link : '/marketplace',
               title: 'Marketplace',
               icon: 'maps:ic_local_mall_24px'
           }
         ];
         $scope.admin = [
           {
             link : '/myelements',
             title: 'My Elements',
             icon: 'image:ic_portrait_24px'
           },
           {
               link : '/add',
               title: 'Add element',
               icon: 'content:ic_add_24px'
           },
           {
               link : '/create',
               title: 'Create element',
               icon: 'content:ic_add_24px'
           },
           {
               link : '/ontology',
               title: 'Ontology Editor',
               icon: 'social:ic_share_24px'
           },
           {
             link : '/settings',
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
    //.controller('EditorCtrl', function ($scope, $timeout, $mdSidenav, $mdUtil, $log) {
	//	$(init("Proa"));
	//})
	.controller('RegisterCtrl', function ($scope, $timeout, $mdSidenav, $mdUtil, $log, $http) {

		$scope.register = function() {
			var payload = {};
			payload.username = $scope.username;
			payload.password = $scope.password;
			payload.email = $scope.email;
			
			$http.post("/semantic-epa-backend/api/v2/admin/register", payload);
		};
	})

	.controller('LoginCtrl', function($rootScope, $scope, $timeout, $log, $location, $http) {
		
		$scope.loading = false;
			
		$scope.logIn = function() {
	    	  $scope.loading = true;
		      $http.post("/semantic-epa-backend/api/v2/admin/login", $scope.credentials)
		      .then(
		          function(response) { // success
		            $rootScope.username = response.data.info.authc.principal.username;
		            $rootScope.email = response.data.info.authc.principal.email;
		            $rootScope.authenticated = true;
		            $scope.loading = false;
		            $location.path("/");
		          }, function(response) { // error
		            $rootScope.authenticated = false;
		            return $q.reject("Login failed");
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
				sesameUrl: 'http://localhost:8080/openrdf-sesame',
				sesameDbName : 'test-6',
				kafkaProtocol : 'http',
				kafkaHost : 'localhost',
				kafkaPort : 9092,
				zookeeperProtocol : 'http',
				zookeeperHost : 'localhost',
				zookeeperPort : 2181,
				jmsProtocol : 'tcp',
				jmsHost : 'localhost',
				jmsPort : 61616,
				adminUsername: '',
				adminEmail: '' ,
				adminPassword: '',
				streamStoryUrl : '',
				panddaUrl : '',
				hippoUrl : ''		    
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
				$scope.loading = false;
				$scope.showToast(data.notifications[0].title);
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
	   
	   $scope.loadConfig();
		
	})
	
    .directive('userAvatar', function() {
	  return {
	    replace: true,
	    template: '<svg class="user-avatar" viewBox="0 0 128 128" height="64" width="64" pointer-events="none" display="block" > <path fill="#FF8A80" d="M0 0h128v128H0z"/> <path fill="#FFE0B2" d="M36.3 94.8c6.4 7.3 16.2 12.1 27.3 12.4 10.7-.3 20.3-4.7 26.7-11.6l.2.1c-17-13.3-12.9-23.4-8.5-28.6 1.3-1.2 2.8-2.5 4.4-3.9l13.1-11c1.5-1.2 2.6-3 2.9-5.1.6-4.4-2.5-8.4-6.9-9.1-1.5-.2-3 0-4.3.6-.3-1.3-.4-2.7-1.6-3.5-1.4-.9-2.8-1.7-4.2-2.5-7.1-3.9-14.9-6.6-23-7.9-5.4-.9-11-1.2-16.1.7-3.3 1.2-6.1 3.2-8.7 5.6-1.3 1.2-2.5 2.4-3.7 3.7l-1.8 1.9c-.3.3-.5.6-.8.8-.1.1-.2 0-.4.2.1.2.1.5.1.6-1-.3-2.1-.4-3.2-.2-4.4.6-7.5 4.7-6.9 9.1.3 2.1 1.3 3.8 2.8 5.1l11 9.3c1.8 1.5 3.3 3.8 4.6 5.7 1.5 2.3 2.8 4.9 3.5 7.6 1.7 6.8-.8 13.4-5.4 18.4-.5.6-1.1 1-1.4 1.7-.2.6-.4 1.3-.6 2-.4 1.5-.5 3.1-.3 4.6.4 3.1 1.8 6.1 4.1 8.2 3.3 3 8 4 12.4 4.5 5.2.6 10.5.7 15.7.2 4.5-.4 9.1-1.2 13-3.4 5.6-3.1 9.6-8.9 10.5-15.2M76.4 46c.9 0 1.6.7 1.6 1.6 0 .9-.7 1.6-1.6 1.6-.9 0-1.6-.7-1.6-1.6-.1-.9.7-1.6 1.6-1.6zm-25.7 0c.9 0 1.6.7 1.6 1.6 0 .9-.7 1.6-1.6 1.6-.9 0-1.6-.7-1.6-1.6-.1-.9.7-1.6 1.6-1.6z"/> <path fill="#E0F7FA" d="M105.3 106.1c-.9-1.3-1.3-1.9-1.3-1.9l-.2-.3c-.6-.9-1.2-1.7-1.9-2.4-3.2-3.5-7.3-5.4-11.4-5.7 0 0 .1 0 .1.1l-.2-.1c-6.4 6.9-16 11.3-26.7 11.6-11.2-.3-21.1-5.1-27.5-12.6-.1.2-.2.4-.2.5-3.1.9-6 2.7-8.4 5.4l-.2.2s-.5.6-1.5 1.7c-.9 1.1-2.2 2.6-3.7 4.5-3.1 3.9-7.2 9.5-11.7 16.6-.9 1.4-1.7 2.8-2.6 4.3h109.6c-3.4-7.1-6.5-12.8-8.9-16.9-1.5-2.2-2.6-3.8-3.3-5z"/> <circle fill="#444" cx="76.3" cy="47.5" r="2"/> <circle fill="#444" cx="50.7" cy="47.6" r="2"/> <path fill="#444" d="M48.1 27.4c4.5 5.9 15.5 12.1 42.4 8.4-2.2-6.9-6.8-12.6-12.6-16.4C95.1 20.9 92 10 92 10c-1.4 5.5-11.1 4.4-11.1 4.4H62.1c-1.7-.1-3.4 0-5.2.3-12.8 1.8-22.6 11.1-25.7 22.9 10.6-1.9 15.3-7.6 16.9-10.2z"/> </svg>'
	  };
	})
	.factory('httpInterceptor', ['$log', function($log) {  
	    
	    var httpInterceptor = ["$rootScope", "$q", "$timeout", function($rootScope, $q, $timeout) {
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
	                $location.path("/login");
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
    }])
    .service('authService', function($http, $rootScope, $location) {
    	
    	var promise = $http.get("/semantic-epa-backend/api/v2/admin/authc")
        .then(
  	          function(response) {
  	        	if (response.data.success == false) 
          		{
          			$rootScope.authenticated = false;
          			$location.path("/login");
          		}
  	        	else {
  	        	$rootScope.username = response.data.info.authc.principal.username;
  	        	$rootScope.email = response.data.info.authc.principal.email;
  		        $rootScope.authenticated = true;
  		        $http.get("/semantic-epa-backend/api/v2/users/" +$rootScope.email +"/notifications")
  			              .success(function(notifications){
  			                  $rootScope.unreadNotifications = notifications
  			                  console.log($rootScope.unreadNotifications);
  			              })
  			              .error(function(msg){
  			                  console.log(msg);
  			              });

  	          }
  	          },
  	          function(response) {
  	        	  $rootScope.username = undefined;
  			      $rootScope.authenticated = false;
  		          $location.path("/login");
  	          });
        
    	return 
        	{
            	authenticate: promise
            };
    })
	.factory('restApi', ['$rootScope', '$http', 'apiConstants', 'authService', function($rootScope, $http, apiConstants, authService) {
	    
	    var restApi = {};
	    
	    var urlBase = function() {
	    	return apiConstants.contextPath +apiConstants.api +'/users/' +$rootScope.email;
	    };   
	
	    restApi.getOwnActions = function () {
	        return $http.get(urlBase() +"/actions/own");
	    };
	    
	    restApi.getAvailableActions = function () {
	        return $http.get(urlBase() +"/actions/available");
	    };
	    
	    restApi.getPreferredActions = function () {
	        return $http.get(urlBase() +"/actions/favorites");
	    };
	    
	    restApi.addPreferredAction = function(elementUri) {
	    	return $http({
	    	    method: 'POST',
	    	    url: urlBase() + "/actions/favorites",
	    	    data: $.param({uri: elementUri}),
	    	    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
	    	})
	    }
	    
	    restApi.removePreferredAction = function(elementUri) {
	    	return $http({
	    	    method: 'DELETE',
	    	    url: urlBase() + "/actions/favorites/" +encodeURIComponent(elementUri)
	    	})
	    }
	    
	    restApi.getOwnSepas = function () {
	        return $http.get(urlBase() +"/sepas/own");
	    };
	    
	    restApi.getAvailableSepas = function () {
	        return $http.get(urlBase() +"/sepas/available");
	    };
	    
	    restApi.getPreferredSepas = function () {
	        return $http.get(urlBase() +"/sepas/favorites");
	    };
	    
	    restApi.addPreferredSepa = function(elementUri) {
	    	return $http({
	    	    method: 'POST',
	    	    url: urlBase() + "/sepas/favorites",
	    	    data: $.param({uri: elementUri}),
	    	    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
	    	})
	    }
	    
	    restApi.removePreferredSepa = function(elementUri) {
	    	return $http({
	    	    method: 'DELETE',
	    	    url: urlBase() + "/sepas/favorites/" +encodeURIComponent(elementUri),
	    	})
	    }
	    
	    restApi.getOwnSources = function () {
	    	console.log($rootScope.email);
	    	return $http.get(urlBase() +"/sources/own");
	    };
	    
	    restApi.getAvailableSources = function () {
	    	return $http.get(urlBase() +"/sources/available");
	    };
	    
	    restApi.getPreferredSources = function () {
	        return $http.get(urlBase() +"/sources/favorites");
	    };
	    	    
	    restApi.addPreferredSource = function(elementUri) {
	    	return $http({
	    	    method: 'POST',
	    	    url: urlBase() + "/sources/favorites",
	    	    data: $.param({uri: elementUri}),
	    	    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
	    	})
	    }
	    
	    restApi.removePreferredSource = function(elementUri) {
	    	return $http({
	    	    method: 'DELETE',
	    	    url: urlBase() + "/sources/favorites/" +encodeURIComponent(elementUri),
	    	})
	    }

		restApi.getOwnStreams = function(source){
			return $http.get(urlBase() + "/sources/" + encodeURIComponent(source.elementId) + "/streams");

		};
	    
	    restApi.add = function(elementUri, ispublic) {
	    	return $http({
	    	    method: 'POST',
	    	    url: urlBase() + "/element",
	    	    data: $.param({uri: elementUri, publicElement: ispublic}),
	    	    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
	    	})
	    }
	    
	    restApi.addBatch = function(elementUris, ispublic) {
	    	return $http({
	    	    method: 'POST',
	    	    url: urlBase() + "/element/batch",
	    	    data: $.param({uri: elementUris, publicElement: ispublic}),
	    	    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
	    	})
	    }
	    
	    restApi.update = function(elementUri) {
	    	return $http({
	    	    method: 'PUT',
	    	    url: urlBase() + "/element/" +encodeURIComponent(elementUri)
	    	})
	    }
	    
	    restApi.del = function(elementUri) {
	    	return $http({
	    	    method: 'DELETE',
	    	    url: urlBase() + "/element/" +encodeURIComponent(elementUri)
	    	})
	    }
	    
	    restApi.jsonld = function(elementUri) {
	    	return $http.get(urlBase() +"/element/" +encodeURIComponent(elementUri) +"/jsonld");
	    }
	    
	    restApi.configured = function() {
	    	return $http.get(apiConstants.contextPath +apiConstants.api +"/setup/configured");
	    }
	    
	    restApi.getConfiguration = function() {
	    	return $http.get(apiConstants.contextPath +apiConstants.api +"/setup/configuration");
	    }
	    
	    restApi.updateConfiguration = function(config) {
	    	return $http.put(apiConstants.contextPath +apiConstants.api +"/setup/configuration", config);
	    };
	    
	    restApi.getOwnPipelines = function() {
	    	return $http.get(urlBase() +"/pipelines/own");
	    	//return $http.get("/semantic-epa-backend/api/pipelines");
	    };
	    
	    restApi.storePipeline = function(pipeline) {
	    	return $http.post(urlBase() +"/pipelines", pipeline);
	    }
	    
	    restApi.deleteOwnPipeline = function(pipelineId) {
	    	return $http({
	    	    method: 'DELETE',
	    	    url: urlBase() + "/pipelines/" +pipelineId
	    	})
	    }
	    
	    restApi.recommendPipelineElement = function(pipeline) {
	    	return $http.post(urlBase() +"/pipelines/recommend", pipeline);
	    }
	    
	    restApi.updatePartialPipeline = function(pipeline) {
	    	return $http.post(urlBase() +"/pipelines/update", pipeline);
	    }
	    
	    restApi.startPipeline = function(pipelineId) {
	    	return $http.get(urlBase() +"/pipelines/" +pipelineId +"/start");
	    }
	    
	    restApi.stopPipeline = function(pipelineId) {
	    	return $http.get(urlBase() +"/pipelines/" +pipelineId +"/stop");
	    }
	    
	    restApi.getNotifications = function() {
	    	return $http.get(urlBase() +"/notifications");
	    }
	    
	    restApi.getUnreadNotifications = function() {
	    	return $http.get(urlBase() +"/notifications/unread");
	    }
	    
	    restApi.updateNotification = function(notificationId) {
	    	return $http.put(urlBase() +"/notifications/" +notificationId);
	    }
	    
	    restApi.deleteNotifications = function(notificationId) {
	    	return $http({
	    	    method: 'DELETE',
	    	    url: urlBase() + "/notifications/" +notificationId
	    	});
	    }
	
	    return restApi;
	}]);
