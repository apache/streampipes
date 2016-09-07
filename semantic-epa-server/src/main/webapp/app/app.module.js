'use strict';

import angular from 'npm/angular';
//import jQuery from 'npm/jquery';
//

//import ngMaterial from 'npm/angular-material';
//import ngMdIcons from 'npm/angular-material-icons';
//import ngRoute from 'npm/angular-route';
//import ngCookies from 'npm/angular-cookies';
//import angularLoadingBar from 'npm/angular-loading-bar';
//import useravatar from 'lib/useravatar';
//import schemaForm from 'npm/angular-schema-form';
import uiRouter from 'npm/angular-ui-router';
//import ngPrettyJson from 'npm/ng-prettyjson';
//import uiTree from 'npm/angular-ui-tree';
//import ng-context-menu from '';
//import ngFileUpload from 'npm/ng-file-upload';
//import duScroll from 'npm/angular-scroll';
//import angularjs-dropdown-multiselect from '';
//import rtPopup from 'npm/angular-rt-popup';
//import angularClipboard from 'npm/angular-clipboard';
//import ngSanitize from 'npm/angular-sanitize';
//import btfordMarkdown from 'npm/angular-markdown-directive';

import spServices from './services/services.module';
import delme from './delme';

import spAdd from './add/add.module';
import spCore from './core/core.module';
import spCreate from './create/create.module';
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
import spVisualizationNew from './visualizations-new/visualizations-new.module';

//import restApi from './services/rest-api.service'
//import authService from './services/auth.service'
//import spServices from './services/services.module'

const MODULE_NAME = 'streamPipesApp';

export default angular
	.module(MODULE_NAME, [
		//'ngMaterial', 
		//'ngMdIcons', 
		delme,
		spServices,
		spAdd,
		spCore,
		spDocs,
		spEditor,
		spLayout,
		spLogin,
		spMyElements,
		spNotifications,
		spOntology,
		spPipelines,
		spProasenseHome ,
		spSensors,
		spVisualizationNew,
		//'spConstants',
		//'sp-services',
		//'ngRoute', 
		//'ngCookies', 
		//'angularLoadingBar', 
		//'useravatar', 
		//'schemaForm', 
		uiRouter, 
		//'ngPrettyJson', 
		//'uiTree', 
		//'ng-context-menu', 
		//'ngFileUpload', 
		//'duScroll', 
		//'angularjs-dropdown-multiselect', 
		//'rtPopup', 
		//'angularClipboard',
		//'ngSanitize',
		//'btfordMarkdown'
	])
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
				var isLogin = toState.name === "streampipes.login";
				var isSetup = toState.name === "streampipes.setup";
				var isExternalLogin = (toState.name === "sso" || toState.name === "ssosuccess");
				var isRegister = toState.name === "streampipes.register";
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

	});
