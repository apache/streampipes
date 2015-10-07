'use strict';

angular
    .module('streamPipesApp', ['ngMaterial', 'ngMdIcons', 'ngRoute', 'ngCookies', 'angular-loading-bar', 'useravatar', 'schemaForm', 'ui.router', 'ngPrettyJson'])
    .constant("apiConstants", {
        url: "http://localhost",
        port: "8080",
        contextPath : "/semantic-epa-backend",
        api : "/api/v2",
		streamEndpointOptions : {
			endpoint: ["Dot", {radius: 5}],
			paintStyle: {fillStyle: "grey"},
			connectorStyle: {strokeStyle: "grey", lineWidth: 4},
			connector: "Straight",
			isSource: true,
			anchor: ["Perimeter", {shape: "Circle"}],
			connectorOverlays: [
				["Arrow", {width: 25, length: 20, location: .5, id: "arrow"}],
			]
		},

		sepaEndpointOptions : {
			endpoint: ["Dot", {radius: 5}],
			paintStyle: {fillStyle: "grey"},
			connectorStyle: {strokeStyle: "grey", lineWidth: 4},
			connector: "Straight",
			isSource: true,
			anchor: "Right",
			connectorOverlays: [
				["Arrow", {width: 25, length: 20, location: .5, id: "arrow"}],
			]
		},

		leftTargetPointOptions : {
			endpoint: "Rectangle",
			paintStyle: {fillStyle: "grey"},
			anchor: "Left",
			isTarget: true
		}
    })
    .run(function($rootScope, $location, restApi, authService, $state, $urlRouter, objectProvider) {

		//$location.path("/setup");
    	var bypass;
    	
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
        
        $rootScope.$on('$stateChangeStart', 
        		function(event, toState, toParams, fromState, fromParams){ 
        	var isLogin = toState.name === "streampipes.login";
        	var isSetup = toState.name === "streampipes.setup";
        	var isRegister = toState.name === "streampipes.register";
        
        	console.log("Setup: " +isSetup +", Login: " +isLogin);
            if(isLogin || isSetup || isRegister){
               return;
            }
            if($rootScope.authenticated === false) {
                event.preventDefault(); 
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
    .config(function($stateProvider, $urlRouterProvider) {
      
//	    $urlRouterProvider.otherwise( function($injector, $location) {
//            var $state = $injector.get("$state");
//            $state.go("streampipes");
//        });
	
    	$urlRouterProvider.otherwise("/streampipes");
    	
	    $stateProvider
	        .state('streampipes', {
	            url: '/streampipes',
	            views: {
	              "top" : {
	            	  templateUrl : "top.html"
	              	},
	              "container" : {
		            	  templateUrl : "streampipes.html",
		            	  controller: 'AppCtrl'
		           	},
		            "streampipesView@streampipes" : {
		            	  templateUrl : "modules/editor/editor.html",
		            	  controller: 'EditorCtrl',
	            		  resolve:{
	                          'AuthData':function(authService){
	                            return authService.authenticate;
	                          }
	            		  }
		            }
	              }
	          })
	          .state('streampipes.edit', {
	            url: '/editor/:pipeline',
	            views: {
	              "top" : {
	            	  templateUrl : "top.html"
	              	},
	              "container" : {
		            	  templateUrl : "streampipes.html",
		            	  controller: 'AppCtrl'
		           	},
		            "streampipesView@streampipes" : {
		            	  templateUrl : "modules/editor/editor.html",
		            	  controller: 'EditorCtrl'
		           	},
	              }
	          })
	          .state('streampipes.login', {
	            url: '/login',
	            views: {
		            "streampipesView@streampipes" : {
		            	  templateUrl : 'login.html',
		            	  controller: 'LoginCtrl'
		            }
	            }
	          })
	          .state('streampipes.pipelines', {
	            url: '/pipelines',
	            views: {
		            "streampipesView@streampipes" : {
		            	  templateUrl : 'modules/pipelines/pipelines.html',
		            	  controller: 'PipelineCtrl'
		            }
	            }
	          })
	          .state('streampipes.visualizations', {
	            url: '/visualizations',
	            views: {
		            "streampipesView@streampipes" : {
		            	  templateUrl : 'modules/visualizations/visualizations.html',
		            	  controller: 'AppCtrl'
		            }
	            }
	          })
	           .state('streampipes.marketplace', {
	            url: '/marketplace',
	            views: {
		            "streampipesView@streampipes" : {
		            	  templateUrl : 'modules/marketplace/marketplace.html',
		            	  controller: 'MarketplaceCtrl'
		            }
	            }
	          })
	           .state('streampipes.ontology', {
	            url: '/ontology',
	            views: {
		            "streampipesView@streampipes" : {
		            	  templateUrl : 'ontology.html',
		            	  controller: 'AppCtrl'
		            }
	            }
	          })
	          .state('streampipes.myelements', {
	            url: '/myelements',
	            views: {
		            "streampipesView@streampipes" : {
		            	  templateUrl : 'modules/myelements/myelements.html',
		            	  controller: 'MyElementsCtrl'
		            }
	            }
	          })
	          .state('streampipes.tutorial', {
	            url: '/tutorial',
	            views: {
		            "streampipesView@streampipes" : {
		            	  templateUrl : 'tutorial.html',
		            	  controller: 'AppCtrl'
		            }
	            }
	          })
	           .state('streampipes.register', {
	            url: '/register',
	            views: {
		            "streampipesView@streampipes" : {
		            	  templateUrl : 'register.html',
		            	  controller: 'RegisterCtrl'
		            }
	            }
	          })
	           .state('streampipes.add', {
	            url: '/add',
	            views: {
		            "streampipesView@streampipes" : {
		            	  templateUrl : 'modules/add/add.html',
		            	  controller: 'AddCtrl'
		            }
	            }
	          })
	          .state('streampipes.setup', {
	            url: '/setup',
	            views: {
		            "streampipesView@streampipes" : {
		            	  templateUrl : 'setup.html',
		            	  controller: 'SetupCtrl'
		            }
	            }
	          })
	          .state('streampipes.error', {
	            url: '/error',
	            views: {
		            "streampipesView@streampipes" : {
		            	  templateUrl : 'error.html',
		            	  controller: 'SetupCtrl'
		            }
	            }
	          })
	          .state('streampipes.settings', {
	            url: '/settings',
	            views: {
		            "streampipesView@streampipes" : {
		            	  templateUrl : 'settings.html',
		            	  controller: 'SettingsCtrl'
		            }
	            }
	          })
	          .state('streampipes.create', {
	            url: '/create',
	            views: {
		            "streampipesView@streampipes" : {
		            	  templateUrl : 'modules/create/create.html',
		            	  controller: 'CreateCtrl'
		            }
	            }
	          })
	          .state('streampipes.notifications', {
	            url: '/notifications',
	            views: {
		            "streampipesView@streampipes" : {
		            	  templateUrl : 'modules/notifications/notifications.html',
		            	  controller: 'RecommendationCtrl'
		            }
	            }
	          })
	           .state('streamstory', {
	            url: '/streamstory',
	            views: {
	            	 "top" : {
		            	  templateUrl : "top.html",
		              	},
		            "container" : {
		            	  templateUrl : 'streamstory.html',
			              controller : "TopNavCtrl"
		            }
	            }
	          })
	          .state('pandda', {        	  
	            url: '/pandda',
	            views: {
	            	 "top" : {
		            	  templateUrl : "top.html",
		              	},
		            "container" : {
		            	  templateUrl : 'pandda.html',
			            	  controller : "TopNavCtrl"
		            }
	            }
	          })
	          .state('hippo', {
	            url: '/hippo',
	            views: {
	            	 "top" : {
		            	  templateUrl : "top.html",
		              	},
		            "container" : {
		            	  templateUrl : 'hippo.html',
		            	  controller : "TopNavCtrl"
		            }
	            }
	          })
	        
    })
    .controller('TopNavCtrl', function($scope, $rootScope, restApi, $sce) {
    	
    	$scope.panddaUrl = "";
    	$scope.streamStoryUrl = "";
    	$scope.hippoUrl = "";
    	
    	$scope.trustSrc = function(src) {
    	    return $sce.trustAsResourceUrl(src);
    	  }
    	
    	$scope.loadConfig = function() {
			restApi.getConfiguration().success(function(msg) {
				$scope.panddaUrl = msg.panddaUrl;
		    	$scope.streamStoryUrl = msg.streamStoryUrl;
		    	$scope.hippoUrl = msg.hippoUrl;
			});
		}
    	
    	$scope.loadConfig();
    	
    	
    })
    .controller('AppCtrl', function ($rootScope, $scope, $q, $timeout, $mdSidenav, $mdUtil, $log, $location, $http, $cookies, $cookieStore, restApi, $state) {
       
    	$rootScope.unreadNotifications = [];
    	
    	$scope.toggleLeft = buildToggler('left');
    	$rootScope.userInfo = {
    			Name : "D",
    			Avatar : null
    	};
    	    
		$rootScope.go = function ( path ) {
			  //$location.path( path );
			$state.go(path);
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
           },
           {
               link : 'streampipes.marketplace',
               title: 'Marketplace',
               icon: 'maps:ic_local_mall_24px'
           }
         ];
         $scope.admin = [
           {
             link : 'streampipes.myelements',
             title: 'My Elements',
             icon: 'image:ic_portrait_24px'
           },
           {
               link : 'streampipes.add',
               title: 'Add element',
               icon: 'content:ic_add_24px'
           },
           {
               link : 'streampipes.create',
               title: 'Create element',
               icon: 'content:ic_add_24px'
           },
           {
               link : 'streampipes.ontology',
               title: 'Ontology Editor',
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

	.controller('LoginCtrl', function($rootScope, $scope, $timeout, $log, $location, $http, $state) {
		
		$scope.loading = false;
		$scope.authenticationFailed = false;
		
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
				            $state.go("streampipes");
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
    }])
    .service('authService', function($http, $rootScope, $location, $state) {
    	
    	var promise = $http.get("/semantic-epa-backend/api/v2/admin/authc")
        .then(
  	          function(response) {
  	        	if (response.data.success == false) 
          		{
          			$rootScope.authenticated = false;
          			$http.get("/semantic-epa-backend/api/v2/setup/configured")
          			.then(function(response) {
          				if (response.data.configured) $state.go("streampipes.login")//$location.path("/login");
          				else $state.go("streampipes.setup")
          			})		
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
  			    $http.get("/semantic-epa-backend/api/v2/setup/configured")
      			.then(function(conf) {
      				if (conf.data.configured) $state.go("streampipes.login")
      				else $state.go("streampipes.setup")
      			})		
  	          });
        
    	return {
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
	    	    url: urlBase() + "/sepas/favorites/" +encodeURIComponent(elementUri)
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
	    	    url: urlBase() + "/sources/favorites/" +encodeURIComponent(elementUri)
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

		restApi.getPipelineById = function(pipelineId) {
			return $http.get(urlBase() + "/pipelines/" + pipelineId);
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
	    
	    restApi.getSepaById = function(elementId) {
	 	    	return $http.get(urlBase() +"/sepas/" +encodeURIComponent(elementId));
	    }
	    
	    restApi.getActionById = function(elementId) {
 	    	return $http.get(urlBase() +"/actions/" +encodeURIComponent(elementId));
    }
	
	    return restApi;
	}]);
