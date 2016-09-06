'use strict';
import angular from 'npm/angular';
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

import spServices from './services/services.module'
import delme from './delme'

import spCore from './core/core.module'
import spLayout from './layout/layout.module'
import spLogin from './login/login.module'

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
															 spCore,
															 spLayout,
															 spLogin,
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
			console.log(toState.name);
				var isLogin = toState.name === "streampipes.login";
				var isSetup = toState.name === "streampipes.setup";
				var isExternalLogin = (toState.name === "sso" || toState.name === "ssosuccess");
				var isRegister = toState.name === "streampipes.register";
				console.log("Setup: " +isSetup +", Login: " +isLogin);
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

	})
	
//TODO refactor ==================================================================================================
	
	.filter('capitalize', function() {
		return function(input) {
			if (input!=null) {
				console.log(input);
				input = input.replace(/_/g, " ");
						var stringArr = input.split(" ");
						var result = "";
						var cap = stringArr.length;
						for(var x = 0; x < cap; x++) {
							stringArr[x] = stringArr[x].toLowerCase();
							if(x === cap - 1) {
								result += stringArr[x].substring(0,1).toUpperCase() + stringArr[x].substring(1);
							} else {
								result += stringArr[x].substring(0,1).toUpperCase() + stringArr[x].substring(1) + " ";
							}
						}
					return result;
				}
		}
	});
//TODO refactor ==================================================================================================
