stateConfig.$inject = ['$stateProvider', '$urlRouterProvider'];

export default function stateConfig($stateProvider, $urlRouterProvider) {

    //	    $urlRouterProvider.otherwise( function($injector, $location) {
    //            var $state = $injector.get("$state");
    //            $state.go("streampipes");
    //        });

    $urlRouterProvider.otherwise("/streampipes");

    var spNavbar = {
        templateUrl: "templates/navbar.html",
        controller: 'AppCtrl'
    }

    var spIconBar = {
        templateUrl: "templates/iconbar.html",
        controller:'AppCtrl'
    }

    var container = {
        templateUrl: "templates/streampipes.html",
        controller: 'AppCtrl'
    }

    $stateProvider
        .state('streampipes', {
            url: '/streampipes',
            views: {
                "container" : container,
                "spNavbar@streampipes": spNavbar,
                "spIconBar@streampipes" : spIconBar,
                "spMain@streampipes": {
                    templateUrl: "app/editor/editor.html",
                    controller: 'EditorCtrl',
                    resolve: {
                        'AuthData': function (authService) {
                            return authService.authenticate;
                        }
                    }
                }
            }
        })
        .state('streampipes.edit', {
            url: '/editor/:pipeline',
            views: {
                "spMain@streampipes": {
                    templateUrl: "app/editor/editor.html",
                    controller: 'EditorCtrl'
                }
            }
        })
        .state('streampipes.pipelineDetails', {
            url: '/pipelines/:pipeline/details',
            views: {
                "spMain@streampipes": {
                    templateUrl: "app/pipeline-details/pipeline-details.html",
                    controller: 'PipelineDetailsCtrl'
                }
            }
        })
        .state('login', {
            url: '/login/:target?session',
            views: {
                "container": {
                    templateUrl: 'templates/login.html',
                    controller: 'LoginCtrl'
                }
            }
        })
        .state('streampipes.pipelines', {
            url: '/pipelines/:pipeline',
            views: {
                "spMain@streampipes": {
                    templateUrl: 'app/pipelines/pipelines.html',
                    controller: 'PipelineCtrl'
                }
            }
        })
        .state('streampipes.dashboard', {
            url: '/dashboard',
            views: {
                "spMain@streampipes": {
                    templateUrl: 'app/dashboard/dashboard.html',
                    controller: 'DashboardCtrl'
                }
            }
        })
        .state('streampipes.ontology', {
            url: '/ontology',
            views: {
                "spMain@streampipes": {
                    templateUrl: 'app/ontology/ontology.html',
                    controller: 'OntologyCtrl'
                }
            }
        })
        .state('streampipes.sensors', {
            url: '/sensors',
            views: {
                "spMain@streampipes": {
                    templateUrl: 'app/sensors/sensors.html',
                    controller: 'SensorsCtrl'
                }
            }
        })
        .state('streampipes.myelements', {
            url: '/myelements',
            views: {
                "spMain@streampipes": {
                    templateUrl: 'app/myelements/myelements.html',
                    controller: 'MyElementsCtrl'
                }
            }
        })
        .state('streampipes.applinks', {
            url: '/applinks',
            views: {
                "spMain@streampipes": {
                    templateUrl: 'app/applinks/applinks.tmpl.html',
                    controller: 'AppLinksCtrl'
                }
            }
        })
        .state('streampipes.tutorial', {
            url: '/tutorial',
            views: {
                "spMain@streampipes": {
                    templateUrl: 'app/tutorial/tutorial.html',
                    controller: 'TutorialCtrl'
                }
            }
        })
        .state('register', {
            url: '/register',
            views: {
                "container": {
                    templateUrl: 'templates/register.html',
                    controller: 'RegisterCtrl'
                }
            }
        })
        .state('streampipes.add', {
            url: '/add',
            views: {
                "spMain@streampipes": {
                    templateUrl: 'app/add/add.html',
                    controller: 'AddCtrl'
                }
            }
        })
        .state('streamppipes.documentation', {
            url: '/docs',
            views: {
                "spMain@streampipes": {
                    templateUrl: 'app/docs/docs.html',
                    controller: 'DocsCtrl'
                }
            }
        })
        .state('setup', {
            url: '/setup',
            views: {
                "container": {
                    templateUrl: 'templates/setup.html',
                    controller: 'SetupCtrl'
                }
            }
        })
        .state('streampipes.error', {
            url: '/error',
            views: {
                "spMain@streampipes": {
                    templateUrl: 'templates/error.html',
                    controller: 'SetupCtrl'
                }
            }
        })
        .state('streampipes.settings', {
            url: '/settings',
            views: {
                "spMain@streampipes": {
                    templateUrl: 'templates/settings.html',
                    controller: 'SettingsCtrl'
                }
            }
        })
        .state('streampipes.notifications', {
            url: '/notifications',
            views: {
                "spMain@streampipes": {
                    templateUrl: 'app/notifications/notifications.html',
                    controller: 'NotificationsCtrl'
                }
            }
        });
};
