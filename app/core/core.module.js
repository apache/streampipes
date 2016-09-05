import angular from 'npm/angular';
import uiRouter from 'npm/angular-ui-router';
import ngMdIcons from 'npm/angular-material-icons';
import ngMaterial from 'npm/angular-material';

import stateConfig from './state.config'
import iconProviderConfig from './icon-provider.config'
import httpProviderConfig from './http-provider.config'

export default angular.module('sp.core', [uiRouter, 'ngMaterial','ngMdIcons'])
	.config(stateConfig)
	.config(iconProviderConfig)
	.config(httpProviderConfig)
	.name;
