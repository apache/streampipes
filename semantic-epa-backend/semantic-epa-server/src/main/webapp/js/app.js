'use strict';

angular
    .module('LandingPage', ['ngMaterial', 'ngMdIcons', 'ngRoute', 'ngCookies', 'angular-loading-bar', 'useravatar', 'schemaForm'])
    .constant("apiConstants", {
        url: "http://localhost",
        port: "8080",
        contextPath : "/semantic-epa-backend",
        api : "/api/v2"
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
            .when('/pipelines', {
                templateUrl : 'pipelines.html',
                controller  : 'PipelineCtrl'
            })
            .when('/', {
                templateUrl : 'editor.html',
                controller  : 'EditorCtrl'
            })
            .when('/visualizations', {
                templateUrl : 'visualizations.html',
                controller  : 'AppCtrl'
            })
            .when('/marketplace', {
                templateUrl : 'marketplace.html',
                controller  : 'MarketplaceCtrl'
            })
             .when('/ontology', {
                templateUrl : 'ontology.html',
                controller  : 'AppCtrl'
            })
            .when('/myelements', {
                templateUrl : 'myelements.html',
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
                templateUrl : 'add.html',
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
                templateUrl : 'create.html',
                controller  : 'CreateCtrl'
            })
	})
    .controller('AppCtrl', function ($rootScope, $scope, $q, $timeout, $mdSidenav, $mdUtil, $log, $location, $http, $cookies, $cookieStore, restApi) {
       
    	$scope.toggleLeft = buildToggler('left');
    	
    	$scope.userInfo = {
    			Name : "Dominik",
    			Avatar : null
    	};
    	    	
		$rootScope.authenticated = function() {
			$http.get("/semantic-epa-backend/api/v2/admin/authc")
	        .then(
	          function(response) {
	        	  console.log(response);
	        	  console.log(response.data.success);
	        	  
	        	if (response.data.success == false) 
	        		{
	        			$rootScope.authenticated = false;
	        			$location.path("/login");
	        		}
	        	else {
	        	$rootScope.username = response.data.info.authc.principal.username;
	        	$rootScope.email = response.data.info.authc.principal.email;
		        $rootScope.authenticated = true;
	          }
	          },
	          function(response) {
	        	  $rootScope.username = undefined;
			      $rootScope.authenticated = false;
		          $location.path("/login");
	          });
		};
		
		$scope.configured = restApi.configured().success(function(msg) {
			if (msg.configured)
				{
					$rootScope.authenticated();
				}
			else {
					$rootScope.authenticated = false;
					$location.path("/setup");
				}
		}).error(function() {
			$location.path("/error");
		});
		
		
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
                    .toggle()
                    .then(function () {
                        $log.debug("toggle " + navID + " is done");
                    });
            },300);
            return debounceFn;
        }
    })
    .controller('LeftCtrl', function ($scope, $timeout, $mdSidenav, $log) {
        $scope.close = function () {
            $mdSidenav('left').close()
                .then(function () {
                    $log.debug("close LEFT is done");
                });
        };
    })
    .controller('EditorCtrl', function ($scope, $timeout, $mdSidenav, $mdUtil, $log) {
		$(init("Proa"));
	})
	.controller('RegisterCtrl', function ($scope, $timeout, $mdSidenav, $mdUtil, $log, $http) {

		$scope.register = function() {
			var payload = {};
			payload.username = $scope.username;
			payload.password = $scope.password;
			payload.email = $scope.email;
			
			$http.post("/semantic-epa-backend/api/v2/admin/register", payload);
		};
	})
	.controller('PipelineCtrl', function ($scope, $timeout, $mdSidenav, $mdUtil, $log) {
		$(refreshPipelines());
		
		//Bind click handler--------------------------------
	    $("#pipelineTableBody").on("click", "tr", function () {
	        if (!$(this).data("active") || $(this).data("active") == undefined) {
	            $(this).data("active", true);
	            $(this).addClass("info");
	            $("#pipelineTableBody").children().not(this).removeClass("info");
	            $("#pipelineTableBody").children().not(this).data("active", false);
	            clearPipelineDisplay();
	            displayPipeline($(this).data("JSON"));
	        } else {

	        }
	    });
	})
	.controller('LoginCtrl', function($rootScope, $scope, $timeout, $log, $location, $http) {
		
		$scope.loading = false;
		
		$scope.logIn = function() {
	    	  $scope.loading = true;
	    	  console.log($scope.credentials);
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
	.controller('MarketplaceCtrl', function($rootScope, $scope, $timeout, $log, $location, $http, restApi) {
		
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
	            	console.log(sepas);
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
			   console.log(type);
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
			   console.log(sepa);
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
			   console.log(source);
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
		   
	})
	.controller('MyElementsCtrl', function($rootScope, $scope, $timeout, $log, $location, $http, restApi, $mdToast, $animate, $mdDialog) {
		
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
			if (type == 'source')  { $scope.loadOwnSources();  }
			else if (type == 'sepa') { $scope.loadOwnSepas();  }
			else if (type == 'action') { $scope.loadOwnActions(); }
		}
		
	    $scope.loadOwnActions = function () {
	        restApi.getOwnActions()
	            .success(function (actions) {
	            	$scope.currentElements = actions;
	            })
	            .error(function (error) {
	                $scope.status = 'Unable to load actions: ' + error.message;
	            });
	    }
	    
	    $scope.loadOwnSepas = function () {
	        restApi.getOwnSepas()
	            .success(function (sepas) {
	            	$scope.currentElements = sepas;
	            })
	            .error(function (error) {
	                $scope.status = 'Unable to load sepas: ' + error.message;
	            });
	    }
	    
	    $scope.loadOwnSources = function () {
	        restApi.getOwnSources()
	            .success(function (sources) {
	            	$scope.currentElements = sources;
	            })
	            .error(function (error) {
	                $scope.status = 'Unable to load sepas: ' + error.message;
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
	    
	   $scope.toggleFavorite = function(element, type) {
		   console.log(type);
		   if (type == 'action') $scope.toggleFavoriteAction(element, type);
		   else if (type == 'source') $scope.toggleFavoriteSource(element, type);
		   else if (type == 'sepa') $scope.toggleFavoriteSepa(element, type);
	   } 
	   
	   $scope.refresh = function(elementUri, type) {
		  restApi.update(elementUri).success(function (msg) {
			  $scope.showToast(msg.notifications[0].title);
		  }).then(function() {
			  $scope.loadCurrentElements(type);
		  })
	   } 
	   
	   $scope.remove = function(elementUri, type) {
			  restApi.del(elementUri).success(function (msg) {
				  $scope.showToast(msg.notifications[0].title);
			  }).then(function() {
				  $scope.loadCurrentElements(type);
			  })
		   } 
	   
	   $scope.jsonld = function(event, elementUri) {
		   restApi.jsonld(elementUri).success(function (msg) {
				  $scope.showAlert(event, elementUri, msg);
			  })
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
		   console.log(sepa);
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
		   console.log(source);
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
	   
	})
	.controller('AddCtrl', function($rootScope, $scope, $timeout, $log, $location, $http, restApi) {
		
		$scope.elements;
		$scope.endpointUrl;
		$scope.endpointData;
		$scope.results = [];
		$scope.loading = false;
		$scope.marketplace = false;
		
		$scope.addFromEndpoint = function () {
			$scope.loading = true;		
			restApi.addBatch($scope.endpointUrl, true)
            .success(function (data) {
            	$scope.loading = false;
            	console.log(data);
            })
		}
		
		$scope.add = function () {
			$scope.loading = true;
	        var uris = $scope.elements.split(" ");
	        $scope.addElements(uris, 0);
		}
		
		$scope.addElements = function (uris, i) {
		    if (i == uris.length) {
		    	$scope.loading = false;
		        return;
		    } else {
		    	var uri = uris[i];		
		    	$scope.results[i] = {};
		    	$scope.results[i].elementId = uri;
		    	$scope.results[i].loading = true;        
		        uri = encodeURIComponent(uri);
		        
		        restApi.add(uri, true)
	            .success(function (data) {
	            	 $scope.results[i].loading = false;
	            	 $scope.results[i].msg = data.notifications[0].description;
	            })
	            .error(function (data) {
	            	 $scope.results[i].loading = false;
	            	 $scope.results[i].msg = data.notifications[0].description;
	            })
	            .then(function () {
		            $scope.addElements(uris, ++i);
		        });
		    }
		}		   
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
	.controller('CreateCtrl', function($rootScope, $scope, $timeout, $log, $location, $http, restApi, $mdToast) {
		
		$scope.schema = {};
		
		
		
		$scope.loadSchema = function() {
			$http.get("http://localhost:8080/semantic-epa-backend/api/v2/users/" +$rootScope.email +"/sources/jsonschema")
	        .then(
	          function(response) {
	        	  $scope.schema = response.data;
	        	  console.log($scope.schema);
	          })
		};
		
		$scope.form = [
		               "name",
		               "description",
		               "iconUrl",
		               {
		            	   "title" : "Event Streams",
		            	   "type" : "array",
		            	   "add" : "Add Event Stream",
		            	   "key": "eventStreams",
		            	   "items": [
		            	             "eventStreams[].name",
		            	             "eventStreams[].description",
		            	             "eventStreams[].iconUrl",
		            	            
		            	             {
		            	            	 "title" : "Event Grounding",
		            	            	 "type": "array",
		            	            	 "key" : "eventStreams[].eventGrounding",
		            	            	 "add": "Add Event Grounding",
		            	                 "remove": "Remove Event Grounding",
		            	            	 "items" : [
		            	            	           
		            	            	            	         "eventStreams[].eventGrounding[].elementName"
		            	            	            	         
		            	            	           ]
		            	            
		            	             
		    		               },
		    		               {
		            	            	 "title" : "Event Properties",
		            	            	 "type": "array",
		            	            	 "key" : "eventStreams[].eventSchema.eventProperties",
		            	            	 "add": "Add Event Properties",
		            	                 "remove": "Remove Event Property",
		            	            	 "items" : [
		            	            	           
		            	            	            	         "eventStreams[].eventSchema.eventProperties[].propertyType"

		            	            	           ]
		            	                         
		    		               }
		            	            ]
		               },
		               
	];

		$scope.model = {};
		$scope.loadSchema();
		
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
	.factory('restApi', ['$rootScope', '$http', 'apiConstants', function($rootScope, $http, apiConstants) {
	    
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
	    	    url: urlBase() + "/actions/favorites/" +encodeURIComponent(elementUri),
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
	    }
	    
	    restApi.getOwnPipelines = function() {
	    	return $http.get(urlBase() +"/pipelines");
	    }
	    
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
	    	return $http.get(urlBase() +"/pipelines" +pipelineId +"/start");
	    }
	    
	    restApi.stopPipeline = function(pipelineId) {
	    	return $http.get(urlBase() +"/pipelines" +pipelineId +"/stop");
	    }
	
	    return restApi;
	}]);
