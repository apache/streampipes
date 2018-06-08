import { HomeComponent } from '../home/home.component';
import { ConfigurationComponent } from '../configuration/configuration.component';
import { AppContainerModule } from '../app-container/app-container.module';
import { AppContainerComponent } from '../app-container/app-container.component';
import { PipelineLogsComponent } from '../pipeline-logs/pipeline-logs.component';
import { NewComponent } from '../connect/new-adapter/new.component';
import { KviComponent } from '../kvi/kvi.component';
import { MainComponent } from '../connect/main/main.component';
import { KviVisualizationComponent } from '../kvi-visualization/kvi-visualization.component';

export default function stateConfig($stateProvider, $urlRouterProvider) {

    //	    $urlRouterProvider.otherwise( function($injector, $location) {
    //            var $state = $injector.get("$state");
    //            $state.go("streampipes");
    //        });

    $urlRouterProvider.otherwise('/streampipes');

    var spNavbar = {
        templateUrl: '../../assets/templates/navbar.html',
        controller: 'AppCtrl'
    };

    var spIconBar = {
        templateUrl: '../../assets/templates/iconbar.html',
        controller: 'AppCtrl'
    };

    var container = {
        templateUrl: '../../assets/templates/streampipes.html',
        controller: 'AppCtrl'
    };

    $stateProvider
        .state('streampipes', {
            url: '/streampipes',
            views: {
                'container': container,
                'spNavbar@streampipes': spNavbar,
                'spIconBar@streampipes': spIconBar,
                'spMain@streampipes': {
                    component: HomeComponent
                }
            },
            resolve: {
                "authData": function (AuthService) {
                    return AuthService.authenticate();
                }
            }
        })
        .state('streampipes.pipelineDetails', {
            url: '/pipelines/:pipeline/details',
            views: {
                'spMain@streampipes': {
                    templateUrl: '../pipeline-details/pipeline-details.html',
                    controller: 'PipelineDetailsCtrl'
                }
            }
        })
        .state('login', {
            url: '/login/:target?session',
            params: {target: null},
            views: {
                'container': {
                    templateUrl: '../../assets/templates/login.html',
                    controller: 'LoginCtrl'
                }
            }
        })
        /*
        .state('streampipes.applinks', {
            url: '/applinks',
            views: {
                "spMain@streampipes": {
                    templateUrl: 'app/applinks/applinks.tmpl.html',
                    controller: 'AppLinksCtrl'
                }
            }
        })
        */
        .state('register', {
            url: '/register',
            views: {
                'container': {
                    templateUrl: '../../assets/templates/register.html',
                    controller: 'RegisterCtrl'
                }
            }
        })
        .state('setup', {
            url: '/setup',
            views: {
                'container': {
                    templateUrl: '../../assets/templates/setup.html',
                    controller: 'SetupCtrl'
                }
            }
        })
        .state('streampipes.error', {
            url: '/error',
            views: {
                'spMain@streampipes': {
                    templateUrl: '../../assets/templates/error.html',
                    controller: 'SetupCtrl'
                }
            }
        })
        .state('streampipes.notifications', {
            url: '/notifications',
            views: {
                'spMain@streampipes': {
                    templateUrl: '../notifications/notifications.html',
                    controller: 'NotificationsCtrl'
                }
            }
        })
        .state('streampipes.editor', {
            url: '/editor/:pipeline',
            params: {pipeline: null},
            views: {
                'spMain@streampipes': {
                    templateUrl: '../editor/editor.html',
                    controller: 'EditorCtrl'
                }
            }
        }).state('streampipes.pipelines', {
        url: '/pipelines/:pipeline',
        params: {pipeline: null},
        views: {
            'spMain@streampipes': {
                templateUrl: '../pipelines/pipelines.html',
                controller: 'PipelineCtrl'
            }
        }
    }).state('streampipes.dashboard', {
        url: '/dashboard',
        views: {
            'spMain@streampipes': {
                templateUrl: '../dashboard/dashboard.html',
                controller: 'DashboardCtrl'
            }
        }
    }).state('streampipes.appfiledownload', {
        url: '/appfiledownload',
        views: {
            'spMain@streampipes': {
                templateUrl: '../app-file-download/app-file-download.tmpl.html',
                controller: 'AppFileDownloadCtrl'
            }
        }
    }).state('streampipes.ontology', {
        url: '',
        views: {
            'spMain@streampipes': {
                templateUrl: '../ontology/ontology.html',
                controller: 'OntologyCtrl'
            }
        }
    })
        .state('streampipes.sensors', {
            url: '',
            views: {
                'spMain@streampipes': {
                    templateUrl: '../sensors/sensors.html',
                    controller: 'SensorsCtrl'
                }
            }
        }).state('streampipes.add', {
        url: '/add',
        views: {
            'spMain@streampipes': {
                templateUrl: '../add/add.html',
                controller: 'AddCtrl'
            }
        }
    })
        .state('streampipes.myelements', {
            url: '/myelements',
            views: {
                'spMain@streampipes': {
                    templateUrl: '../myelements/myelements.html',
                    controller: 'MyElementsCtrl'
                }
            }
        })
        .state('streampipes.configuration', {
            url: '/configuration',
            views: {
                'spMain@streampipes': {
                    component: ConfigurationComponent
                }
            }
        })
        .state('streampipes.appcontainer', {
            url: '/appcontainer',
            views: {
                'spMain@streampipes': {
                    component: AppContainerComponent
                }
            }
        })
        .state('streampipes.pipelinelogs', {
            url: '/pipelines/:pipeline/logs',
            views: {
                'spMain@streampipes': {
                    component: PipelineLogsComponent
                }
            }
        })
        .state('streampipes.connect', {
            url: '/connect',
            views: {
                'spMain@streampipes': {
                    component: MainComponent
                }
            }
        })
        .state('streampipes.kvi', {
            url: '/kvi',
            views: {
                'spMain@streampipes': {
                    component: KviComponent
                }
            }
        })
        .state('streampipes.kvivisualization', {
            url: '/kvivisualization',
            views: {
                'spMain@streampipes': {
                    component: KviVisualizationComponent
                }
            }
        });
};
