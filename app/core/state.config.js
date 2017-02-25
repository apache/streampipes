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

    $stateProvider
        .state('streampipes', {
            url: '/streampipes',
            views: {
                "spNavbar": spNavbar,
                "spIconBar" : spIconBar,
                "spMain": {
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
        .state('edit', {
            url: '/editor/:pipeline',
            views: {
                "spNavbar": spNavbar,
                "spIconBar" : spIconBar,
                "spMain": {
                    templateUrl: "app/editor/editor.html",
                    controller: 'EditorCtrl'
                }
            }
        })
        .state('login', {
            url: '/login/:target?session',
            views: {
                "spNavbar": spNavbar,
                "spIconBar" : spIconBar,
                "spMain": {
                    templateUrl: 'templates/login.html',
                    controller: 'LoginCtrl'
                }
            }
        })
        .state('pipelines', {
            url: '/pipelines/:pipeline',
            views: {
                "spNavbar": spNavbar,
                "spIconBar" : spIconBar,
                "spMain": {
                    templateUrl: 'app/pipelines/pipelines.html',
                    controller: 'PipelineCtrl'
                }
            }
        })
        .state('dashboard', {
            url: '/dashboard',
            views: {
                "spNavbar": spNavbar,
                "spIconBar" : spIconBar,
                "spMain": {
                    templateUrl: 'app/dashboard/dashboard.html',
                    controller: 'DashboardCtrl'
                }
            }
        })
        .state('ontology', {
            url: '/ontology',
            views: {
                "spNavbar": spNavbar,
                "spIconBar" : spIconBar,
                "spMain": {
                    templateUrl: 'app/ontology/ontology.html',
                    controller: 'OntologyCtrl'
                }
            }
        })
        .state('sensors', {
            url: '/sensors',
            views: {
                "spNavbar": spNavbar,
                "spIconBar" : spIconBar,
                "spMain": {
                    templateUrl: 'app/sensors/sensors.html',
                    controller: 'SensorsCtrl'
                }
            }
        })
        .state('myelements', {
            url: '/myelements',
            views: {
                "spNavbar": spNavbar,
                "spIconBar" : spIconBar,
                "spMain": {
                    templateUrl: 'app/myelements/myelements.html',
                    controller: 'MyElementsCtrl'
                }
            }
        })
        .state('applinks', {
            url: '/applinks',
            views: {
                "spNavbar": spNavbar,
                "spIconBar" : spIconBar,
                "spMain": {
                    templateUrl: 'app/applinks/applinks.tmpl.html',
                    controller: 'AppLinksCtrl'
                }
            }
        })
        .state('tutorial', {
            url: '/tutorial',
            views: {
                "spNavbar": spNavbar,
                "spIconBar" : spIconBar,
                "spMain": {
                    templateUrl: 'app/tutorial/tutorial.html',
                    controller: 'TutorialCtrl'
                }
            }
        })
        .state('register', {
            url: '/register',
            views: {
                "spNavbar": spNavbar,
                "spIconBar" : spIconBar,
                "spMain": {
                    templateUrl: 'templates/register.html',
                    controller: 'RegisterCtrl'
                }
            }
        })
        .state('add', {
            url: '/add',
            views: {
                "spNavbar": spNavbar,
                "spIconBar" : spIconBar,
                "spMain": {
                    templateUrl: 'app/add/add.html',
                    controller: 'AddCtrl'
                }
            }
        })
        .state('documentation', {
            url: '/docs',
            views: {
                "spNavbar": spNavbar,
                "spIconBar" : spIconBar,
                "spMain": {
                    templateUrl: 'app/docs/docs.html',
                    controller: 'DocsCtrl'
                }
            }
        })
        .state('setup', {
            url: '/setup',
            views: {
                "spNavbar": spNavbar,
                "spIconBar" : spIconBar,
                "spMain": {
                    templateUrl: 'templates/setup.html',
                    controller: 'SetupCtrl'
                }
            }
        })
        .state('error', {
            url: '/error',
            views: {
                "spNavbar": spNavbar,
                "spIconBar" : spIconBar,
                "spMain": {
                    templateUrl: 'templates/error.html',
                    controller: 'SetupCtrl'
                }
            }
        })
        .state('settings', {
            url: '/settings',
            views: {
                "spNavbar": spNavbar,
                "spIconBar" : spIconBar,
                "spMain": {
                    templateUrl: 'templates/settings.html',
                    controller: 'SettingsCtrl'
                }
            }
        })
        .state('notifications', {
            url: '/notifications',
            views: {
                "spNavbar": spNavbar,
                "spIconBar" : spIconBar,
                "spMain": {
                    templateUrl: 'app/notifications/notifications.html',
                    controller: 'NotificationsCtrl'
                }
            }
        });
};
