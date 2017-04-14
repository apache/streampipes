'use strict';

import angular from 'npm/angular';

import "jquery-ui";

import uiRouter from 'npm/angular-ui-router';

import spServices from './services/services.module';

import spAdd from './add/add.module';
import spCore from './core/core.module';
import spCreate from './create/create.module';
import spDashboard from './dashboard/dashboard.module';
import spDocs from './docs/docs.module';
import spEditor from './editor/editor.module';
import spLayout from './layout/layout.module';
import spLogin from './login/login.module';
import spMyElements from './myelements/my-elements.module';
import spNotifications from './notifications/notifications.module';
import spOntology from './ontology/ontology.module';
import spPipelines from './pipelines/pipelines.module';
import spSensors from './sensors/sensors.module';
import spAppLinks from './applinks/applinks.module';
import spTutorial from './tutorial/tutorial.module';
import spPipelineDetails from './pipeline-details/pipeline-details.module'

const MODULE_NAME = 'streamPipesApp';

export default angular
    .module(MODULE_NAME, [
        spServices,
        spAdd,
        spCore,
        spCreate,
        spDashboard,
        spDocs,
        spEditor,
        spLayout,
        spLogin,
        spMyElements,
        spNotifications,
        spOntology,
        spPipelines,
        spSensors,
        spTutorial,
        spAppLinks,
        spPipelineDetails,
        uiRouter,
    ])
    .run(function ($rootScope, $location, restApi, authService, $state, $urlRouter, objectProvider) {
        var bypass;
        window.loading_screen.finish();
        if (!$location.path().startsWith("/login") && !$location.path().startsWith("/sso")) {
            restApi.configured().success(function (msg) {
                if (msg.configured) {
                    authService.authenticate;
                }
                else {
                    $rootScope.authenticated = false;
                    $state.go("setup");
                }
            });
        }


        $rootScope.$on('$stateChangeStart',
            function (event, toState, toParams, fromState, fromParams) {
                console.log(toState.name);
                var isLogin = toState.name === "login";
                var isSetup = toState.name === "setup";
                var isExternalLogin = (toState.name === "sso" || toState.name === "ssosuccess");
                var isRegister = toState.name === "register";
                if (isLogin || isSetup || isRegister || isExternalLogin) {
                    return;
                }
                else if ($rootScope.authenticated === false) {
                    event.preventDefault();
                    console.log("logging in event prevent");
                    $state.go('login');
                }

            })

        $rootScope.$on("$routeChangeStart", function (event, nextRoute, currentRoute) {
            authService.authenticate;
        });
        $rootScope.state = new objectProvider.State();
        $rootScope.state.sources = false;
        $rootScope.state.sepas = false;
        $rootScope.state.actions = false;
        $rootScope.state.adjustingPipelineState = false;
        $rootScope.state.adjustingPipeline = {};

    }).config(function($mdThemingProvider) {

        $mdThemingProvider.definePalette('streamPipesPrimary', {
            '50': '304269',
            '100': '304269',
            '200': '304269',
            '300': '304269',
            '400': '304269',
            '500': '304269',
            '600': '304269',
            '700': '003B3D',
            '800': '304269',
            '900': '50FFBF',
            'A100': '304269',
            'A200': '304269',
            'A400': '304269',
            'A700': '304269',
            'contrastDefaultColor': 'light',    // whether, by default, text (contrast)
                                                // on this palette should be dark or light

            'contrastDarkColors': ['50', '100', //hues which contrast should be 'dark' by default
                '200', '300', '400', 'A100'],
        });

        $mdThemingProvider.definePalette('streamPipesAccent', {
            '50': 'DF5A49',
            '100': 'DF5A49',
            '200': '007F54',
            '300': '007F54',
            '400': '007F54',
            '500': '007F54',
            '600': 'CC2800',
            '700': 'CC2800',
            '800': 'CC2800',
            '900': 'CC2800',
            'A100': 'CC2800',
            'A200': 'CC2800',
            'A400': 'CC2800',
            'A700': 'CC2800',
            'contrastDefaultColor': 'light',    // whether, by default, text (contrast)
                                                // on this palette should be dark or light

            'contrastDarkColors': ['50', '100', //hues which contrast should be 'dark' by default
                '200', '300', '400', 'A100'],
        });

        $mdThemingProvider.theme('default')
            .primaryPalette('streamPipesPrimary')
            //.accentPalette('streamPipesAccent')

    });
