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
import spProasenseHome from './proasense-home/proasense-home.module';
import spSensors from './sensors/sensors.module';

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
        spProasenseHome,
        spSensors,
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
                    $state.go("streampipes.setup");
                }
            });
        }


        $rootScope.$on('$stateChangeStart',
            function (event, toState, toParams, fromState, fromParams) {
                var isLogin = toState.name === "streampipes.login";
                var isSetup = toState.name === "streampipes.setup";
                var isExternalLogin = (toState.name === "sso" || toState.name === "ssosuccess");
                var isRegister = toState.name === "streampipes.register";
                if (isLogin || isSetup || isRegister || isExternalLogin) {
                    return;
                }
                else if ($rootScope.authenticated === false) {
                    event.preventDefault();
                    console.log("logging in event prevent");
                    $state.go('streampipes.login');
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

    });
