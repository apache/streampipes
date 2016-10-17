stateConfig.$inject = ['$stateProvider', '$urlRouterProvider'];

export default function stateConfig($stateProvider, $urlRouterProvider) {

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
					templateUrl : "templates/top.html",	
					controller: "TopNavCtrl"
				},
				"container" : {
					templateUrl : "templates/streampipes.html",
					controller: 'AppCtrl'
				},
				"streampipesView@streampipes" : {
					templateUrl : "app/editor/editor.html",
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
					templateUrl : "templates/top.html",
					controller : "TopNavCtrl"
				},
				"container" : {
					templateUrl : "templates/streampipes.html",
					controller: 'AppCtrl'
				},
				"streampipesView@streampipes" : {
					templateUrl : "app/editor/editor.html",
					controller: 'EditorCtrl'
				}
			}
		})
		.state('sso', {
			url: '/sso/:target?session',
			views: {
				"container" : {
					templateUrl : 'templates/sso.html',
					controller: 'SsoCtrl'
				}
			}
		})
		.state('ssosuccess', {
			url: '/ssosuccess',
			views: {
				"container" : {
					templateUrl : 'templates/sso.html',
				}
			}
		})
		.state('streampipes.login', {
			url: '/login/:target?session',
			views: {
				"streampipesView@streampipes" : {
					templateUrl : 'templates/login.html',
					controller: 'LoginCtrl'
				}
			}
		})
		.state('home', {
			url: '/home',
			views: {
				"top" : {
					templateUrl : "templates/top.html",
					controller : "TopNavCtrl"
				},
				"container" : {
					templateUrl : "app/proasense-home/home.html",
					controller: 'HomeCtrl'
				}
			}
		})
		.state('streampipes.pipelines', {
			url: '/pipelines/:pipeline',
			views: {
				"streampipesView@streampipes" : {
					templateUrl : 'app/pipelines/pipelines.html',
					controller: 'PipelineCtrl'
				}
			}
		})
		.state('streampipes.dashboard', {
			url: '/dashboard',
			views: {
				"streampipesView@streampipes" : {
					templateUrl : 'app/dashboard/dashboard.html',
					controller: 'DashboardCtrl'
				}
			}
		})
	//			.state('streampipes.marketplace', {
	//				url: '/marketplace',
	//				views: {
	//					"streampipesView@streampipes" : {
	//						templateUrl : 'modules/marketplace/marketplace.html',
	//						controller: 'MarketplaceCtrl'
	//					}
	//				}
	//			})
		.state('streampipes.ontology', {
			url: '/ontology',
			views: {
				"streampipesView@streampipes" : {
					templateUrl : 'app/ontology/ontology.html',
					controller: 'OntologyCtrl'
				}
			}
		})
		.state('streampipes.sensors', {
			url: '/sensors',
			views: {
				"streampipesView@streampipes" : {
					templateUrl : 'app/sensors/sensors.html',
					controller: 'SensorsCtrl'
				}
			}
		})
		.state('streampipes.myelements', {
			url: '/myelements',
			views: {
				"streampipesView@streampipes" : {
					templateUrl : 'app/myelements/myelements.html',
					controller: 'MyElementsCtrl'
				}
			}
		})
		.state('streampipes.applinks', {
			url: '/applinks',
			views: {
				"streampipesView@streampipes" : {
					templateUrl : 'app/applinks/applinks.tmpl.html',
					controller: 'AppLinksCtrl'
				}
			}
		})
		.state('streampipes.tutorial', {
			url: '/tutorial',
			views: {
				"streampipesView@streampipes" : {
					templateUrl : 'templates/tutorial.html',
					controller: 'AppCtrl'
				}
			}
		})
		.state('streampipes.register', {
			url: '/register',
			views: {
				"streampipesView@streampipes" : {
					templateUrl : 'templates/register.html',
					controller: 'RegisterCtrl'
				}
			}
		})
		.state('streampipes.add', {
			url: '/add',
			views: {
				"streampipesView@streampipes" : {
					templateUrl : 'app/add/add.html',
					controller: 'AddCtrl'
				}
			}
		})
		.state('streampipes.documentation', {
			url: '/docs',
			views: {
				"streampipesView@streampipes" : {
					templateUrl : 'app/docs/docs.html',
					controller: 'DocsCtrl'
				}
			}
		})
		.state('streampipes.setup', {
			url: '/setup',
			views: {
				"streampipesView@streampipes" : {
					templateUrl : 'templates/setup.html',
					controller: 'SetupCtrl'
				}
			}
		})
		.state('streampipes.error', {
			url: '/error',
			views: {
				"streampipesView@streampipes" : {
					templateUrl : 'templates/error.html',
					controller: 'SetupCtrl'
				}
			}
		})
		.state('streampipes.settings', {
			url: '/settings',
			views: {
				"streampipesView@streampipes" : {
					templateUrl : 'templates/settings.html',
					controller: 'SettingsCtrl'
				}
			}
		})
		.state('streampipes.notifications', {
			url: '/notifications',
			views: {
				"streampipesView@streampipes" : {
					templateUrl : 'app/notifications/notifications.html',
					controller: 'NotificationsCtrl'
				}
			}
		})
		.state('streamstory', {
			url: '/streamstory',
			views: {
				"top" : {
					templateUrl : "templates/top.html",
					controller : "TopNavCtrl"
				},
				"container" : {
					templateUrl : 'templates/streamstory.html',
					controller: "TopNavCtrl"
				}
			}
		})
		.state('pandda', {        	  
			url: '/pandda',
			views: {
				"top" : {
					templateUrl : "templates/top.html",
					controller : "TopNavCtrl"
				},
				"container" : {
					templateUrl : 'templates/pandda.html',
					controller: "TopNavCtrl"
				}
			}
		})
		.state('hippo', {
			url: '/hippo',
			views: {
				"top" : {
					templateUrl : "templates/top.html",
					controller : "TopNavCtrl"
				},
				"container" : {
					templateUrl : 'templates/hippo.html',
					controller: "TopNavCtrl"
				}
			}
		}).state('inspectionReport', {
			url: '/inspection',
			views: {
				"top" : {
					templateUrl : "templates/top.html",
					controller : "TopNavCtrl"
				},
				"container" : {
					templateUrl : 'templates/humaninspectionreport.html',
					controller: "TopNavCtrl"
				}
			}
		}).state('maintenanceReport', {
			url: '/maintenance',
			views: {
				"top" : {
					templateUrl : "templates/top.html",
					controller : "TopNavCtrl"
				},
				"container" : {
					templateUrl : 'templates/humanmaintenancereport.html',
					controller: "TopNavCtrl"
				}
			}
		});
};
